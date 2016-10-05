package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.srtlearn.R;
import com.wnc.srtlearn.modules.srt.ReadFavoriteSrt;
import com.wnc.srtlearn.modules.tts.BdTextToOfflineSpeech;
import com.wnc.srtlearn.modules.tts.BdTextToOnlineSpeech;
import com.wnc.srtlearn.modules.tts.BdTextToSpeech;
import com.wnc.srtlearn.vo.FavoriteSrtInfoVo;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.CtrlableVerGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

public class FavoriteSrtActivity extends BaseHorActivity implements UncaughtExceptionHandler, OnClickListener, CtrlableVerGestureDetectorListener, CtrlableHorGestureDetectorListener
{

	List<com.wnc.srtlearn.vo.FavoriteSrtInfoVo> list = new ArrayList<FavoriteSrtInfoVo>();
	int index = 0;
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_srtview);
		// 设置未捕获异常UncaughtExceptionHandler的处理方法
		Thread.setDefaultUncaughtExceptionHandler(this);
		list = ReadFavoriteSrt.getFSInfos();
		System.out.println(list.size());
		initView();
		setContent();
		this.gestureDetector = new GestureDetector(this, new MyCtrlableGestureDetector(this, 0.1, 0.2, this, this));
		// SaveFavoriteSrtToDb.save(this);
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

		((Button) findViewById(R.id.btnViewContext)).setOnClickListener(this);
		((Button) findViewById(R.id.btnFirst)).setOnClickListener(this);
		((Button) findViewById(R.id.btnLast)).setOnClickListener(this);
		((Button) findViewById(R.id.btnSpeakCh)).setOnClickListener(this);
		((Button) findViewById(R.id.btnSpeakEn)).setOnClickListener(this);
		((Button) findViewById(R.id.btnSrtSrh)).setOnClickListener(this);
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
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnViewContext:
			startActivity(new Intent().setClass(this, SrtActivity.class).putExtra("srtFilePath", list.get(index).getSrtFile()).putExtra("seektime", list.get(index).getFromTime().toString()));
			break;
		case R.id.btnFirst:
			index = 0;
			setContent();
			break;
		case R.id.btnLast:
			index = list.size() - 1;
			setContent();
			break;
		case R.id.btnSpeakCh:
			if (bdTextToSpeech != null)
			{
				bdTextToSpeech.stop();
			}
			bdTextToSpeech = BdTextToOnlineSpeech.getInstance(FavoriteSrtActivity.this);
			bdTextToSpeech.speak(chsTv.getText().toString());
			break;
		case R.id.btnSpeakEn:
			if (bdTextToSpeech != null)
			{
				bdTextToSpeech.stop();
			}
			bdTextToSpeech = BdTextToOfflineSpeech.getInstance(FavoriteSrtActivity.this);
			bdTextToSpeech.speak(engTv.getText().toString());
			break;
		case R.id.btnSrtSrh:
			Intent intent = new Intent(FavoriteSrtActivity.this, SrtSearchActivity.class).putExtra("dialog", engTv.getText().toString());
			startActivity(intent);
			break;
		}

	}

	@Override
	public void doUp(FlingPoint p1, FlingPoint p2)
	{
		if (index > 0)
		{
			index--;
		}
		setContent();
	}

	@Override
	public void doDown(FlingPoint p1, FlingPoint p2)
	{
		if (index < list.size() - 1)
		{
			index++;
		}
		setContent();
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
		if (index > 0)
		{
			index--;
		}
		setContent();
	}

	@Override
	public void doRight(FlingPoint p1, FlingPoint p2)
	{
		if (index < list.size() - 1)
		{
			index++;
		}
		setContent();
	}
}
