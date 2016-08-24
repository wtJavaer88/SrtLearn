package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import srt.FavoriteSrtInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.srtlearn.R;
import com.wnc.srtlearn.tts.BdTextToOnlineSpeech;
import com.wnc.srtlearn.tts.BdTextToSpeech;

public class FavoriteSrtActivity extends Activity implements UncaughtExceptionHandler
{

	List<srt.FavoriteSrtInfo> list = new ArrayList<FavoriteSrtInfo>();
	int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_srtview);
		// 设置未捕获异常UncaughtExceptionHandler的处理方法
		Thread.setDefaultUncaughtExceptionHandler(this);
		list = TestReadSrt.getFSInfos();
		System.out.println(list.size());
		initView();
		setContent();
	}

	BdTextToSpeech bdTextToSpeech;
	private TextView chsTv;
	private TextView engTv;

	private void setContent()
	{
		this.chsTv.setText(list.get(index).getChs());
		this.engTv.setText(list.get(index).getEng());
	}

	private void initView()
	{
		chsTv = (TextView) findViewById(R.id.chs_text);
		engTv = (TextView) findViewById(R.id.eng_text);

		((Button) findViewById(R.id.pre_btn)).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				if (index > 0)
				{
					index--;
				}
				setContent();
			}

		});
		((Button) findViewById(R.id.next_btn)).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				if (index < list.size() - 1)
				{
					index++;
				}
				setContent();
			}

		});
		((Button) findViewById(R.id.btn_speak)).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (bdTextToSpeech != null)
				{
					bdTextToSpeech.stop();
				}
				String content = chsTv.getText().toString();
				bdTextToSpeech = BdTextToOnlineSpeech.getInstance(FavoriteSrtActivity.this);
				bdTextToSpeech.speak(content);
			}
		});
		((Button) findViewById(R.id.btn_rec)).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				System.out.println("RRRRRRRRRRRRRRec");
				startActivity(new Intent(getApplicationContext(), TTSActivity.class));
			}
		});
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

}
