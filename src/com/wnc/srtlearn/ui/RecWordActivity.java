package com.wnc.srtlearn.ui;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.modules.tts.Config;
import com.wnc.srtlearn.modules.tts.RecCallBack;
import com.wnc.srtlearn.modules.tts.RecDialogUtil;
import com.wnc.srtlearn.monitor.ActiveWork;
import com.wnc.srtlearn.monitor.StudyMonitor;
import com.wnc.srtlearn.monitor.WORKTYPE;
import common.app.GalleryUtil;
import common.app.ToastUtil;
import common.app.VoicePlayerUtil;
import common.app.WheelDialogShowUtil;
import common.uihelper.AfterGalleryChooseListener;
import common.uihelper.AfterWheelChooseListener;
import common.utils.TextFormatUtil;

public class RecWordActivity extends BaseActivity implements OnClickListener, UncaughtExceptionHandler, AfterGalleryChooseListener, RecCallBack, AfterWheelChooseListener
{
	private Gallery gallery;
	private EditText et;
	// 百度自定义对话框
	private BaiduASRDigitalDialog recDialog = null;
	String dialog;

	int READ_MODE = 0;
	String curWordContent = "";
	String curSelContent = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

		Thread.setDefaultUncaughtExceptionHandler(this);
		setContentView(R.layout.activity_recword);
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
		System.out.println(dialog);
		curWordContent = "";
		curSelContent = "";
	}

	private void initView()
	{
		RecClickListener recClickListener = new RecClickListener();
		((Button) findViewById(R.id.btn_recword)).setOnClickListener(recClickListener);
		((Button) findViewById(R.id.btn_recdialog)).setOnClickListener(recClickListener);
		((Button) findViewById(R.id.btn_reccustom)).setOnClickListener(recClickListener);
		((Button) findViewById(R.id.btn_recrepeat)).setOnClickListener(recClickListener);
		gallery = (Gallery) findViewById(R.id.recword_gallery);
		et = ((EditText) findViewById(R.id.et_recresult));
		et.setVisibility(View.INVISIBLE);
		// GalleryUtil.getPinyinGallery(this, gallery, dialog, this);
		GalleryUtil.getYuyinGallery(this, gallery, dialog, this);
	}

	ActiveWork activeWork;

	public class RecClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{

			activeWork = StudyMonitor.peekWork(WORKTYPE.TTS_REC);
			switch (v.getId())
			{
			case R.id.btn_recword:
				READ_MODE = 1;
				et.setVisibility(View.INVISIBLE);
				((TextView) findViewById(R.id.tv_rectip)).setText("单字 <" + curWordContent + "> 朗读:");
				speakChs_Baidu();
				break;
			case R.id.btn_recdialog:
				READ_MODE = 2;
				et.setVisibility(View.INVISIBLE);
				((TextView) findViewById(R.id.tv_rectip)).setText("整段 <" + dialog + "> 朗读:");
				speakChs_Baidu();
				break;
			case R.id.btn_reccustom:
				READ_MODE = 3;
				System.out.println("READ_MODE1:" + READ_MODE);
				et.setVisibility(View.INVISIBLE);
				WheelDialogShowUtil.showHanziDialog(RecWordActivity.this, dialog, 0, 0, RecWordActivity.this);
				break;
			case R.id.btn_recrepeat:
				if (READ_MODE == 0)
				{
					ToastUtil.showShortToast(getApplicationContext(), "还没有选择要识别的字词!");
					return;
				}
				et.setVisibility(View.INVISIBLE);
				speakChs_Baidu();
				break;
			}
		}

	}

	@Override
	public void onClick(View v)
	{

	}

	// 百度语音识别
	public void speakChs_Baidu()
	{
		try
		{
			Config.setCurrentLanguageIndex(0);
			if (recDialog != null)
			{
				recDialog.dismiss();
			}
			recDialog = RecDialogUtil.getDialog(this, this);
			recDialog.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		System.out.println("uncaughtException: " + ex.getMessage());
		for (StackTraceElement o : ex.getStackTrace())
		{
			System.out.println(o.toString());
		}
	}

	@Override
	public void listenComplete(String content)
	{
		StudyMonitor.addActiveWork(activeWork);
		et.setVisibility(View.VISIBLE);
		et.setText(content);
		try
		{
			if (isCorrect(content))
			{
				System.out.println("OK!");
				VoicePlayerUtil.playAssetVoice(this, "perfect.wav");
				ToastUtil.showLongToast(getApplicationContext(), "你太棒了!");
			}
			else
			{
				VoicePlayerUtil.playAssetVoice(this, "jxnl.wav");
				ToastUtil.showLongToast(getApplicationContext(), "继续努力啊!");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean isCorrect(String content)
	{
		boolean result = false;
		if (READ_MODE == 1 && equalsNoSymbol(content, curWordContent))
		{
			result = true;
		}
		if (READ_MODE == 2 && equalsNoSymbol(content, dialog))
		{
			result = true;
		}
		if (READ_MODE == 3 && equalsNoSymbol(content, curSelContent))
		{
			result = true;
		}
		return result;
	}

	private boolean equalsNoSymbol(String paramStr1, String paramStr2)
	{
		System.out.println("READ_MODE:" + READ_MODE);
		System.out.println(TextFormatUtil.getTextNoSymbol(paramStr1) + " " + TextFormatUtil.getTextNoSymbol(paramStr2));
		return TextFormatUtil.getTextNoSymbol(paramStr1).equals(TextFormatUtil.getTextNoSymbol(paramStr2));
	}

	/**
	 * 返回选中汉字的原始索引和原拼音
	 */
	@Override
	public void afterGalleryChoose(String str)
	{
		curWordContent = str;
	}

	@Override
	public void afterWheelChoose(Object... objs)
	{
		curSelContent = dialog.substring(Integer.parseInt(objs[0].toString()), 1 + Integer.parseInt(objs[1].toString()));
		((TextView) findViewById(R.id.tv_rectip)).setText("自定义 <" + curSelContent + "> 朗读:");
		READ_MODE = 3;
		speakChs_Baidu();
	}

	@Override
	public void onBackPressed()
	{
		System.out.println("back press");
		// 这里处理逻辑代码，大家注意：该方法仅适用于2.0或更新版的sdk
		RecDialogUtil.reset();
		super.onBackPressed();
		super.onDestroy();
	}
}
