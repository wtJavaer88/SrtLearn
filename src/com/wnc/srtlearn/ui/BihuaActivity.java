package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;

import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.srt.SwfPlayMgr;
import common.app.GalleryUtil;
import common.app.ToastUtil;
import common.uihelper.AfterGalleryChooseListener;

public class BihuaActivity extends Activity implements OnClickListener, UncaughtExceptionHandler, AfterGalleryChooseListener
{

	private Gallery gallery;
	private EditText et;
	String dialog = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

		Thread.setDefaultUncaughtExceptionHandler(this);
		setContentView(R.layout.activity_bihua);
		initData();
		initView();
	}

	private void initData()
	{
		// dialog = "武汉恒信,欢迎您的光临!".trim();
		Intent intent = getIntent();
		if (intent != null && intent.getStringExtra("dialog") != null)
		{
			dialog = intent.getStringExtra("dialog").trim();
		}
		else
		{
			dialog = "武汉恒信,欢迎您的光临!".trim();
		}
	}

	private void initView()
	{
		((Button) findViewById(R.id.button1)).setOnClickListener(this);
		gallery = (Gallery) findViewById(R.id.gallery);
		et = ((EditText) findViewById(R.id.editText1));
		GalleryUtil.getBihuaGallery(this, gallery, dialog, this);
	}

	@Override
	public void onClick(View v)
	{
		String string = et.getText().toString().trim();
		if (BasicStringUtil.isNotNullString(string))
		{
			SwfPlayMgr.reCreateHtml(string);
			startBhHtml();
		}
	}

	private void startBhHtml()
	{
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("file:///" + SwfPlayMgr.SWF_HTML);
		intent.setData(content_url);
		intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
		startActivity(intent);
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
		et.setText(str);
		ToastUtil.showShortToast(this, str);
	}
}
