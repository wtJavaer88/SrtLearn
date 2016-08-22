package srt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Environment;
import android.os.Message;
import android.util.Log;
import app.ToastUtil;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.srt.SrtSetting;
import com.wnc.srtlearn.srt.SrtVoiceHelper;
import com.wnc.srtlearn.ui.SrtActivity;
import common.MyFileUtil;
import common.TextFormatUtil;

public class SrtPlayService
{
    private Thread autoPlayThread;
    private boolean replayCtrl = false;// 复读模式
    boolean autoPlayNextCtrl = true;// 如果播放过程出异常,就不能单靠系统设置的值控制自动播放下一个了,
    private SrtActivity srtActivity;
    private int beginReplayIndex = -1;
    private int endReplayIndex = -1;
    final String favoriteTxt = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/favorite.txt";
    final String thumbPicFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/图片/";
    // 文件夹名称最大只取12位
    final int FOLDER_NAME_MAXLEN = 12;
    final int DELTA_UNIQUE = 1000;// 文件夹和所属文件的Map的Key规则
    private Map<Integer, String> srtFilePathes = new HashMap<Integer, String>();
    List<File> tvFolders = null;

    public SrtPlayService(SrtActivity srtActivity)
    {
        this.srtActivity = srtActivity;

    }

    public void favorite()
    {
        if (BasicFileUtil.writeFileString(favoriteTxt,
                getFavoriteCurrContent(), "UTF-8", true))
        {
            ToastUtil.showLongToast(srtActivity, "收藏成功!");
        }
        else
        {
            ToastUtil.showLongToast(srtActivity, "收藏失败!");
        }
    }

