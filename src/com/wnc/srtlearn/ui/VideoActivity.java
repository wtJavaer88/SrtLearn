package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import net.widget.cqq.AddAndSubView;
import srt.DataHolder;
import srt.SrtInfo;
import srt.SrtTextHelper;
import srt.TimeHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.modules.video.MyVideoView;
import com.wnc.srtlearn.modules.video.MyVideoView.OnVideoClickLinster;
import com.wnc.srtlearn.modules.video.RelpayInfo;
import com.wnc.srtlearn.modules.video.SingleMPlayer;
import com.wnc.srtlearn.modules.video.VideoPlayThread;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableDoubleClickGestureDetectorListener;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

/**
 * 使用SurfaceView和MediaPlayer的本地视频播放器。
 * 
 */
public class VideoActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler, CtrlableHorGestureDetectorListener,
        CtrlableDoubleClickGestureDetectorListener
{
    private VideoPlayThread videoPlayThread;
    public static final int SRT_AUTOPAUSE_CODE = 100;
    public static final int ON_PLAYING_CODE = 101;
    private MyVideoView videoView;
    private Button button_replay_setting, button_custom_replay;
    private Button button_onlyone;
    private MediaPlayer mediaPlayer;
    public SeekBar seekBar;
    private GestureDetector gestureDetector;
    private TextView veng_tv;
    private TextView vchs_tv;
    private TextView tipTv;
    LinearLayout headLayout;
    private List<SrtInfo> srtInfos;
    private int curIndex = DataHolder.getCurrentSrtIndex();
    private SrtInfo curSrt;
    public int seektime = 0;
    public int seekendtime = 0;

    private int currentPosition;
    ImageButton imgButton_play;

    private String videoSeries;
    private String videoEpisode;
    View main;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        main = getLayoutInflater().from(this).inflate(
                R.layout.activity_videotest, null);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideVirtualBts();
        setContentView(main);

        Thread.setDefaultUncaughtExceptionHandler(this);
        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.2, 0, this, null)
                        .setDclistener(this));
        init();
        videoPlayThread = new VideoPlayThread(this);
        initData();
    }

    /**
     * 隐藏虚拟按键
     */
    @SuppressLint("NewApi")
    private void hideVirtualBts()
    {
        // 普通
        final int currentAPIVersion = BasicPhoneUtil
                .getCurrentAPIVersion(getApplicationContext());
        System.out.println("Level ........" + currentAPIVersion);
        if (currentAPIVersion < 19)
        {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        else
        {
            // 完全
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void init()
    {
        headLayout = (LinearLayout) findViewById(R.id.video_head);
        imgButton_play = (ImageButton) findViewById(R.id.imgbtn_play);
        button_onlyone = (Button) findViewById(R.id.button_onlyone);
        button_replay_setting = (Button) findViewById(R.id.button_replay_setting);
        button_custom_replay = (Button) findViewById(R.id.button_replay_custom);

        veng_tv = (TextView) findViewById(R.id.veng_tv);
        vchs_tv = (TextView) findViewById(R.id.vchs_tv);
        tipTv = (TextView) findViewById(R.id.tipTv);

        videoView = (MyVideoView) findViewById(R.id.sv);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                System.out.println("onStopTrackingTouch");
                updateUI(seekBar.getProgress());
                initSeekTimes();
                videoSeek(seektime);
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
        srtInfos = DataHolder.getAllSrtInfos();
        Intent intent = getIntent();

        if (intent != null)
        {
            String fileInfo = intent.getStringExtra("fileinfo");
            if (BasicStringUtil.isNotNullString(fileInfo))
            {
                this.videoSeries = PatternUtil.getFirstPatternGroup(fileInfo,
                        "(.*?)/").trim();
                this.videoEpisode = PatternUtil.getFirstPatternGroup(fileInfo,
                        "/(.*+)").trim();
                System.out.println(videoEpisode + " " + videoSeries);
            }
            seektime = intent.getIntExtra("seekfrom", 0);
            seekendtime = intent.getIntExtra("seekto", 0);
            curIndex = intent.getIntExtra("curindex", 0);
            curSrt = srtInfos.get(curIndex);
            System.out.println("curIndex:  " + curIndex);
            setUI();
            setTipTv(0);
        }

        currentPosition = seektime;
        videoView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 4.0一下的版本需要加该段代码。

        videoView.getHolder().addCallback(new Callback()
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
                    initHoldPlay();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height)
            {

            }
        });
        videoView.setOnVideoClickLinster(new OnVideoClickLinster()
        {

            @Override
            public void onDoubleClick()
            {
                playpause();
            }

            @Override
            public void onClick()
            {
                switchHead();
            }
        });
        // 横屏最大化
        videoView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        button_onlyone.setOnClickListener(this);
        imgButton_play.setOnClickListener(this);
        button_replay_setting.setOnClickListener(this);
        button_custom_replay.setOnClickListener(this);
    }

    /**
     * 主要是看看当画面,无其他目的,点击播放后又要重新new一个
     */
    private void initHoldPlay()
    {
        hideHead();
        isShowingSrt = true;
        isPaused = false;

        String path = SrtTextHelper.getVideoFile(MyAppParams.VIDEO_FOLDER,
                videoSeries, videoEpisode);
        mediaPlayer = SingleMPlayer.getMp(path);
        mediaPlayer.setDisplay(videoView.getHolder());
        mediaPlayer.setOnPreparedListener(new OnPreparedListener()
        {

            @Override
            public void onPrepared(MediaPlayer mp)
            {
                // mediaPlayer.start();
                int max = mediaPlayer.getDuration();
                seekBar.setProgress(currentPosition);
                seekBar.setMax(max);
                mediaPlayer.seekTo(currentPosition);
                videoPlayThread.start();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.button_onlyone:
            onlyOneSrt = onlyOneSrt ? false : true;
            if (onlyOneSrt)
            {
                ToastUtil.showShortToast(getApplicationContext(),
                        "当前为单个字幕播放模式!");
            }
            break;
        case R.id.imgbtn_play:
            hideHead();
            playpause();
            break;

        case R.id.button_replay_setting:
            replaySetting();
            break;

        case R.id.button_replay_custom:
            hideHead();
            cusReplay();
            break;

        default:
            break;
        }
    }

    /**
     * 实现屏幕的手动切换
     */
    private void screenChange()
    {
        if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        {
            Log.e("Video", "err");
        }

    }

    private void switchHead()
    {
        System.out.println("切换视频头");
        if (this.headLayout.getVisibility() == View.VISIBLE)
        {
            hideHead();
        }
        else
        {
            this.headLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideHead()
    {
        this.headLayout.setVisibility(View.GONE);
    }

    RelpayInfo replayInfo = new RelpayInfo();
    AlertDialog replaySettingDialog;

    private void replaySetting()
    {
        final AddAndSubView mView1 = new AddAndSubView(this);
        final AddAndSubView mView2 = new AddAndSubView(this);
        final AddAndSubView mView3 = new AddAndSubView(this);

        if (replaySettingDialog == null)
        {
            System.out.println("replayInfo:" + replayInfo);
            replaySettingDialog = new AlertDialog.Builder(this).create();
            replaySettingDialog.show();
            replaySettingDialog.getWindow().setGravity(Gravity.CENTER);
            replaySettingDialog.getWindow().setLayout(
                    (int) (MyAppParams.getScreenWidth() * 0.5),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT);
            replaySettingDialog.getWindow().setContentView(
                    R.layout.srt_replay_setting);

            LinearLayout mLayout1 = (LinearLayout) replaySettingDialog
                    .findViewById(R.id.layout_add_and_sub_count);
            mView1.setNum(2);
            mView1.setDeafultStyle();
            mLayout1.addView(mView1);
            LinearLayout mLayout2 = (LinearLayout) replaySettingDialog
                    .findViewById(R.id.layout_add_and_sub_preload);
            mView2.setNum(0);
            mView2.setNumStep(100);
            mView2.setDeafultStyle();
            mLayout2.addView(mView2);
            LinearLayout mLayout3 = (LinearLayout) replaySettingDialog
                    .findViewById(R.id.layout_add_and_sub_afterload);

            mView3.setNum(0);
            mView3.setNumStep(100);
            mView3.setDeafultStyle();
            mLayout3.addView(mView3);

            TextView btnOk = (TextView) replaySettingDialog
                    .findViewById(R.id.srt_replay_set_dialg_ok);
            TextView btnCancel = (TextView) replaySettingDialog
                    .findViewById(R.id.srt_replay_set_dialg_no);
            btnOk.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mView1.getNum() == 0)
                    {
                        ToastUtil.showShortToast(getApplicationContext(),
                                "字幕数目不能为0!");
                    }
                    else
                    {
                        replayInfo.setSrtcounts(mView1.getNum());
                        replayInfo.setPreLoad(mView2.getNum());
                        replayInfo.setAfterLoad(mView3.getNum());
                        replaySettingDialog.dismiss();
                    }
                }
            });
            btnCancel.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    replaySettingDialog.dismiss();
                }
            });
        }
        else
        {
            if (replayInfo.getSrtcounts() > 0)
            {
                mView1.setNum(replayInfo.getSrtcounts());
            }
            mView2.setNum(replayInfo.getPreLoad());
            mView3.setNum(replayInfo.getAfterLoad());
            replaySettingDialog.show();
        }
    }

    /**
     * 默认的话三个参数全为0, 只复读自己一句
     */
    private void cusReplay()
    {
        setCusReplay(true);
        this.imgButton_play.setImageResource(R.drawable.bfq_pause);
        int endIndex = curIndex + replayInfo.getSrtcounts() - 1;
        if (replayInfo.getSrtcounts() == 0)
        {
            endIndex = curIndex;
        }
        curSrt = srtInfos.get(curIndex);
        setSrtContent(curSrt);

        endIndex = endIndex >= srtInfos.size() ? srtInfos.size() : endIndex;
        seektime = (int) TimeHelper.getTime(srtInfos.get(curIndex)
                .getFromTime()) - replayInfo.getPreLoad();
        seektime = seektime < 0 ? 0 : seektime;
        seekendtime = (int) TimeHelper.getTime(srtInfos.get(endIndex)
                .getToTime()) + replayInfo.getAfterLoad();
        if (mediaPlayer != null)
        {
            videoSeek(seektime);
        }
    }

    private void stopPlay()
    {
        videoPlayThread.isPlaying = false;
        setCusReplay(false);
        isPaused = false;
        isShowingSrt = false;
        if (mediaPlayer != null)
        {
            try
            {
                mediaPlayer.reset();
                mediaPlayer.release();
                setMediaPlayer(null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void playpause()
    {
        if (mediaPlayer.isPlaying())
        {
            isPaused = true;
            isShowingSrt = false;
            mediaPlayer.pause();
            imgButton_play.setImageResource(R.drawable.bfq_play);
        }
        else
        {
            imgButton_play.setImageResource(R.drawable.bfq_pause);
            if (isPausedModel())
            {
                // 暂停,但字幕不在更新或播放. 这种情况下,继续播放
                isPaused = false;
                isShowingSrt = true;
                mediaPlayer.start();
            }
            else
            {
                // 在一个新的字幕播放中
                isPaused = false;
                // play(seektime);
                videoSeek(seektime);
            }

        }
    }

    private void videoSeek(int time)
    {
        hideHead();
        isShowingSrt = true;
        isPaused = false;
        this.imgButton_play.setImageResource(R.drawable.bfq_pause);
        mediaPlayer.seekTo(time);
        mediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener()
        {
            @Override
            public void onSeekComplete(MediaPlayer arg0)
            {
                mediaPlayer.start();
            }
        });
    }

    public boolean isPaused = false;// 只在暂停时为true
    boolean isShowingSrt = false;// 字幕更新或播放时为true,暂停停止时为false
    private boolean isCusReplay = false;
    public boolean onlyOneSrt = false;// 控制字幕是否是单条模式

    public boolean isPausedModel()
    {
        return isPaused && !isShowingSrt;
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SRT_AUTOPAUSE_CODE)
            {
                if (isCusReplay())
                {
                    setCusReplay(false);
                }
                seekendtime = Integer.MAX_VALUE;
                playpause();
            }
            else if (msg.what == ON_PLAYING_CODE)
            {
                int position = BasicNumberUtil.getNumber("" + msg.obj);
                setTipTv(position);
                if (!isCusReplay())
                {
                    if (updateUI(position))
                    {
                        initSeekTimes();
                    }
                }
                else
                {
                    // 这儿不更新curIndex
                    updateReplayUI(BasicNumberUtil.getNumber("" + msg.obj));
                }
            }
            else
            {
                playpause();
            }
        }

    };

    private void setTipTv(int position)
    {
        if (position > 0)
        {
            final String timeStr = SrtTextHelper.timeToText(position) + "/"
                    + SrtTextHelper.timeToText(seekBar.getMax());
            tipTv.setText("  " + videoSeries + "-" + videoEpisode + "    "
                    + timeStr);
        }
        else
        {
            tipTv.setText(videoSeries + "-" + videoEpisode);
        }
    }

    private boolean updateReplayUI(int position)
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
                    return true;
                }
                return false;
            }
        }
        return false;
    };

    /**
     * 成功则表示已经切换字幕,否则还是原有字幕
     * 
     * @param position
     * @return
     */
    private boolean updateUI(int position)
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
                    return true;
                }
                return false;
            }
        }
        return false;
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
        return false;
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
            videoSeek(seektime);
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
            videoSeek(seektime);
        }
    }

    /**
     * 非常重要的一个方法,两个变量直接控制播放起点和终点,不能乱用
     */
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

    @Override
    public void onBackPressed()
    {
        System.out.println("按下返回键 curIndex: " + curIndex);
        Intent intent = new Intent();
        intent.putExtra("curIndex", curIndex);// 放入返回值
        setResult(0, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
        super.onBackPressed();
    }

    @Override
    public void onPause()
    {
        System.out.println("OnPause........" + mediaPlayer.isPlaying());
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            playpause();
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            super.onConfigurationChanged(newConfig);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            super.onConfigurationChanged(newConfig);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (int) (0.4 * Math
                            .max(MyAppParams.getScreenWidth(),
                                    MyAppParams.getScreenHeight()))));
        }
        hideHead();
        hideVirtualBts();
    }

    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer)
    {
        this.mediaPlayer = mediaPlayer;
    }

    public boolean isCusReplay()
    {
        return isCusReplay;
    }

    public void setCusReplay(boolean isCusReplay)
    {
        this.isCusReplay = isCusReplay;
    }

    public Handler getHandler()
    {
        return handler;
    }

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void doDoubleClick(MotionEvent e)
    {
        if (e.getY() > videoView.getBottom())
        {
            playpause();
        }
    }
}
