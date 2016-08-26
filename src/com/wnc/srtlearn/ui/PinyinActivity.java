package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.string.PatternUtil;
import common.app.GalleryUtil;
import common.app.ToastUtil;
import common.uihelper.AfterGalleryChooseListener;
import common.utils.PinYinUtil;

public class PinyinActivity extends Activity implements OnClickListener, UncaughtExceptionHandler, AfterGalleryChooseListener
{
	private Gallery gallery;
	private EditText et;

	String dialog;
	String[] pinyinArr;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

		Thread.setDefaultUncaughtExceptionHandler(this);
		setContentView(R.layout.activity_pinyin);
		initData();
		initView();
	}

	private void initData()
	{
		Intent intent = getIntent();
		if (intent != null && intent.getStringExtra("dialog") != null)
		{
			dialog = intent.getStringExtra("dialog").trim();
		}
		else
		{
			dialog = "武汉恒信,欢迎您的光临!".trim();
		}
		if (intent != null && intent.getStringExtra("pinyin") != null)
		{
			pinyinArr = intent.getStringExtra("pinyin").trim().split(" ");
		}
		else
		{
			pinyinArr = PinYinUtil.getSeveralPinyin(dialog).split(" ");
		}
	}

	private void initView()
	{
		((Button) findViewById(R.id.pinyin_ok)).setOnClickListener(this);
		((Button) findViewById(R.id.pinyin_cancel)).setOnClickListener(this);
		gallery = (Gallery) findViewById(R.id.pinyin_gallery);
		et = ((EditText) findViewById(R.id.pinyin_et));

		// GalleryUtil.getPinyinGallery(this, gallery, dialog, this);
		GalleryUtil.getPinyinGallery(this, gallery, dialog, pinyinArr, this);
	}

	int curArrIndex = -1;

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.pinyin_ok:
			String pyContent = et.getText().toString().trim();
			if (BasicStringUtil.isNotNullString(pyContent))
			{
				pinyinArr[curArrIndex] = pyContent;
				System.out.println(Arrays.toString(pinyinArr));
				ToastUtil.showShortToast(this, "修改拼音成功!");
				final int selectedItemPosition = gallery.getSelectedItemPosition();
				GalleryUtil.getPinyinGallery(this, gallery, dialog, pinyinArr, this);
				gallery.setSelection(selectedItemPosition);
			}
			break;
		case R.id.pinyin_cancel:
			setResultAndFinish();
			break;
		}
	}

	private void setResultAndFinish()
	{
		Intent intent = new Intent();
		String ret = "";
		for (String s : pinyinArr)
		{
			ret += " " + s;
		}
		intent.putExtra("pinyin", ret.trim());// 放入返回值
		setResult(0, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
		finish();
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

	/**
	 * 返回选中汉字的原始索引和原拼音
	 */
	@Override
	public void afterGalleryChoose(String str)
	{
		curArrIndex = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(str, "\\d+"));
		et.setText(pinyinArr[curArrIndex]);
		char charAt = dialog.charAt(curArrIndex);
		System.out.println(charAt);
	}
}