    public String getThumbPicPath()
    {
        String filePath = thumbPicFolder
                + getCurFile().replace(srtActivity.srtFolder, "");
        int i = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, i);
        File picFolder = new File(filePath);
        System.out.println("picFolder..." + picFolder);
        if (picFolder.exists())
        {
            System.out.println("exist....");
            filePath = filePath + File.separator
                    + TextFormatUtil.getFileNameNoExtend(getCurFile())
                    + "_p1.pic";
            if (!BasicFileUtil.isExistFile(filePath))
            {
                if (picFolder.listFiles().length > 0)
                {
                    filePath = picFolder.listFiles()[0].getAbsolutePath();
                }
            }
        }
        else
        {
            filePath = "";
        }
        System.out.println("filePath:" + filePath);
        return filePath;
    }

    public SrtInfo getSrtInfo(SRT_VIEW_TYPE view_type)
    {
        SrtInfo srt = null;
        switch (view_type)
        {
        case VIEW_FIRST:
            srt = DataHolder.getFirst();
            break;
        case VIEW_LAST:
            srt = DataHolder.getLast();
            break;
        case VIEW_LEFT:
            srt = DataHolder.getPre();
            break;
        case VIEW_RIGHT:
            srt = DataHolder.getNext();
            break;
        case VIEW_CURRENT:
            srt = DataHolder.getCurrent();
            break;
        }
        return srt;
    }

    public String getPleyProgress()
    {
        final List<SrtInfo> list = DataHolder.map.get(getCurFile());
        if (list == null)
        {
            return "";
        }
        return "(" + (getCurIndex() + 1) + "/" + list.size() + ")";
    }

    public int getCurIndex()
    {
        return DataHolder.getCurrentSrtIndex();
    }

    public String getCurFile()
    {
        return DataHolder.getFileKey();
    }

    public String getFavoriteCurrContent()
    {
        return BasicDateUtil.getCurrentDateTimeString() + " \""
                + getCurFile().replace(srtActivity.srtFolder, "") + "\" "
                + getCurrentPlaySrtInfo() + "\r\n";
    }

    public void showNewSrtFile(String srtFile)
    {
        System.out.println("srtFile:" + srtFile);
        if (BasicFileUtil.isExistFile(srtFile))
        {
            srtActivity.stopSrtPlay();
            DataHolder.switchFile(srtFile);
            if (!DataHolder.map.containsKey(srtFile))
            {
                SrtFileDataHelper.dataEntity(getCurFile());
                srtActivity.getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_RIGHT);
            }
            else
            {
                srtActivity.getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_CURRENT);
            }
        }
        else
        {
            Log.e("srt", "not found " + srtFile);
        }
    }

    /**
     * 控制切换是否复读,快捷设置仅复读本句
     */
    public void switchReplayModel()
    {
        this.setReplayCtrl(isReplayCtrl() ? false : true);
        if (isReplayCtrl())
        {
            setReplayIndex(getCurIndex(), getCurIndex());
        }
        ToastUtil.showShortToast(srtActivity, isReplayCtrl() ? "复读" : "不复读");
    }

    public void stopReplayModel()
    {
        this.setReplayCtrl(false);
        setReplayIndex(-1, -1);
    }

    public void setReplayIndex(int bIndex, int eIndex)
    {
        if (bIndex > eIndex)
        {
            ToastUtil.showLongToast(srtActivity, "结束时间不能小于开始时间!");
        }
        else
        {
            setBeginReplayIndex(bIndex);
            setEndReplayIndex(eIndex);
        }
    }

    /**
     * 检查复读模式是否失效:在复读的时候,如果翻页的范围超出了复读范围
     * <p>
     * 注意不能通过取反来表示复读有效
     * 
     * @return
     */
    public boolean isReplayInvalid()
    {
        return isReplayCtrl()
                && (getCurIndex() < getBeginReplayIndex() || getCurIndex() > getEndReplayIndex());
    };

    /**
     * 复读模式是否在进行中
     * 
     * @return
     */
    public boolean isReplayRunning()
    {
        return isReplayCtrl()
                && (getCurIndex() >= getBeginReplayIndex() && getCurIndex() <= getEndReplayIndex());
    };

    public void playSrt()
    {
        // 每次播放,先设置自动播放控制为true
        autoPlayNextCtrl = true;
        // 停止原有的播放线程,播放新字幕
        stopSrtPlayThread();
        autoPlayThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Message msg = new Message();
                long time = TimeHelper.getTime(DataHolder.getCurrent()
                        .getToTime())
                        - TimeHelper.getTime(DataHolder.getCurrent()
                                .getFromTime());
                try
                {
                    if (SrtSetting.isPlayVoice())
                    {
                        String voicePath = SrtTextHelper.getSrtVoiceLocation();
                        if (BasicFileUtil.isExistFile(voicePath))
                        {
                            SrtVoiceHelper.play(voicePath);
                        }
                    }
                    Thread.sleep(time);
                    srtActivity.reveiveMsg(msg);
                }
                catch (InterruptedException e)
                {
                    // e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        getAutoPlayThread().start();
    }

    public void stopSrt()
    {
        stopSrtPlayThread();
        autoPlayThread = null;
        autoPlayNextCtrl = false;
    }

    /**
     * 停止原来的字幕自动播放
     */
    private void stopSrtPlayThread()
    {
        if (getAutoPlayThread() != null)
        {
            getAutoPlayThread().interrupt();
        }
    }

    public boolean isAutoPlayModel()
    {
        return autoPlayNextCtrl && SrtSetting.isAutoPlayNext();
    }

    public boolean isSrtShowing()
    {
        if (BasicFileUtil.isExistFile(getCurFile()))
        {
            return true;
        }
        ToastUtil.showShortToast(srtActivity, "没有选择任何剧集");
        return false;
    }

    public Thread getAutoPlayThread()
    {
        return autoPlayThread;
    }

    public boolean isReplayCtrl()
    {
        return replayCtrl;
    }

    public void setReplayCtrl(boolean replayCtrl)
    {
        this.replayCtrl = replayCtrl;
    }

    public int getEndReplayIndex()
    {
        return endReplayIndex;
    }

    public void setEndReplayIndex(int endReplayIndex)
    {
        this.endReplayIndex = endReplayIndex;
    }

    public int getBeginReplayIndex()
    {
        return beginReplayIndex;
    }

    public void setBeginReplayIndex(int beginReplayIndex)
    {
        this.beginReplayIndex = beginReplayIndex;
    }

    public String[] getDirs()
    {
        if (tvFolders == null)
        {
            tvFolders = getFolderFiles();
        }
        int tvs = tvFolders.size();
        final String[] leftArr = new String[tvs];
        int i = 0;
        for (File folder : tvFolders)
        {
            leftArr[i++] = BasicStringUtil.subString(folder.getName(), 0,
                    FOLDER_NAME_MAXLEN);
        }
        return leftArr;
    }

    public String[][] getDirsFiles()
    {
        if (tvFolders == null)
        {
            tvFolders = getFolderFiles();
        }
        int tvs = tvFolders.size();
        String[][] rightArr = new String[tvs][];
        int i = 0;
        for (File folder : tvFolders)
        {
            File[] listFiles = folder.listFiles();
            List<File> fileList = MyFileUtil.getSortFiles(listFiles);
            List<String> srtList = new ArrayList<String>();
            int j = 0;
            for (File f2 : fileList)
            {
                if (SrtTextHelper.isSrtfile(f2))
                {
                    srtFilePathes.put(DELTA_UNIQUE * i + j,
                            f2.getAbsolutePath());
                    // 文件名最大只取8位
                    srtList.add(BasicStringUtil.subString(
                            TextFormatUtil.getFileNameNoExtend(f2.getName()),
                            0, 8));
                    j++;
                }
            }
            rightArr[i] = srtList.toArray(new String[srtList.size()]);
            i++;
        }
        return rightArr;
    }

    private List<File> getFolderFiles()
    {
        tvFolders = new ArrayList<File>();
        File srtFolderFile = new File(srtActivity.srtFolder);
        for (File f : MyFileUtil.getSortFiles(srtFolderFile.listFiles()))
        {
            if (f.isDirectory())
            {
                tvFolders.add(f);
            }
        }
        return tvFolders;
    }

    public String getSrtFileByArrIndex(int dIndex, int fIndex)
    {
        return srtFilePathes.get(DELTA_UNIQUE * dIndex + fIndex);
    }

    public List<SrtInfo> getCurrentPlaySrtInfo()
    {
        if (isReplayRunning())
        {
            return getSrtInfos(beginReplayIndex, endReplayIndex);
        }
        else
        {
            return Arrays.asList(DataHolder.getCurrent());
        }
    }

    public List<SrtInfo> getReplaySrtInfos()
    {
        return getSrtInfos(beginReplayIndex, endReplayIndex);
    }

    /**
     * 获取一段区间内的字幕信息
     * 
     * @param bIndex
     * @param eIndex
     * @return
     */
    public List<SrtInfo> getSrtInfos(int bIndex, int eIndex)
    {
        List<SrtInfo> list = new ArrayList<SrtInfo>();
        List<SrtInfo> currentSrtInfos = DataHolder.getCurrentSrtInfos();
        if (currentSrtInfos != null)
        {
            for (int i = bIndex; i <= eIndex; i++)
            {
                list.add(currentSrtInfos.get(i));
            }
        }
        return list;
    }
}
