package srt;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import srt.ex.SrtErrCode;
import srt.ex.SrtException;

import com.wnc.srtlearn.dao.SrtInfoDao;
import com.wnc.srtlearn.modules.srt.Favoritable;
import com.wnc.srtlearn.modules.srt.FavoriteMgr;
import com.wnc.srtlearn.modules.srt.SrtVoiceHelper;
import com.wnc.srtlearn.setting.SrtSetting;
import com.wnc.srtlearn.ui.SBaseLearnActivity;
import common.app.ToastUtil;

public class SrtPlayService implements Favoritable
{
    private PlayThread playThread;
    private boolean replayCtrl = false;// 复读模式
    public boolean autoPlayNextCtrl = true;// 如果播放过程出异常,就不能单靠系统设置的值控制自动播放下一个了,
    public SBaseLearnActivity sBaseLearnActivity;
    private int beginReplayIndex = -1;
    private int endReplayIndex = -1;

    // 两个音频间的播放延迟
    final int VOICE_PLAY_DELAY = 200;

    public SrtPlayService(SBaseLearnActivity sBaseLearnActivity)
    {
        this.sBaseLearnActivity = sBaseLearnActivity;
    }

    public void favorite() throws SrtException
    {
        List<SrtInfo> currentPlaySrtInfos = getCurrentPlaySrtInfos();
        FavoriteMgr favoriteMgr = new FavoriteMgr(this, sBaseLearnActivity);
        if (favoriteMgr.save(currentPlaySrtInfos))
        {
            ToastUtil.showLongToast(sBaseLearnActivity, "收藏成功!");
        }
        else
        {
            ToastUtil.showLongToast(sBaseLearnActivity, "收藏失败!");
        }
    }

    public SrtInfo getSrtInfo(SRT_VIEW_TYPE view_type) throws SrtException
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

    @Override
    public String getCurFile()
    {
        return DataHolder.getFileKey();
    }

    public void showNewSrtFile(String srtFile) throws SrtException
    {
        // 优先从数据库取数
        final String tmp = SrtTextHelper.getSxFile(srtFile).replace(" ", "");
        if (SrtInfoDao.isExistEpidose(tmp))
        {
            srtFile = tmp;
        }
        this.setReplayCtrl(false);
        this.setReplayIndex(-1, -1);
        System.out.println("srtFile:" + srtFile);
        DataHolder.switchFile(srtFile);
        if (!DataHolder.srtInfoMap.containsKey(srtFile))
        {
            final DataParseThread dataParseThread = new DataParseThread(srtFile);
            dataParseThread.start();
            beginDataListening(dataParseThread);
        }
        else
        {
            sBaseLearnActivity.play(getSrtInfo(SRT_VIEW_TYPE.VIEW_CURRENT));
        }
    }

    /**
     * 进入数据层开始解析的同时,服务层开始监听数据的进度,并向显示层发反馈消息
     * 
     * @param dataParseThread
     */
    private void beginDataListening(final DataParseThread dataParseThread)
    {
        new Thread(new Runnable()
        {
            boolean watching = true;
            boolean hasCached = false;

            @Override
            public void run()
            {
                while (watching)
                {
                    int runState = dataParseThread.getRunState();
                    if (runState != 0)
                    {
                        switch (runState)
                        {
                        case 1:
                            if (!hasCached)
                            {
                                sBaseLearnActivity
                                        .getBackGroundHanlder()
                                        .sendEmptyMessage(
                                                SBaseLearnActivity.MESSAGE_GET_CACHED_SRT);
                                hasCached = true;
                            }
                            break;
                        case 2:
                            if (hasCached)
                            {
                                sBaseLearnActivity
                                        .getBackGroundHanlder()
                                        .sendEmptyMessage(
                                                SBaseLearnActivity.MESSAGE_GET_ALL_SRT_PLAYED);
                            }
                            else
                            {
                                sBaseLearnActivity
                                        .getBackGroundHanlder()
                                        .sendEmptyMessage(
                                                SBaseLearnActivity.MESSAGE_GET_ALL_SRT_UNPLAYED);
                            }
                            watching = false;
                            break;
                        default:
                            sBaseLearnActivity
                                    .getBackGroundHanlder()
                                    .sendEmptyMessage(
                                            SBaseLearnActivity.MESSAGE_GET_ERROR_SRT);
                            watching = false;
                            break;
                        }
                    }
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 指定一个时间点,开始搜索字幕
     * 
     * @param srtFile
     * @param seekTimeStr
     * @throws SrtException
     */
    public void seekSrtFile(String srtFile, String seekTimeStr)
            throws SrtException
    {
        this.setReplayCtrl(false);
        this.setReplayIndex(-1, -1);
        System.out.println("srtFile:" + srtFile);
        sBaseLearnActivity.stopSrtPlay();
        DataHolder.switchFile(srtFile);
        if (!DataHolder.srtInfoMap.containsKey(srtFile))
        {
            final DataParseThread dataParseThread = new DataParseThread(
                    getCurFile(), seekTimeStr);
            dataParseThread.start();
            beginDataListening(dataParseThread);
        }
        else
        {
            SrtInfo curPlaySrtInfo = null;
            for (int i = 0; i < DataHolder.getAllSrtInfos().size(); i++)
            {
                SrtInfo srtInfo = DataHolder.getAllSrtInfos().get(i);
                if (srtInfo.getFromTime().toString().compareTo(seekTimeStr) >= 0)
                {
                    curPlaySrtInfo = srtInfo;
                    DataHolder.setCurrentSrtIndex(i);
                    break;
                }
            }
            if (curPlaySrtInfo == null)
            {
                throw new SrtException(SrtErrCode.SRT_OUTOF_RANGE);
            }
            else
            {
                sBaseLearnActivity.play(curPlaySrtInfo);
            }
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
        ToastUtil.showShortToast(sBaseLearnActivity, isReplayCtrl() ? "复读"
                : "不复读");
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
            ToastUtil.showLongToast(sBaseLearnActivity, "结束时间不能小于开始时间!");
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
        // 停止原有的播放线程,播放新字幕
        stopSrt();
        // 每次播放,先设置自动播放控制为true
        autoPlayNextCtrl = true;
        playThread = new PlayThread(this);
        playThread.start();
    }

    public void stopSrt()
    {
        SrtVoiceHelper.stop();
        if (playThread != null)
        {
            playThread.threadRunning = false;
            playThread = null;
        }
        autoPlayNextCtrl = false;
    }

    public boolean isAutoPlayModel()
    {
        return autoPlayNextCtrl && SrtSetting.isAutoPlayNext();
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

    public List<SrtInfo> getCurrentPlaySrtInfos() throws SrtException
    {
        if (isReplayRunning())
        {
            return DataHolder.getSrtInfos(beginReplayIndex, endReplayIndex);
        }
        else
        {
            return Arrays.asList(DataHolder.getCurrent());
        }
    }

    @Override
    public String getFavTag()
    {
        String tag = "tag<";
        if (isReplayRunning())
        {
            tag += "replay";
        }
        else
        {
            tag += "normal";
        }

        tag += ">";
        return tag;
    }
}
