package srt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.srtlearn.srt.SrtSetting;
import com.wnc.srtlearn.srt.SrtVoiceHelper;
import com.wnc.srtlearn.ui.SrtActivity;
import common.app.ToastUtil;

public class SrtPlayService
{
    private Thread playThread;
    private boolean replayCtrl = false;// 复读模式
    boolean autoPlayNextCtrl = true;// 如果播放过程出异常,就不能单靠系统设置的值控制自动播放下一个了,
    private SrtActivity srtActivity;
    private int beginReplayIndex = -1;
    private int endReplayIndex = -1;
    final String FAVORITE_TXT = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srtlearn/favorite.txt";
    final String SRT_FOLDER = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/res/srt/";

    // 两个音频间的播放延迟
    final int VOICE_PLAY_DELAY = 200;

    public SrtPlayService(SrtActivity srtActivity)
    {
        this.srtActivity = srtActivity;
    }

    public void favorite()
    {
        if (BasicFileUtil.writeFileString(FAVORITE_TXT,
                getFavoriteCurrContent(), "UTF-8", true))
        {
            ToastUtil.showLongToast(srtActivity, "收藏成功!");
        }
        else
        {
            ToastUtil.showLongToast(srtActivity, "收藏失败!");
        }
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
        final List<SrtInfo> list = DataHolder.srtInfoMap.get(getCurFile());
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
                + getCurFile().replace(SRT_FOLDER, "") + "\" "
                + getCurrentPlaySrtInfos() + "\r\n";
    }

    public void showNewSrtFile(String srtFile)
    {
        this.setReplayCtrl(false);
        this.setReplayIndex(-1, -1);
        System.out.println("srtFile:" + srtFile);
        if (BasicFileUtil.isExistFile(srtFile))
        {
            srtActivity.stopSrtPlay();
            DataHolder.switchFile(srtFile);
            if (!DataHolder.srtInfoMap.containsKey(srtFile))
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
        stopPlayThread();
        // 每次播放,先设置自动播放控制为true
        autoPlayNextCtrl = true;
        // 停止原有的播放线程,播放新字幕
        playThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Message msg = new Message();
                long time = VOICE_PLAY_DELAY
                        + TimeHelper.getTime(DataHolder.getCurrent()
                                .getToTime())
                        - TimeHelper.getTime(DataHolder.getCurrent()
                                .getFromTime());
                try
                {
                    if (SrtSetting.isPlayVoice())
                    {
                        final String voicePath = SrtTextHelper
                                .getSrtVoiceLocation(DataHolder.getFileKey(),
                                        DataHolder.getCurrent().getFromTime()
                                                .toString());
                        if (BasicFileUtil.isExistFile(voicePath))
                        {
                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    SrtVoiceHelper.play(voicePath);
                                }
                            }).start();

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
        playThread.start();
    }

    public void stopSrt()
    {
        stopPlayThread();
        autoPlayNextCtrl = false;
    }

    /**
     * 停止原来的字幕自动播放
     */
    private void stopPlayThread()
    {
        if (playThread != null)
        {
            playThread.interrupt();
        }
        playThread = null;
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

    public boolean isRunning()
    {
        return playThread != null;
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

    public List<SrtInfo> getCurrentPlaySrtInfos()
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
        List<SrtInfo> currentSrtInfos = DataHolder.getAllSrtInfos();
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
