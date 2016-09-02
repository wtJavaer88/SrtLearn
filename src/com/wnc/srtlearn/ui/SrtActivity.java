package com.wnc.srtlearn.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import srt.DataHolder;
import srt.SRT_VIEW_TYPE;
import srt.SrtFileDataHelper;
import srt.SrtFilesAchieve;
import srt.SrtInfo;
import srt.SrtPlayService;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.modules.srt.HeadSetUtil;
import com.wnc.srtlearn.modules.srt.SrtSetting;
import com.wnc.srtlearn.modules.srt.HeadSetUtil.OnHeadSetListener;
import com.wnc.srtlearn.ui.handler.AutoPlayHandler;
import common.app.ClickFileIntentFactory;
import common.app.ClipBoardUtil;
import common.app.ShareUtil;
import common.app.SharedPreferenceUtil;
import common.app.ToastUtil;
import common.app.WheelDialogShowUtil;
import common.uihelper.AfterWheelChooseListener;
import common.uihelper.HorGestureDetectorListener;
import common.uihelper.MyAppParams;
import common.uihelper.MyGestureDetector;
import common.uihelper.VerGestureDetectorListener;
import common.utils.TextFormatUtil;

public class SrtActivity extends BaseActivity implements OnClickListener,
        OnLongClickListener, HorGestureDetectorListener,
        VerGestureDetectorListener, UncaughtExceptionHandler
{
    private final String SRT_PLAY_TEXT = "播放";
    private final String SRT_STOP_TEXT = "停止";
    public Handler autoPlayHandler;
    final int PINYIN_RESULT = 100;

    // 组件设置成静态, 防止屏幕旋转的时候内存地址会变
    private Button btnPlay;
    private TextView movieTv;
    private TextView chsTv;
    private TextView engTv;
    private TextView timelineTv;

    private GestureDetector gestureDetector;
    AlertDialog alertDialog;

    int[] defaultTimePoint =
    { 0, 0, 0 };
    int[] defaultMoviePoint =
    { 0, -1 };// 初次使用请把右边序号设为-1,以便程序判断

    String[] settingItems = new String[]
    { "自动下一条", "播放声音", "打开复读", "音量调节", "隐藏中文", "音量键-翻页" };
    String[] moreItems = new String[]
    { "自主跟读", "笔顺学习", "拼音修改" };
    private SrtPlayService srtPlayService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
        setContentView(R.layout.activity_srt);

        SharedPreferenceUtil.init(this);
        // 引入线控监听
        HeadSetUtil.getInstance().setOnHeadSetListener(headSetListener);
        HeadSetUtil.getInstance().open(this);
        // 设置未捕获异常UncaughtExceptionHandler的处理方法
        Thread.setDefaultUncaughtExceptionHandler(this);

        srtPlayService = new SrtPlayService(this);

        initAppParams();
        initView();
        initEngMenuDialog();
        initSettingDialog();
        initMoreDialog();
        if (srtPlayService.isSrtShowing())
        {
            play(DataHolder.getCurrent());
        }
        autoPlayHandler = new AutoPlayHandler(this);

        // 因为是横屏,所以设置的滑屏比例低一些
        this.gestureDetector = new GestureDetector(this, new MyGestureDetector(
                0.1, 0.25, this));
    }

    @SuppressWarnings("deprecation")
    private void initAppParams()
    {
        MyAppParams.getInstance().setPackageName(this.getPackageName());
        MyAppParams.getInstance().setResources(this.getResources());
        MyAppParams.getInstance().setAppPath(this.getFilesDir().getParent());
        MyAppParams.setScreenWidth(this.getWindowManager().getDefaultDisplay()
                .getWidth());
        MyAppParams.setScreenHeight(this.getWindowManager().getDefaultDisplay()
                .getHeight());
    }

    Builder alertDialogBuilder;
    Builder settingDialogBuilder;
    Builder moreDialogBuilder;

    private void initMoreDialog()
    {
        moreDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("更多")
                .setItems(moreItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            switch (which)
                            {
                            case 0:
                                stopSrtPlay();
                                SrtActivity.this.startActivity(new Intent(
                                        SrtActivity.this, RecWordActivity.class)
                                        .putExtra("dialog", DataHolder
                                                .getCurrent().getChs()));
                                break;
                            case 1:
                                stopSrtPlay();
                                SrtActivity.this.startActivity(new Intent(
                                        SrtActivity.this, BihuaActivity.class)
                                        .putExtra("dialog", DataHolder
                                                .getCurrent().getChs()));
                                break;
                            case 2:
                                srtPlayService.stopSrt();
                                Intent intent = new Intent(SrtActivity.this,
                                        PinyinActivity.class).putExtra(
                                        "dialog",
                                        DataHolder.getCurrent().getChs())
                                        .putExtra(
                                                "pinyin",
                                                DataHolder.getCurrent()
                                                        .getEng());
                                startActivityForResult(intent, PINYIN_RESULT);
                                break;
                            default:
                                break;
                            }
                        }
                        catch (Exception e)
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "操作失败!");
                            e.printStackTrace();
                        }
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
    }

    private void initEngMenuDialog()
    {
        final String[] menuItems = new String[]
        { "复制英文", "复制中英文", "收藏", "分享" };
        alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("对字幕进行操作")
                .setItems(menuItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            switch (which)
                            {
                            case 0:
                                ClipBoardUtil.setNormalContent(
                                        SrtActivity.this, getEng());
                                ToastUtil.showLongToast(
                                        getApplicationContext(), "复制成功!");
                                break;
                            case 1:
                                ClipBoardUtil.setNormalContent(
                                        SrtActivity.this, getEngChs());
                                ToastUtil.showLongToast(
                                        getApplicationContext(), "复制成功!");
                                break;
                            case 2:
                                srtPlayService.favorite();
                                break;
                            case 3:
                                srtPlayService.favorite();
                                shareSrt();
                                break;
                            default:
                                break;
                            }
                        }
                        catch (Exception e)
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "操作失败!");
                            e.printStackTrace();
                        }
                    }

                    private String getEng()
                    {
                        String result = "";
                        for (SrtInfo srtInfo : srtPlayService
                                .getCurrentPlaySrtInfos())
                        {
                            result += srtInfo.getEng() + " ";
                        }
                        return result;
                    }

                    private String getEngChs()
                    {
                        String eresult = "";
                        String cresult = "";
                        for (SrtInfo srtInfo : srtPlayService
                                .getCurrentPlaySrtInfos())
                        {
                            eresult += srtInfo.getEng() + " ";
                            cresult += srtInfo.getChs() + " ";
                        }
                        System.out.println(eresult + " <> " + cresult);
                        return eresult + " <> " + cresult;
                    }

                    private void shareSrt()
                    {
                        ShareUtil.shareText(SrtActivity.this, getEngChs());
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
    }

    private void initSettingDialog()
    {
        settingDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("设置")
                .setItems(settingItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            switch (which)
                            {
                            case 0:
                                SrtSetting.setAutoPlayNext(SrtSetting
                                        .isAutoPlayNext() ? false : true);
                                break;
                            case 1:
                                SrtSetting.setPlayVoice(SrtSetting
                                        .isPlayVoice() ? false : true);
                                break;
                            case 2:
                                srtPlayService.switchReplayModel();
                                if (!srtPlayService.isRunning())
                                {
                                    beginSrtPlay();
                                }
                                break;
                            case 3:
                                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                mAudioManager.adjustStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                                break;
                            case 4:
                                toggleChsTv();
                                break;
                            case 5:
                                SrtSetting.setVolKeyListen(SrtSetting
                                        .isVolKeyListen() ? false : true);
                                break;
                            default:
                                break;
                            }
                        }
                        catch (Exception e)
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "操作失败!");
                            e.printStackTrace();
                        }
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
    }

    private void initView()
    {
        btnPlay = (Button) findViewById(R.id.btnPlay);
        movieTv = (TextView) findViewById(R.id.file_tv);
        chsTv = (TextView) findViewById(R.id.chs_tv);
        engTv = (TextView) findViewById(R.id.eng_tv);
        timelineTv = (TextView) findViewById(R.id.timeline_tv);

        chsTv.setMovementMethod(new ScrollingMovementMethod());
        engTv.setMovementMethod(new ScrollingMovementMethod());

        engTv.setOnLongClickListener(this);
        timelineTv.setOnClickListener(this);
        movieTv.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        findViewById(R.id.btnFirst).setOnClickListener(this);
        findViewById(R.id.btnLast).setOnClickListener(this);
        findViewById(R.id.btnSkip).setOnClickListener(this);
        findViewById(R.id.btnChoose).setOnClickListener(this);
        findViewById(R.id.btnSetting).setOnClickListener(this);
        findViewById(R.id.btnMore).setOnClickListener(this);
        try
        {
            if (BasicStringUtil.isNotNullString(DataHolder.getFileKey()))
            {
                initFileTv(DataHolder.getFileKey());
                setContent(DataHolder.getCurrent());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initFileTv(String srtFilePath)
    {
        if (BasicFileUtil.isExistFile(srtFilePath))
        {
            File f = new File(srtFilePath);
            String folder = f.getParent();
            int i = folder.lastIndexOf("/");
            if (i != -1)
            {
                folder = folder.substring(i + 1);
                String name = TextFormatUtil.getFileNameNoExtend(f.getName());

                movieTv.setText(folder + " / " + name);
            }
        }
    }

    private void toggleChsTv()
    {
        if (isChsShow())
        {
            chsTv.setVisibility(View.INVISIBLE);
        }
        else
        {
            chsTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btnSetting:
            setting();
            break;
        case R.id.btnChoose:
            showChooseMovieWheel();
            break;
        case R.id.btnSkip:
            if (hasSrtContent())
            {
                showSkipWheel();
            }
            break;
        case R.id.btnFirst:
            if (hasSrtContent())
            {
                getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_FIRST);
            }
            break;
        case R.id.btnLast:
            if (hasSrtContent())
            {
                getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LAST);
            }
            break;
        case R.id.btnPlay:
            if (hasSrtContent())
            {
                clickPlayBtn();
            }
            break;
        case R.id.file_tv:
            if (hasSrtContent())
            {
                stopSrtPlay();
                showThumbPic();
            }
            break;
        case R.id.timeline_tv:
            if (hasSrtContent())
            {
                stopSrtPlay();// 停止播放
                showSrtInfoWheel();
            }
            break;
        case R.id.btnMore:
            alertDialog = moreDialogBuilder.show();
            break;
        }
    }

    private void setting()
    {
        settingItems[0] = !SrtSetting.isAutoPlayNext() ? "自动下一条" : "只播放一条";
        settingItems[1] = !SrtSetting.isPlayVoice() ? "播放声音" : "不播放声音";
        settingItems[2] = !srtPlayService.isReplayCtrl() ? "复读" : "不复读";
        settingItems[4] = !isChsShow() ? "显示中文" : "隐藏中文";
        settingItems[5] = !SrtSetting.isVolKeyListen() ? "音量键-翻页" : "音量键-音量";
        alertDialog = settingDialogBuilder.show();
    }

    private boolean isChsShow()
    {
        return chsTv.getVisibility() == View.VISIBLE;
    }

    private void showSrtInfoWheel()
    {
        List<SrtInfo> currentSrtInfos = DataHolder.getAllSrtInfos();

        if (currentSrtInfos != null && !currentSrtInfos.isEmpty())
        {

            int wheelIndex1 = -1;
            int wheelIndex2 = -1;

            if (srtPlayService.isReplayRunning())
            {
                wheelIndex1 = srtPlayService.getBeginReplayIndex();
                wheelIndex2 = srtPlayService.getEndReplayIndex();
                ToastUtil.showShortToast(this, "当前正在复读模式中!");
            }
            else
            {
                wheelIndex1 = srtPlayService.getCurIndex();
                wheelIndex2 = srtPlayService.getCurIndex();
            }
            if (SrtFileDataHelper.leftTimelineArr != null
                    && SrtFileDataHelper.rightTimelineArr != null)
            {
                WheelDialogShowUtil.showSrtDialog(this,
                        SrtFileDataHelper.leftTimelineArr,
                        SrtFileDataHelper.rightTimelineArr, wheelIndex1,
                        wheelIndex2, new AfterWheelChooseListener()
                        {
                            @Override
                            public void afterWheelChoose(Object... objs)
                            {
                                srtPlayService.setReplayIndex(
                                        Integer.valueOf(objs[0].toString()),
                                        Integer.valueOf(objs[1].toString()));
                                srtPlayService.setReplayCtrl(true);
                                DataHolder.setCurrentSrtIndex(srtPlayService
                                        .getBeginReplayIndex());
                                // 选择完毕立即开始播放
                                beginSrtPlay();
                            }
                        });
            }
        }
    }

    private boolean hasSrtContent()
    {
        return srtPlayService.isSrtShowing();
    }

    /**
     * 显示该剧集图片
     */
    private void showThumbPic()
    {
        String filePath = SrtFilesAchieve.getThumbPicPath(srtPlayService
                .getCurFile());
        try
        {
            Intent intent = ClickFileIntentFactory.getIntentByFile(filePath);
            startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ToastUtil.showShortToast(getApplicationContext(), "找不到图片:"
                    + filePath);
        }
    }

    public void clickPlayBtn()
    {
        if (srtPlayService.isRunning())
        {
            stopSrtPlay();
        }
        else
        {
            beginSrtPlay();
        }
    }

    private void beginSrtPlay()
    {
        btnPlay.setText(SRT_STOP_TEXT);
        srtPlayService.playSrt();
    }

    /**
     * 停止字幕播放
     */
    public void stopSrtPlay()
    {
        btnPlay.setText(SRT_PLAY_TEXT);
        srtPlayService.stopSrt();
    }

    private void showSkipWheel()
    {
        try
        {
            WheelDialogShowUtil.showTimeSelectDialog(this, defaultTimePoint,
                    new AfterWheelChooseListener()
                    {
                        @Override
                        public void afterWheelChoose(Object... objs)
                        {
                            try
                            {
                                int h = Integer.parseInt(objs[0].toString());
                                int m = Integer.parseInt(objs[1].toString());
                                int s = Integer.parseInt(objs[2].toString());
                                defaultTimePoint[0] = h;
                                defaultTimePoint[1] = m;
                                defaultTimePoint[2] = s;
                                setContentAndPlay(DataHolder.getClosestSrt(h,
                                        m, s));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                ToastUtil.showShortToast(SrtActivity.this,
                                        e.getMessage());
                            }
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showChooseMovieWheel()
    {
        try
        {
            final String[] leftArr = SrtFilesAchieve.getDirs();
            final String[][] rightArr = SrtFilesAchieve.getDirsFiles();
            WheelDialogShowUtil.showRelativeDialog(this, "选择剧集", leftArr,
                    rightArr, defaultMoviePoint[0], defaultMoviePoint[1], 8,
                    new AfterWheelChooseListener()
                    {
                        @Override
                        public void afterWheelChoose(Object... objs)
                        {
                            defaultMoviePoint[0] = Integer.valueOf(objs[0]
                                    .toString());
                            defaultMoviePoint[1] = Integer.valueOf(objs[1]
                                    .toString());
                            String srtFilePath = SrtFilesAchieve
                                    .getSrtFileByArrIndex(defaultMoviePoint[0],
                                            defaultMoviePoint[1]);
                            initFileTv(srtFilePath);
                            srtPlayService.showNewSrtFile(srtFilePath);
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void play(SrtInfo srt)
    {
        if (alertDialog != null)
        {
            alertDialog.hide();
        }

        if (srt != null)
        {
            setContentAndPlay(srt);
        }
    }

    public void getSrtInfoAndPlay(SRT_VIEW_TYPE view_type)
    {
        try
        {
            SrtInfo srt = srtPlayService.getSrtInfo(view_type);
            play(srt);
        }
        catch (Exception ex)
        {
            stopSrtPlay();
            ToastUtil.showLongToast(this, ex.getMessage());
        }
    }

    private void setContentAndPlay(SrtInfo srt)
    {
        setContent(srt);
        beginSrtPlay();
    }

    private void setContent(SrtInfo srt)
    {
        // 对于字幕里英文与中文颠倒的,用这种方法
        if (TextFormatUtil.containsChinese(srt.getEng()))
        {
            chsTv.setText(srt.getEng() == null ? "NULL" : srt.getEng());
            engTv.setText(srt.getChs() == null ? "NULL" : srt.getChs());
        }
        else
        {
            // System.out.println("setContent:" + srt);
            chsTv.setText(srt.getChs() == null ? "NULL" : srt.getChs());
            engTv.setText(srt.getEng() == null ? "NULL" : srt.getEng());
        }

        checkLineCount();

        if (srt.getFromTime() != null && srt.getToTime() != null)
        {
            timelineTv.setText(srt.getFromTime().toString() + " ---> "
                    + srt.getToTime().toString());

            defaultTimePoint[0] = srt.getFromTime().getHour();
            defaultTimePoint[1] = srt.getFromTime().getMinute();
            defaultTimePoint[2] = srt.getFromTime().getSecond();
        }
        ((TextView) findViewById(R.id.progress_tv)).setText(srtPlayService
                .getPleyProgress());
    }

    private void checkLineCount()
    {
        ToastUtil.cancel();
        int elineCount = engTv.getLineCount();
        int clineCount = chsTv.getLineCount();
        if (elineCount > 2 && clineCount > 2)
        {
            ToastUtil.showLongToast(this, "中文和英文都超过两行,请手动滚动");
        }
        else if (elineCount > 2)
        {
            ToastUtil.showLongToast(this, "英文超过两行,请手动滚动");
        }
        else if (clineCount > 2)
        {
            ToastUtil.showLongToast(this, "中文超过两行,请手动滚动");
        }
        // 下次开始,自动回到第一行
        engTv.scrollTo(0, 0);
        chsTv.scrollTo(0, 0);
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
    public void doLeft()
    {
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LEFT);
    }

    @Override
    public void doRight()
    {
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_RIGHT);
    }

    @Override
    public boolean onLongClick(View v)
    {
        stopSrtPlay();
        alertDialog = alertDialogBuilder.show();
        return true;
    }

    @Override
    public void onDestroy()
    {
        if (alertDialog != null)
        {
            alertDialog.dismiss();
        }
        HeadSetUtil.getInstance().close(this);
        super.onDestroy();
    }

    OnHeadSetListener headSetListener = new OnHeadSetListener()
    {
        @Override
        public void onDoubleClick()
        {
            srtPlayService.switchReplayModel();
            if (!srtPlayService.isRunning())
            {
                beginSrtPlay();
            }
        }

        @Override
        public void onClick()
        {
            clickPlayBtn();
        }

        @Override
        public void onThreeClick()
        {
            ToastUtil.showShortToast(getApplicationContext(), "三击");
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (SrtSetting.isVolKeyListen())
        {
            switch (keyCode)
            {
            case KeyEvent.KEYCODE_BACK:
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                doRight();
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                doLeft();
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (SrtSetting.isVolKeyListen())
        {
            switch (keyCode)
            {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
        stopSrtPlay();
        ToastUtil.showShortToast(this, "播放出现异常");
    }

    @Override
    public void doUp()
    {
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LEFT);
    }

    @Override
    public void doDown()
    {
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_RIGHT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.PINYIN_RESULT && data != null)
        {
            String retPY = data.getStringExtra("pinyin");
            System.out.println("返回拼音:" + retPY);
            SrtInfo srtInfo = DataHolder.getCurrent();
            srtInfo.setEng(retPY);
            setContent(srtInfo);
        }
    }

    public Handler getHanlder()
    {
        return this.autoPlayHandler;
    }

    public SrtPlayService getSrtPlayService()
    {
        return srtPlayService;
    }

}
