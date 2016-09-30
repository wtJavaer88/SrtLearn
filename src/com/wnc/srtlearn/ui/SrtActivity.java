package com.wnc.srtlearn.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.headset.HeadSetUtil;
import net.headset.HeadSetUtil.OnHeadSetListener;
import srt.DataHolder;
import srt.SRT_VIEW_TYPE;
import srt.SrtFilesAchieve;
import srt.SrtInfo;
import srt.SrtPlayService;
import srt.SrtTextHelper;
import srt.TimeHelper;
import srt.ex.ReachFileTailException;
import srt.ex.SrtException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.dao.FavDao;
import com.wnc.srtlearn.dao.WorkDao;
import com.wnc.srtlearn.monitor.StudyMonitor;
import com.wnc.srtlearn.monitor.WorkMgr;
import com.wnc.srtlearn.monitor.work.ActiveWork;
import com.wnc.srtlearn.monitor.work.WORKTYPE;
import com.wnc.srtlearn.setting.SrtSetting;
import com.wnc.srtlearn.ui.handler.AutoPlayHandler;
import common.app.BasicPhoneUtil;
import common.app.ClickFileIntentFactory;
import common.app.ClipBoardUtil;
import common.app.ShareUtil;
import common.app.SysInit;
import common.app.ToastUtil;
import common.app.WheelDialogShowUtil;
import common.uihelper.AfterWheelChooseListener;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.CtrlableVerGestureDetectorListener;
import common.uihelper.gesture.EmptyFlingPoint;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;
import common.utils.MyFileUtil;
import common.utils.TextFormatUtil;

