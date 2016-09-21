package srt;

import java.util.Queue;

import srt.ex.SrtException;

import android.os.Message;

import com.wnc.srtlearn.modules.srt.SrtVoiceHelper;
import com.wnc.srtlearn.monitor.StudyMonitor;
import com.wnc.srtlearn.monitor.work.ActiveWork;
import com.wnc.srtlearn.monitor.work.WORKTYPE;
import com.wnc.srtlearn.setting.SrtSetting;
import com.wnc.srtlearn.ui.handler.AutoPlayHandler;
import common.utils.Mp3Utils;

public class PlayThread extends Thread
{// 两个音频间的播放延迟
    final int VOICE_PLAY_DELAY = 200;
    public volatile boolean threadRunning = true;
    SrtPlayService srtPlayService;

    public PlayThread(SrtPlayService srtPlayService)
    {
        this.srtPlayService = srtPlayService;
    }

    @Override
    public void run()
    {
        try
        {
            long voiceDuration = palyVoice();

            long beginTime = System.currentTimeMillis();
            final int SLEEP_TIME = 100;
            ActiveWork work = StudyMonitor.peekWork(WORKTYPE.SRT);
            while (threadRunning)
            {
                if (System.currentTimeMillis() - beginTime >= voiceDuration)
                {
                    // 默认听完声音才算一个
                    StudyMonitor.addActiveWork(work);
                    // 正常结束
                    threadRunning = false;
                    Message msg = new Message();
                    msg.what = AutoPlayHandler.NEXT_DIALOG;
                    srtPlayService.sBaseLearnActivity.getHanlder().sendMessage(
                            msg);
                }
                else
                {
                    Thread.sleep(SLEEP_TIME);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("PlayThread err:" + e.getMessage());
            e.printStackTrace();
            threadRunning = false;
            // 出现异常, 自动播放停止
            srtPlayService.autoPlayNextCtrl = false;
            // 通知UI,停止播放
            Message msg2 = new Message();
            msg2.what = AutoPlayHandler.PLAY_ERR;
            srtPlayService.sBaseLearnActivity.getHanlder().sendMessage(msg2);
        }
    }

    private long palyVoice() throws SrtException
    {
        // String voicePath = SrtTextHelper.getSrtVoiceLocation(
        // DataHolder.getFileKey(), DataHolder.getCurrent()
        // .getFromTime().toString());
        // if (BasicFileUtil.isExistFile(voicePath)
        // && SrtSetting.isPlayVoice())
        // {
        // SrtVoiceHelper.play(voicePath);
        // }
        final SrtInfo currentSrtInfo = DataHolder.getCurrent();
        Queue<String> srtVoicesWithBg = SrtTextHelper.getSrtVoicesInRange(
                DataHolder.getFileKey(), currentSrtInfo.getFromTime()
                        .toString(), currentSrtInfo.getToTime().toString());
        long curSrtduration = TimeHelper.getTime(currentSrtInfo.getToTime())
                - TimeHelper.getTime(currentSrtInfo.getFromTime());
        long voiceDuration = VOICE_PLAY_DELAY + curSrtduration;

        try
        {
            if (!SrtSetting.isPlayVoice())
            {
                if (srtVoicesWithBg.size() == 1)
                {
                    SrtVoiceHelper.play(srtVoicesWithBg.peek());
                }
                return voiceDuration;
            }

            if (!SrtSetting.isPlayBgVoice())
            {
                SrtVoiceHelper.play(srtVoicesWithBg.peek());
                return voiceDuration;
            }

            SrtVoiceHelper.playInList(srtVoicesWithBg);

            voiceDuration += getVoiceDuration(currentSrtInfo, srtVoicesWithBg);
        }
        catch (Exception e)
        {
        }

        return voiceDuration;
    }

    private long getVoiceDuration(final SrtInfo currentSrtInfo,
            Queue<String> srtVoicesWithBg) throws SrtException
    {
        if (srtVoicesWithBg.size() < 2)
        {
            return 0;
        }

        long addTime = 0;
        SrtInfo nextSrtInfo;
        String secondPath = "";
        for (String q : srtVoicesWithBg)
        {
            secondPath = q;
        }
        try
        {
            nextSrtInfo = DataHolder.getSrtInfoByIndex(DataHolder
                    .getCurrentSrtIndex() + 1);

            // 如果第二段是背景声音,则播放并累加时间
            if (!secondPath.contains(nextSrtInfo.getFromTime().toString()
                    .replace(":", "")))
            {
                final long l = TimeHelper.getTime(nextSrtInfo.getFromTime())
                        - TimeHelper.getTime(currentSrtInfo.getToTime());
                addTime = l;
            }
            else
            {
                // 否则剔除,不要把下一段字幕的声音加进来了
                srtVoicesWithBg.remove(1);
            }
        }
        catch (RuntimeException ex)
        {
            addTime = 1000 * Mp3Utils.getTime(secondPath);
        }
        return addTime;
    }
}