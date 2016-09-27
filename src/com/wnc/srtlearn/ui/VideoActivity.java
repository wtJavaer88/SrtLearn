package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.R;

/**
 * 使用SurfaceView和MediaPlayer的本地视频播放器。
 * 
 */
public class VideoActivity extends BaseVerActivity implements OnClickListener,
        UncaughtExceptionHandler
{
    private SurfaceView surfaceView;
    private Button button_play, button_pause, button_stop, button_replay;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private EditText seekTv;
    private int currentPosition;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videotest);
        Thread.setDefaultUncaughtExceptionHandler(this);

        init();

        initData();
    }

    private void init()
    {
        button_play = (Button) findViewById(R.id.button_play);
        button_pause = (Button) findViewById(R.id.button_pause);
        button_stop = (Button) findViewById(R.id.button_stop);
        button_replay = (Button) findViewById(R.id.button_replay);
        seekTv = (EditText) findViewById(R.id.seekTv);

        surfaceView = (SurfaceView) findViewById(R.id.sv);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                int process = seekBar.getProgress();
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(process);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {

            }
        });
    }

    private void initData()
    {
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("seekto") != null)
        {
            seekTv.setText(intent.getStringExtra("seekto"));
        }
        mediaPlayer = new MediaPlayer();
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 4.0一下的版本需要加该段代码。

        surfaceView.getHolder().addCallback(new Callback()
        {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                /**
                 * 当点击手机上home键（或其他使SurfaceView视图消失的键）时，调用该方法，获取到当前视频的播放值，
                 * currentPosition。 并停止播放。
                 */
                currentPosition = mediaPlayer.getCurrentPosition();
                stop();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                /**
                 * 当重新回到该视频应当视图的时候，调用该方法，获取到currentPosition，
                 * 并从该currentPosition开始继续播放。
                 */
                if (currentPosition > 0)
                {
                    play(currentPosition);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height)
            {

            }
        });

        button_play.setOnClickListener(this);
        button_pause.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        button_replay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.button_play:
            play(BasicNumberUtil.getNumber(seekTv.getText().toString()));
            break;

        case R.id.button_pause:
            pause();
            break;

        case R.id.button_stop:
            stop();
            break;

        case R.id.button_replay:
            replay();
            break;

        default:
            break;
        }
    }

    private void replay()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.seekTo(0);
        }
        else
        {
            play(0);
        }
    }

    private void stop()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.seekTo(0);
        }

    }

    private void pause()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        else
        {
            mediaPlayer.start();
        }

    }

    private void play(final int currentPosition)
    {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/wnc/Friends.S01E02.avi";
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置视频流类型
        try
        {

            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new OnPreparedListener()
            {

                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mediaPlayer.start();
                    int max = mediaPlayer.getDuration();
                    seekBar.setMax(max);
                    mediaPlayer.seekTo(currentPosition);

                    new Thread()
                    {

                        @Override
                        public void run()
                        {
                            isPlaying = true;
                            while (isPlaying)
                            {
                                int position = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(position);
                                try
                                {
                                    Thread.sleep(500);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                        };
                    }.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new OnCompletionListener()
            {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    button_play.setEnabled(true);
                }
            });

            mediaPlayer.setOnErrorListener(new OnErrorListener()
            {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra)
                {
                    button_play.setEnabled(true);
                    return false;
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }

}
