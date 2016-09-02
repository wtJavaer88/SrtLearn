package srt;

import java.util.Queue;

import android.os.Message;

import com.wnc.srtlearn.modules.srt.SrtSetting;
import com.wnc.srtlearn.modules.srt.SrtVoiceHelper;

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
            while (threadRunning)
            {
                if (System.currentTimeMillis() - beginTime >= voiceDuration)
                {
                    // 正常结束
                    threadRunning = false;
                    Message msg = new Message();
                    msg.what = 1;
                    srtPlayService.srtActivity.getHanlder().sendMessage(msg);
                }
                else
                {
                    Thread.sleep(100);
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
            msg2.what = 2;
            srtPlayService.srtActivity.getHanlder().sendMessage(msg2);
        }
    }

    private long palyVoice()
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
        long voiceDuration = VOICE_PLAY_DELAY
                + TimeHelper.getTime(currentSrtInfo.getToTime())
                - TimeHelper.getTime(currentSrtInfo.getFromTime());

        try
        {
            if (!SrtSetting.isPlayVoice())
            {
                return voiceDuration;
            }
            else if (!SrtSetting.isPlayBgVoice() || srtVoicesWithBg.size() == 1)
            {
                SrtVoiceHelper.play(srtVoicesWithBg.peek());
                return voiceDuration;
            }

            if (srtVoicesWithBg.size() >= 2)
            {
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
                    if (!secondPath.contains(nextSrtInfo.getFromTime()
                            .toString().replace(":", "")))
                    {
                        final long l = TimeHelper.getTime(nextSrtInfo
                                .getFromTime())
                                - TimeHelper
                                        .getTime(currentSrtInfo.getToTime());
                        voiceDuration += l;
                    }
                    else
                    {
                        // 否则剔除,不要把下一段字幕的声音加进来了
                        srtVoicesWithBg.remove(1);
                    }
                }
                catch (RuntimeException ex)
                {
                    voiceDuration += 1000 * Mp3Utils.getTime(secondPath);
                }

            }

            if (SrtSetting.isPlayVoice())
            {
                SrtVoiceHelper.playInList(srtVoicesWithBg);
            }
        }
        catch (Exception e)
        {
        }

        return voiceDuration;
    }
}