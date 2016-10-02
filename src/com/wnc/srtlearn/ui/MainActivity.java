package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import net.widget.sdufe.thea.guo.GalleryModel;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Gallery;

import com.wnc.basic.BasicDateUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.monitor.MyLogger;
import common.app.GalleryUtil;
import common.uihelper.AfterGalleryChooseListener;

public class MainActivity extends BaseHorActivity implements OnClickListener, UncaughtExceptionHandler, AfterGalleryChooseListener
{
	private Gallery gallery;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

		MyLogger.log(BasicDateUtil.getCurrentDateTimeString() + " 开始运行");
		Thread.setDefaultUncaughtExceptionHandler(this);
		setContentView(R.layout.activity_main);
		initView();

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
		GalleryUtil.getSrtSeasonGallery(this, gallery, list, this);
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

}
