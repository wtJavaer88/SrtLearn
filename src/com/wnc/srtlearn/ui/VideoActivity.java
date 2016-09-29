package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import net.widget.cqq.AddAndSubView;
import srt.DataHolder;
import srt.SrtInfo;
import srt.TimeHelper;
import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.R;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
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
    private static final int PLAY_SLEEP_TIME = 200;
    private static final int SRT_AUTOPAUSE_CODE = 100;
    private static final int ON_PLAYING_CODE = 101;
    private SurfaceView surfaceView;
    private Button button_pause, button_replay_setting, button_custom_replay;
    private Button button_onlyone;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private GestureDetector gestureDetector;
    private TextView veng_tv;
    private TextView vchs_tv;
    private TextView tipTv;
    LinearLayout headLayout;
    private List<SrtInfo> srtInfos;
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
        headLayout = (LinearLayout) findViewById(R.id.video_head);
        button_onlyone = (Button) findViewById(R.id.button_onlyone);
        button_pause = (Button) findViewById(R.id.button_pause);
        button_replay_setting = (Button) findViewById(R.id.button_replay_setting);
        button_custom_replay = (Button) findViewById(R.id.button_replay_custom);

        veng_tv = (TextView) findViewById(R.id.veng_tv);
        vchs_tv = (TextView) findViewById(R.id.vchs_tv);
        tipTv = (TextView) findViewById(R.id.tipTv);

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
        srtInfos = DataHolder.getAllSrtInfos();
        Intent intent = getIntent();

        if (intent != null)
        {
            seektime = intent.getIntExtra("seekfrom", 0);
            seekendtime = intent.getIntExtra("seekto", 0);
            curIndex = intent.getIntExtra("curindex", 0);
            curSrt = srtInfos.get(curIndex);
            System.out.println("curIndex:  " + curIndex);
            setUI();
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
        surfaceView.setOnClickListener(this);
        button_onlyone.setOnClickListener(this);
        button_pause.setOnClickListener(this);
        button_replay_setting.setOnClickListener(this);
        button_custom_replay.setOnClickListener(this);
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
        case R.id.button_pause:
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

        case R.id.sv:
            switchHead();
            break;
        default:
            break;
        }
    }

    private void switchHead()
    {
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

    class RelpayInfo
    {
        int preLoad;
        int afterLoad;
        int srtcounts;

        public int getPreLoad()
        {
            return preLoad;
        }

        public void setPreLoad(int preLoad)
        {
            this.preLoad = preLoad;
        }

        public int getAfterLoad()
        {
            return afterLoad;
        }

        public void setAfterLoad(int afterLoad)
        {
            this.afterLoad = afterLoad;
        }

        public int getSrtcounts()
        {
            return srtcounts;
        }

        public void setSrtcounts(int srtcounts)
        {
            this.srtcounts = srtcounts;
        }

        @Override
        public String toString()
        {
            return "RelpayInfo [preLoad=" + preLoad + ", afterLoad="
                    + afterLoad + ", srtcounts=" + srtcounts + "]";
        }
    }

    RelpayInfo replayInfo = new RelpayInfo();
    AlertDialog replaySettingDialog;

    private void replaySetting()
    {
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
            final AddAndSubView mView1 = new AddAndSubView(this);
            if (replayInfo.getSrtcounts() > 0)
            {
                mView1.setNum(replayInfo.getSrtcounts());
            }
            else
            {
                mView1.setNum(2);
            }
            mView1.setDeafultStyle();
            mLayout1.addView(mView1);
            LinearLayout mLayout2 = (LinearLayout) replaySettingDialog
                    .findViewById(R.id.layout_add_and_sub_preload);

            final AddAndSubView mView2 = new AddAndSubView(this);
            mView2.setNum(replayInfo.getPreLoad());
            mView2.setNumStep(100);
            mView2.setDeafultStyle();
            mLayout2.addView(mView2);
            LinearLayout mLayout3 = (LinearLayout) replaySettingDialog
                    .findViewById(R.id.layout_add_and_sub_afterload);

            final AddAndSubView mView3 = new AddAndSubView(this);
            mView2.setNum(replayInfo.getAfterLoad());
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
            replaySettingDialog.show();
        }
    }

    /**
     * 默认的话三个参数全为0, 只复读自己一句
     */
    private void cusReplay()
    {
        firstPlay = false;
        isCusReplay = true;
        this.button_pause.setText("暂停");
        int endIndex = curIndex + replayInfo.getSrtcounts() - 1;
        if (replayInfo.getSrtcounts() == 0)
        {
            endIndex = curIndex;
        }

        endIndex = endIndex >= srtInfos.size() ? srtInfos.size() : endIndex;
        seektime = (int) TimeHelper.getTime(srtInfos.get(curIndex)
                .getFromTime()) - replayInfo.getPreLoad();
        seektime = seektime < 0 ? 0 : seektime;
        seekendtime = (int) TimeHelper.getTime(srtInfos.get(endIndex)
                .getToTime()) + replayInfo.getAfterLoad();
        if (mediaPlayer != null && mediaPlayer.isPlaying())
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
                    isShowingSrt = true;
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
    boolean onlyOneSrt = false;// 控制字幕是否是单条模式

    private boolean isPausedModel()
    {
        return isPaused && !isShowingSrt;
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SRT_AUTOPAUSE_CODE)
            {
                if (isCusReplay)
                {
                    isCusReplay = false;
                }
                seekendtime = Integer.MAX_VALUE;
                playpause();
            }
            else if (msg.what == ON_PLAYING_CODE)
            {
                if (!isCusReplay)
                {
                    if (updateUI(BasicNumberUtil.getNumber("" + msg.obj)))
                    {
                        initSeekTimes();
                    }
                }
                else
                {
                    // 这儿不更新curIndex
                    updateReplayUI(BasicNumberUtil.getNumber("" + msg.obj));
                }
                System.out.println(seekendtime);
            }
            else
            {
                playpause();
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
        hideHead();
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
                                boolean isover = curOver(position);
                                System.out.println(position + "/" + seekendtime
                                        + "  " + isover + " " + isPaused);
                                if (isover && (isCusReplay || onlyOneSrt))
                                {
                                    Message msg = new Message();
                                    msg.what = SRT_AUTOPAUSE_CODE;
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

                        private boolean curOver(int position)
                        {
                            return position > seekendtime && !isPausedModel();
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
        if (p1.getY() < seekBar.getTop())
        {
            return;
        }
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
        if (seekBar.getMax() > 1000)
        {
            this.tipTv.setText(TimeHelper.getTime(curSrt.getFromTime()) / 1000
                    + "/" + seekBar.getMax() / 1000);
        }
    }

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        if (p1.getY() < seekBar.getTop())
        {
            return;
        }
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

}