public class SrtActivity extends SBaseLearnActivity implements OnClickListener,
        OnLongClickListener, CtrlableHorGestureDetectorListener,
        CtrlableVerGestureDetectorListener, UncaughtExceptionHandler
{
    private final String SRT_PLAY_TEXT = "播放";
    private final String SRT_STOP_TEXT = "停止";
    public Handler autoPlayHandler;
    final int PINYIN_RESULT = 100;

    View main;

    private Button btnPlay;
    private TextView movieTv;
    private TextView chsTv;
    private TextView engTv;
    private TextView timelineTv;
    private ImageButton imgBtnStar;

    private GestureDetector gestureDetector;
    AlertDialog alertDialog;

    int[] defaultTimePoint =
    { 0, 0, 0 };
    int[] defaultMoviePoint =
    { 0, -1 };// 初次使用请把右边序号设为-1,以便程序判断

    String[] settingItems = new String[]
    { "自动下一条", "播放声音", "打开复读", "隐藏中文", "音量键-翻页" };
    String[] moreItems = new String[]
    { "说一说", "写一写", "拼一拼", "查一查", "看视频" };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        main = getLayoutInflater().from(this).inflate(R.layout.activity_srt,
                null);

        hideVirtualBts();
        setContentView(main);

        // 引入线控监听
        HeadSetUtil.getInstance().setOnHeadSetListener(headSetListener);
        HeadSetUtil.getInstance().open(this);
        // 设置未捕获异常UncaughtExceptionHandler的处理方法
        Thread.setDefaultUncaughtExceptionHandler(this);

        srtPlayService = new SrtPlayService(this);

        SysInit.init(this);

        initMonitor();
        initView();
        initDialogs();
        if (srtPlayService.isSrtShowing())
        {
            try
            {
                play(DataHolder.getCurrent());
            }
            catch (SrtException e)
            {
                e.printStackTrace();
            }
        }
        autoPlayHandler = new AutoPlayHandler(this);

        // 因为是横屏,所以设置的滑屏比例低一些
        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.15, 0.25, this, this));
        enter(SrtFilesAchieve.getSrtFileByArrIndex(0, 6));
    }

    /**
     * 虚拟按键变成圆点
     */
    @SuppressLint("NewApi")
    public void virtualBtsToRadiu()
    {
        int i = main.getSystemUiVisibility();

        if (i == View.SYSTEM_UI_FLAG_VISIBLE)
        {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }

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

    private void initDialogs()
    {
        initEngMenuDialog();
        initSettingDialog();
        initMoreDialog();
    }

    private void initMonitor()
    {
        StudyMonitor.runMonitor();
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
                                SrtActivity.this
                                        .startActivity(new Intent(
                                                SrtActivity.this,
                                                RecWordActivity.class)
                                                .putExtra("dialog", chsTv
                                                        .getText().toString()));
                                break;
                            case 1:
                                stopSrtPlay();
                                SrtActivity.this.startActivity(new Intent(
                                        SrtActivity.this, BihuaActivity.class)
                                        .putExtra("dialog", chsTv.getText()
                                                .toString()));
                                break;
                            case 2:
                                srtPlayService.stopSrt();
                                intoPinyin();
                                break;
                            case 3:
                                srtPlayService.stopSrt();
                                intoSearch();
                                break;
                            case 4:
                                srtPlayService.stopSrt();
                                intoVideo();
                                break;
                            default:
                                break;
                            }
                            hideVirtualBts();
                        }
                        catch (Exception e)
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "操作失败!");
                            e.printStackTrace();
                        }
                    }

                    private void intoSearch()
                    {
                        Intent intent = new Intent(SrtActivity.this,
                                SrtSearchActivity.class).putExtra("dialog",
                                engTv.getText().toString());
                        startActivity(intent);
                    }

                    private void intoPinyin() throws SrtException
                    {
                        Intent intent = new Intent(SrtActivity.this,
                                PinyinActivity.class).putExtra("dialog",
                                DataHolder.getCurrent().getChs()).putExtra(
                                "pinyin", DataHolder.getCurrent().getEng());
                        startActivityForResult(intent, PINYIN_RESULT);
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        hideVirtualBts();
                    }
                });
    }

    private void intoVideo() throws SrtException
    {
        stopSrtPlay();
        Intent intent = new Intent(SrtActivity.this, VideoActivity.class)
                .putExtra("fileinfo",
                        SrtTextHelper.getSxFile(srtPlayService.getCurFile()))
                .putExtra(
                        "seekfrom",
                        (int) TimeHelper.getTime(DataHolder.getCurrent()
                                .getFromTime()))
                .putExtra(
                        "seekto",
                        (int) TimeHelper.getTime(DataHolder.getCurrent()
                                .getToTime()))
                .putExtra("curindex", DataHolder.getCurrentSrtIndex())
                .putExtra("eng", DataHolder.getCurrent().getEng())
                .putExtra("chs", DataHolder.getCurrent().getChs());
        startActivity(intent);
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
                            hideVirtualBts();
                        }
                        catch (Exception e)
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "操作失败!");
                            e.printStackTrace();
                        }
                    }

                    private String getEng() throws SrtException
                    {
                        String result = "";
                        for (SrtInfo srtInfo : srtPlayService
                                .getCurrentPlaySrtInfos())
                        {
                            result += srtInfo.getEng() + " ";
                        }
                        return result;
                    }

                    private String getEngChs() throws SrtException
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

                    private void shareSrt() throws SrtException
                    {
                        ShareUtil.shareText(SrtActivity.this, getEngChs());
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        hideVirtualBts();
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
                                toggleChsTv();
                                break;
                            case 4:
                                SrtSetting.setVolKeyListen(SrtSetting
                                        .isVolKeyListen() ? false : true);
                                break;
                            default:
                                break;
                            }
                            hideVirtualBts();
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
                        hideVirtualBts();
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

        imgBtnStar = (ImageButton) findViewById(R.id.btnStar);

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
                setSrtContent(DataHolder.getCurrent());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initFileTv(String srtFilePath)
    {
        movieTv.setText(SrtTextHelper.getSxFile(srtFilePath));
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
            try
            {
                intoVideo();
            }
            catch (SrtException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // alertDialog = moreDialogBuilder.show();
            break;
        }
        hideVirtualBts();
    }

    private void setting()
    {
        settingItems[0] = !SrtSetting.isAutoPlayNext() ? "自动下一条" : "只播放一条";
        settingItems[1] = !SrtSetting.isPlayVoice() ? "播放声音" : "不播放声音";
        settingItems[2] = !srtPlayService.isReplayCtrl() ? "复读" : "不复读";
        settingItems[3] = !isChsShow() ? "显示中文" : "隐藏中文";
        settingItems[4] = !SrtSetting.isVolKeyListen() ? "音量键-翻页" : "音量键-音量";
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
            final String[] leftTimelineArr = DataHolder.getSrtTimeArr(
                    srtPlayService.getCurFile()).getLeftTimelineArr();
            final String[] rightTimelineArr = DataHolder.getSrtTimeArr(
                    srtPlayService.getCurFile()).getRightTimelineArr();
            if (leftTimelineArr != null && rightTimelineArr != null)
            {
                WheelDialogShowUtil.showSrtDialog(this, leftTimelineArr,
                        rightTimelineArr, wheelIndex1, wheelIndex2,
                        new AfterWheelChooseListener()
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
        String filePath = "";
        try
        {
            filePath = SrtFilesAchieve.getThumbPicPath(srtPlayService
                    .getCurFile());

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
    @Override
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
                                setSrtContentAndPlay(DataHolder.getClosestSrt(
                                        h, m, s));
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

                            enter(srtFilePath);
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void play(SrtInfo srt)
    {
        if (alertDialog != null)
        {
            alertDialog.hide();
        }

        if (srt != null)
        {
            setSrtContentAndPlay(srt);
        }
    }

    public void getSrtInfoAndPlay(SRT_VIEW_TYPE view_type)
    {
        try
        {
            SrtInfo srt = srtPlayService.getSrtInfo(view_type);
            // System.out.println("913view_type:" + view_type + "  srt:" + srt);
            play(srt);
        }
        catch (ReachFileTailException ex)
        {
            if (SrtSetting.isAutoNextEP())
            {
                final long tip_time = 2000;
                final String nextEp = getNextEp();
                if (nextEp != null)
                {
                    ToastUtil.showShortToast(this, "将为你自动播放下一集");
                    new Thread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            try
                            {
                                TimeUnit.MILLISECONDS.sleep(tip_time);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.what = AutoPlayHandler.NEXT_EP;
                            msg.obj = nextEp;
                            autoPlayHandler.sendMessage(msg);
                        }
                    }).start();
                }
                else
                {
                    ToastUtil.showLongToast(this, "本文件夹已经没有更多字幕了!");
                }
            }
            else
            {
                stopSrtPlay();
            }
        }
        catch (Exception ex)
        {
            stopSrtPlay();
            ToastUtil.showShortToast(this, ex.getMessage());
        }
    }

    /**
     * 自动获取下一集
     * 
     * @return
     */
    private String getNextEp()
    {
        String srtFile = srtPlayService.getCurFile();
        File folder = new File(BasicFileUtil.getFileParent(srtFile));
        List<File> sortedList = MyFileUtil.getSortFiles(folder.listFiles());
        System.out.println(sortedList);

        List<File> validFiles = new ArrayList<File>();
        for (File f : sortedList)
        {
            if (SrtTextHelper.isSrtfile(f))
            {
                validFiles.add(f);
            }
        }
        for (int i = 0; i < validFiles.size(); i++)
        {
            String absolutePath = validFiles.get(i).getAbsolutePath();
            if (i < validFiles.size() - 1 && absolutePath.equals(srtFile))
            {
                String nextFile = validFiles.get(i + 1).getAbsolutePath();
                return nextFile;
            }
        }
        return null;
    }

    @Override
    public void enter(String srtFilePath)
    {
        initFileTv(srtFilePath);
        try
        {
            srtPlayService.showNewSrtFile(srtFilePath);
        }
        catch (SrtException e)
        {
            e.printStackTrace();
        }
    }

    private void setSrtContentAndPlay(SrtInfo srt)
    {
        checkFav(srt);
        setSrtContent(srt);
        beginSrtPlay();
    }

    /**
     * 判断是否已经收藏过
     */
    private void checkFav(SrtInfo srt)
    {

        boolean exist = FavDao.isExistSingle(this, srt, srtPlayService
                .getCurFile().replace(MyAppParams.SRT_FOLDER, ""));
        if (exist)
        {
            imgBtnStar.setVisibility(View.VISIBLE);
        }
        else
        {
            imgBtnStar.setVisibility(View.INVISIBLE);
        }
    }

    private void setSrtContent(SrtInfo srt)
    {
        // 对于字幕里英文与中文颠倒的,用这种方法
        if (TextFormatUtil.containsChinese(srt.getEng())
                && !TextFormatUtil.containsChinese(srt.getChs()))
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
            timelineTv.setText(SrtTextHelper.concatTimeline(srt.getFromTime(),
                    srt.getToTime()));

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
    public void doLeft(FlingPoint p1, FlingPoint p2)
    {
        if (canScroll(p1.getY()))
        {
            getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LEFT);
        }
    }

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        if (canScroll(p1.getY()))
        {
            getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_RIGHT);
        }
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

        System.out.println("运行总时间: " + StudyMonitor.getRunTime());
        try
        {
            WorkDao.initDb(getApplicationContext());
            ActiveWork applicationActiveWork = StudyMonitor
                    .getApplicationActiveWork();
            int runId = WorkMgr.insertRunRecord(
                    applicationActiveWork.getEntertime(),
                    applicationActiveWork.getExitTime());
            WorkMgr.insertWork(WORKTYPE.SRT, runId);
            WorkMgr.insertWork(WORKTYPE.TTS_REC, runId);
            WorkMgr.insertWork(WORKTYPE.SRT_SEARCH, runId);
            WorkDao.closeDb();
            backupDatabase(this);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public String backupDatabase(Context context)
    {
        String subjectdb = "srtlearn.db";
        File dbFile = context.getDatabasePath(subjectdb);
        String newFilePath = BasicFileUtil.getMakeFilePath(MyAppParams
                .getInstance().getBackupDbPath(),
                BasicDateUtil.getCurrentDateTimeString() + "_" + subjectdb);
        // File[] files = new File(MyAppParams.getInstance().getBackupDbPath())
        // .listFiles();
        // Arrays.sort(files);

        if (BasicFileUtil.CopyFile(dbFile, new File(newFilePath)))
        {
            ToastUtil.showLongToast(context, "复制数据库成功");
        }
        else
        {
            ToastUtil.showShortToast(context, "复制" + subjectdb + "文件到<"
                    + newFilePath + ">失败!");
        }

        return newFilePath;
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
                doRight(new EmptyFlingPoint(), new EmptyFlingPoint());
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
                doLeft(new EmptyFlingPoint(), new EmptyFlingPoint());
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
        else
        {
            BasicPhoneUtil.showMediaVolume(SrtActivity.this);
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
    public void doUp(FlingPoint p1, FlingPoint p2)
    {
        if (canScroll(p1.getY()))
        {
            getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LEFT);
        }
    }

    @Override
    public void doDown(FlingPoint p1, FlingPoint p2)
    {
        if (canScroll(p1.getY()))
        {
            getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_RIGHT);
        }
    }

    /**
     * 判断是否可以竖向滚动
     * 
     * @param p1
     * @return
     */
    private boolean canScroll(float y)
    {
        // 也可以调整为只要是英文大于2行,就不监听
        return engTv.getLineCount() <= 2 || y < this.engTv.getTop()
                || y > this.engTv.getBottom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.PINYIN_RESULT && data != null)
        {
            String retPY = data.getStringExtra("pinyin");
            System.out.println("返回拼音:" + retPY);
            try
            {
                SrtInfo srtInfo = DataHolder.getCurrent();
                srtInfo.setEng(retPY);
                setSrtContent(srtInfo);
            }
            catch (SrtException e)
            {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Handler getHanlder()
    {
        return this.autoPlayHandler;
    }

    @Override
    public SrtPlayService getSrtPlayService()
    {
        return srtPlayService;
    }

    @Override
    public void playNext()
    {
        doRight(new EmptyFlingPoint(), new EmptyFlingPoint());
    }

    @Override
    public void playCurrent()
    {
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_CURRENT);
    }

}
