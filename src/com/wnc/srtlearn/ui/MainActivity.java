package com.wnc.srtlearn.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import net.widget.sdufe.thea.guo.GalleryModel;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Gallery;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.dao.WorkDao;
import com.wnc.srtlearn.monitor.MyLogger;
import com.wnc.srtlearn.monitor.StudyMonitor;
import com.wnc.srtlearn.monitor.WorkMgr;
import com.wnc.srtlearn.monitor.work.ActiveWork;
import com.wnc.srtlearn.monitor.work.WORKTYPE;
import com.wnc.srtlearn.setting.Backup;
import common.app.GalleryUtil;
import common.app.SysInit;
import common.app.ToastUtil;
import common.uihelper.AfterGalleryChooseListener;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableVerGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

public class MainActivity extends BaseHorActivity implements OnClickListener,
        UncaughtExceptionHandler, AfterGalleryChooseListener,
        CtrlableVerGestureDetectorListener
{
    private Gallery gallery;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

        SysInit.init(this);
        initMonitor();

        MyLogger.log(BasicDateUtil.getCurrentDateTimeString() + " 开始运行");
        Thread.setDefaultUncaughtExceptionHandler(this);
        setContentView(R.layout.activity_main);
        initView();
        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.15, 0.5, null, this));
    }

    private void initMonitor()
    {
        StudyMonitor.runMonitor();
    }

    List<GalleryModel> list = new ArrayList<GalleryModel>();

    private void initView()
    {
        ((Button) findViewById(R.id.myfav_bt)).setOnClickListener(this);
        gallery = (Gallery) findViewById(R.id.seasons_gallery);
        list.add(new GalleryModel(R.drawable.friends_s01, "老友记-第一季",
                "Friends.S01"));
        list.add(new GalleryModel(R.drawable.friends_s02, "老友记-第二季",
                "Friends.S02"));
        list.add(new GalleryModel(R.drawable.friends_s03, "老友记-第三季",
                "Friends.S03"));
        list.add(new GalleryModel(R.drawable.friends_s04, "老友记-第四季",
                "Friends.S04"));
        list.add(new GalleryModel(R.drawable.friends_s05, "老友记-第五季",
                "Friends.S05"));
        list.add(new GalleryModel(R.drawable.friends_s06, "老友记-第六季",
                "Friends.S06"));
        list.add(new GalleryModel(R.drawable.friends_s07, "老友记-第七季",
                "Friends.S07"));
        list.add(new GalleryModel(R.drawable.friends_s08, "老友记-第八季",
                "Friends.S08"));
        list.add(new GalleryModel(R.drawable.friends_s09, "老友记-第九季",
                "Friends.S09"));
        list.add(new GalleryModel(R.drawable.friends_s10, "老友记-第十季",
                "Friends.S10"));
        try
        {
            GalleryUtil.getSrtSeasonGallery(this, gallery, list, this);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.myfav_bt:
            gotoAct(FavoriteSrtActivity.class);
            break;
        }
    }

    private void gotoAct(Class clazz)
    {
        startActivity(new Intent(this, clazz));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        System.out.println(ex.getMessage());
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }

    @Override
    public void afterGalleryChoose(String str)
    {
        System.out.println("选择的电视剧:" + str);
    }

    @Override
    public void onDestroy()
    {
        StudyMonitor.stopMonitor();

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
            if (Backup.canBackup)
            {
                backupDatabase(this);
            }
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

    @Override
    public void doUp(FlingPoint p1, FlingPoint p2)
    {
        gotoAct(FavoriteSrtActivity.class);
    }

    @Override
    public void doDown(FlingPoint p1, FlingPoint p2)
    {
        startActivity(new Intent(this, SrtActivity.class).putExtra(
                "season_name", list.get(gallery.getSelectedItemPosition())
                        .getId()));
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

}
