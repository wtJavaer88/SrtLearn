package com.wnc.srtlearn.modules.video;

import android.os.Message;

import com.wnc.srtlearn.ui.VideoActivity;

public class VideoPlayThread extends Thread
{
    private volatile boolean isPlaying;
    private static final int PLAY_SLEEP_TIME = 100;
    VideoActivity activity;

    public VideoPlayThread(VideoActivity activity)
    {
        this.activity = activity;
    }

    public void stopPlay()
    {
        isPlaying = false;
    }

    @Override
    public void run()
    {
        isPlaying = true;
        while (isPlaying)
        {
            int position = activity.getMediaPlayer().getCurrentPosition();
            boolean isover = curOver(position);
            if (isover && (activity.isCusReplay() || activity.onlyOneSrt))
            {
                activity.getHandler().sendEmptyMessage(
                        activity.MESSAGE_SRT_AUTOPAUSE_CODE);
            }
            else
            {
                activity.seekBar.setProgress(position);

                if (!activity.isPaused && position > activity.seektime)
                {
                    // 找出当前播放处的字幕
                    Message msg2 = new Message();
                    msg2.what = activity.MESSAGE_ON_PLAYING_CODE;
                    msg2.obj = position;
                    activity.getHandler().sendMessage(msg2);
                }
            }
            try
            {
                Thread.sleep(PLAY_SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }

    private boolean curOver(int position)
    {
        return position > activity.seekendtime && !activity.isPausedModel();
    }

}