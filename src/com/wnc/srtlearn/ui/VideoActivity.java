package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import srt.DataHolder;
import srt.SrtInfo;
import srt.TimeHelper;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wnc.srtlearn.R;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

/**
 * 使用SurfaceView和MediaPlayer的本地视频播放器。
 * 
 */
public class VideoActivity extends BaseVerActivity implements OnClickListener,
        UncaughtExceptionHandler, CtrlableHorGestureDetectorListener
{
    private static final int SRT_PAUSE = 100;
    private SurfaceView surfaceView;
    private Button button_pause, button_stop, button_replay;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private GestureDetector gestureDetector;
    private TextView veng_tv;
    private TextView vchs_tv;

    private List<SrtInfo> srtInfos = DataHolder.getAllSrtInfos();
    private int curIndex = DataHolder.getCurrentSrtIndex();
    private SrtInfo curSrt;
    // private EditText seekTv;
    private int seektime = 0;
    private int seekendtime = 0;

    private int currentPosition;
    private volatile boolean isPlaying;

    private boolean firstPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videotest);
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.2, 0, this, null));
        init();

        initData();
    }

    private void init()
    {
        button_pause = (Button) findViewById(R.id.button_pause);
        button_stop = (Button) findViewById(R.id.button_stop);
        button_replay = (Button) findViewById(R.id.button_replay);
        veng_tv = (TextView) findViewById(R.id.veng_tv);
        vchs_tv = (TextView) findViewById(R.id.vchs_tv);

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

        if (intent != null)
        {
            seektime = intent.getIntExtra("seekfrom", 0);
            seekendtime = intent.getIntExtra("seekto", 0);
            if (intent.getStringExtra("eng") != null)
            {
                this.veng_tv.setText(intent.getStringExtra("eng"));

            }
            if (intent.getStringExtra("chs") != null)
            {
                this.vchs_tv.setText(intent.getStringExtra("chs"));

            }
        }

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
                stopPlay();
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

        button_pause.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        button_replay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

        case R.id.button_pause:
            playpause();
            break;

        case R.id.button_stop:
            stopPlay();
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
            mediaPlayer.seekTo(seektime);
        }
        else
        {
            isPlaying = false;
            play(seektime);
        }
    }

    private void stopPlay()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.seekTo(0);
            isPlaying = false;
        }
    }

    private void playpause()
    {
        String tip = button_pause.getText().toString();
        this.button_pause.setText(tip.equals("播放") ? "暂停" : "播放");
        if (firstPlay)
        {
            play(seektime);
            firstPlay = false;
        }
        else
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
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SRT_PAUSE)
            {
                playpause();
            }
        }
    };

    private void play(final int currentPosition)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
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
                                if (position > seekendtime)
                                {
                                    if (mediaPlayer.isPlaying())
                                    {
                                        Message msg = new Message();
                                        msg.what = SRT_PAUSE;
                                        handler.sendMessage(msg);
                                    }
                                }
                                else
                                {
                                    seekBar.setProgress(position);
                                }
                                try
                                {
                                    Thread.sleep(100);
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
    }

    private void setSrtContent(SrtInfo srt)
    {
        veng_tv.setText(srt.getEng() == null ? "NULL" : srt.getEng());
        vchs_tv.setText(srt.getChs() == null ? "NULL" : srt.getChs());
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
        if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
        {
            return super.dispatchTouchEvent(paramMotionEvent);
        }
        return true;
    }

    @Override
    public void doLeft(FlingPoint p1, FlingPoint p2)
    {
        curIndex--;
        if (curIndex < 0)
        {
            curIndex++;
        }
        work();
    }

    private void work()
    {
        curSrt = srtInfos.get(curIndex);
        setSrtContent(curSrt);
        seektime = (int) TimeHelper.getTime(curSrt.getFromTime());
        seekendtime = (int) TimeHelper.getTime(curSrt.getToTime());
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            play(seektime);
        }
    }

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        curIndex++;
        if (curIndex == srtInfos.size())
        {
            curIndex--;
        }
        work();
    }

}
