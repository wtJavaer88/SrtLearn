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

import com.wnc.basic.BasicNumberUtil;
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
    private static final int PLAY_SLEEP_TIME = 50;
    private static final int SRT_PAUSE_CODE = 100;
    private static final int ON_PLAYING_CODE = 101;
    private SurfaceView surfaceView;
    private Button button_pause, button_replay, button_custom_replay;
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
        button_replay = (Button) findViewById(R.id.button_replay);
        button_custom_replay = (Button) findViewById(R.id.button_replay_custom);

        veng_tv = (TextView) findViewById(R.id.veng_tv);
        vchs_tv = (TextView) findViewById(R.id.vchs_tv);

        surfaceView = (SurfaceView) findViewById(R.id.sv);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                System.out.println("onStopTrackingTouch");
                updateUI(seekBar.getProgress());
                initSeekTimes();
                play(seektime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                System.out.println("onStartTrackingTouch");
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
        button_replay.setOnClickListener(this);
        button_custom_replay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

        case R.id.button_pause:
            playpause();
            break;

        case R.id.button_replay:
            replay();
            break;

        case R.id.button_replay_custom:
            cusReplay();
            break;
        default:
            break;
        }
    }

    private void cusReplay()
    {
        isCusReplay = true;
        this.button_pause.setText("暂停");
        int endIndex = curIndex + 2;
        endIndex = endIndex >= srtInfos.size() ? srtInfos.size() : endIndex;
        seekendtime = (int) TimeHelper.getTime(srtInfos.get(endIndex)
                .getToTime());
        play(seektime);
    }

    private void replay()
    {
        isCusReplay = false;
        this.button_pause.setText("暂停");
        isShowingSrt = true;
        isPaused = false;
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.seekTo(seektime);
        }
        else
        {
            play(seektime);
        }
    }

    private void stopPlay()
    {
        isCusReplay = false;
        isPlaying = false;
        isPaused = false;
        isShowingSrt = false;
        if (mediaPlayer != null)
        {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playpause()
    {
        isCusReplay = false;
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
                isPaused = true;
                isShowingSrt = false;
                mediaPlayer.pause();
            }
            else
            {
                if (isPausedModel())
                {
                    // 暂停,但字幕不在更新或播放. 这种情况下,继续播放
                    isPaused = false;
                    mediaPlayer.start();
                }
                else
                {
                    isPaused = false;
                    isPlaying = false;
                    play(seektime);
                }

            }
        }
    }

    boolean isPaused = false;// 只在暂停时为true
    boolean isShowingSrt = false;// 字幕更新或播放时为true,暂停停止时为false
    boolean isCusReplay = false;

    private boolean isPausedModel()
    {
        return isPaused && !isShowingSrt;
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SRT_PAUSE_CODE)
            {
                playpause();
            }
            else if (msg.what == ON_PLAYING_CODE)
            {
                if (!isCusReplay)
                {
                    updateUI(BasicNumberUtil.getNumber("" + msg.obj));
                    initSeekTimes();
                }
                else
                {
                    // 这儿不更新curIndex
                    updateReplayUI(BasicNumberUtil.getNumber("" + msg.obj));
                }
                System.out.println(seekendtime);
            }
        }
    };

    /**
     * 播放指定位置的字幕
     * 
     * @param currentPosition
     */
    private void play(final int currentPosition)
    {
        this.button_pause.setText("暂停");
        isShowingSrt = true;
        isPaused = false;
        isPlaying = false;
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
                    seekBar.setProgress(currentPosition);
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

                                if (position > seekendtime && isShowingSrt)
                                {
                                    Message msg = new Message();
                                    msg.what = SRT_PAUSE_CODE;
                                    handler.sendMessage(msg);
                                }
                                else
                                {
                                    seekBar.setProgress(position);

                                    if (!isPaused && position > currentPosition)
                                    {
                                        // 找出当前播放处的字幕
                                        Message msg2 = new Message();
                                        msg2.what = ON_PLAYING_CODE;
                                        msg2.obj = position;
                                        handler.sendMessage(msg2);
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

    private void updateReplayUI(int position)
    {
        for (int i = 0; i < srtInfos.size(); i++)
        {
            SrtInfo srt = srtInfos.get(i);
            if (TimeHelper.getTime(srt.getToTime()) >= position
                    && TimeHelper.getTime(srt.getFromTime()) <= position)
            {
                if (i != curIndex)
                {
                    curSrt = srtInfos.get(i);
                    setUI();
                }
                break;
            }
        }
    };

    private void updateUI(int position)
    {
        for (int i = 0; i < srtInfos.size(); i++)
        {
            SrtInfo srt = srtInfos.get(i);
            if (TimeHelper.getTime(srt.getToTime()) >= position
                    && TimeHelper.getTime(srt.getFromTime()) <= position)
            {
                if (i != curIndex)
                {
                    curIndex = i;
                    curSrt = srtInfos.get(curIndex);
                    setUI();
                }
                break;
            }
        }
    };

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
        isShowingSrt = true;
        curIndex--;
        if (curIndex < 0)
        {
            curIndex++;
        }
        curSrt = srtInfos.get(curIndex);
        setUI();
        initSeekTimes();
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            play(seektime);
        }
    }

    private void setUI()
    {
        setSrtContent(curSrt);
    }

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        isShowingSrt = true;
        curIndex++;
        if (curIndex == srtInfos.size())
        {
            curIndex--;
        }
        curSrt = srtInfos.get(curIndex);
        setUI();
        initSeekTimes();
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            play(seektime);
        }
    }

    private void initSeekTimes()
    {
        if (curSrt != null)
        {
            seektime = (int) TimeHelper.getTime(curSrt.getFromTime());
            seekendtime = (int) TimeHelper.getTime(curSrt.getToTime());
        }
    }

    @Override
    public void onDestroy()
    {
        stopPlay();
        super.onDestroy();
    }
}
