package com.wnc.srtlearn.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import net.widget.sdufe.thea.guo.GalleryModel;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends BaseHorActivity implements OnClickListener, UncaughtExceptionHandler, AfterGalleryChooseListener
{
	private Gallery gallery;

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

	}

	private void initMonitor()
	{
		StudyMonitor.runMonitor();
	}

	private void initView()
	{
		((Button) findViewById(R.id.myfav_bt)).setOnClickListener(this);
		((Button) findViewById(R.id.tosrt_bt)).setOnClickListener(this);
		gallery = (Gallery) findViewById(R.id.seasons_gallery);
		List<GalleryModel> list = new ArrayList<GalleryModel>();
		list.add(new GalleryModel(R.drawable.friends_s01, "老友记-第一季", "5"));
		list.add(new GalleryModel(R.drawable.friends_s02, "老友记-第二季", "6"));
		list.add(new GalleryModel(R.drawable.friends_s03, "老友记-第三季", "7"));
		list.add(new GalleryModel(R.drawable.friends_s04, "老友记-第四季", "8"));
		list.add(new GalleryModel(R.drawable.friends_s05, "老友记-第五季", "9"));
		list.add(new GalleryModel(R.drawable.friends_s06, "老友记-第六季", "10"));
		list.add(new GalleryModel(R.drawable.friends_s07, "老友记-第七季", "11"));
		list.add(new GalleryModel(R.drawable.friends_s08, "老友记-第八季", "12"));
		list.add(new GalleryModel(R.drawable.friends_s09, "老友记-第九季", "13"));
		list.add(new GalleryModel(R.drawable.friends_s10, "老友记-第十季", "14"));
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
		case R.id.tosrt_bt:
			gotoAct(SrtActivity.class);
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
			ActiveWork applicationActiveWork = StudyMonitor.getApplicationActiveWork();
			int runId = WorkMgr.insertRunRecord(applicationActiveWork.getEntertime(), applicationActiveWork.getExitTime());
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
		String newFilePath = BasicFileUtil.getMakeFilePath(MyAppParams.getInstance().getBackupDbPath(), BasicDateUtil.getCurrentDateTimeString() + "_" + subjectdb);
		// File[] files = new File(MyAppParams.getInstance().getBackupDbPath())
		// .listFiles();
		// Arrays.sort(files);

		if (BasicFileUtil.CopyFile(dbFile, new File(newFilePath)))
		{
			ToastUtil.showLongToast(context, "复制数据库成功");
		}
		else
		{
			ToastUtil.showShortToast(context, "复制" + subjectdb + "文件到<" + newFilePath + ">失败!");
		}

		return newFilePath;
	}
}
