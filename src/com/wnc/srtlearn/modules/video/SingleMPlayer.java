package com.wnc.srtlearn.modules.video;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class SingleMPlayer
{
    public static MediaPlayer getMp(String path)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置视频流类型
        try
        {

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnCompletionListener(new OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                }
            });

            mediaPlayer.setOnErrorListener(new OnErrorListener()
            {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra)
                {
                    return false;
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mediaPlayer;
    }
}
