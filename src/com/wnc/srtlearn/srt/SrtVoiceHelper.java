package com.wnc.srtlearn.srt;

import java.io.File;
import java.io.FileInputStream;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SrtVoiceHelper
{
    static MediaPlayer player;
    static boolean isPlaying = false;

    public static void stop()
    {
        try
        {
            if (player != null)
            {
                player.reset();
                player.release();
                player = null;
                isPlaying = false;
            }
        }
        catch (Exception e)
        {
            player = null;
            isPlaying = false;
            System.out.println("voiceStopEx." + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void play(String voicePath)
    {
        try
        {
            stop();

            if (!isPlaying)
            {
                // System.out.println(voicePath);
                File file = new File(voicePath);
                FileInputStream fis = new FileInputStream(file);
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(fis.getFD());
                player.prepare();
                player.start();
                isPlaying = true;
                player.setOnCompletionListener(new OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                    }
                });
            }
        }
        catch (Exception e)
        {
            player = null;
            isPlaying = false;
            System.out.println("voicePlayEx." + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
