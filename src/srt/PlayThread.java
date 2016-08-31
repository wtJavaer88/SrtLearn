package srt;

import android.os.Message;

import com.wnc.basic.BasicFileUtil;
import com.wnc.srtlearn.srt.SrtSetting;
import com.wnc.srtlearn.srt.SrtVoiceHelper;

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

        long voiceDuration = VOICE_PLAY_DELAY
                + TimeHelper.getTime(DataHolder.getCurrent().getToTime())
                - TimeHelper.getTime(DataHolder.getCurrent().getFromTime());

        try
        {

            final String voicePath = SrtTextHelper.getSrtVoiceLocation(
                    DataHolder.getFileKey(), DataHolder.getCurrent()
                            .getFromTime().toString());
            if (BasicFileUtil.isExistFile(voicePath)
                    && SrtSetting.isPlayVoice())
            {
                SrtVoiceHelper.play(voicePath);
            }
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
}
