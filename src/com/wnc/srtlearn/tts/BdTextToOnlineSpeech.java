package com.wnc.srtlearn.tts;

import android.content.Context;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * Created by JunkChen on 2016/4/5 0005.
 */
public class BdTextToOnlineSpeech extends BdTextToSpeech implements SpeechSynthesizerListener
{
	private static final String TAG = "BdTextToLocalSpeech";
	private Context context;
	private String mSampleDirPath;

	private static BdTextToOnlineSpeech ourInstance;

	public static synchronized BdTextToOnlineSpeech getInstance(Context context)
	{
		if (ourInstance == null)
		{
			ourInstance = new BdTextToOnlineSpeech(context);
		}
		return ourInstance;
	}

	private BdTextToOnlineSpeech(Context context)
	{
		Log.i(TAG, ">>>BdTextToSpeech executed.<<<");
		this.context = context;
		initialTts();
	}

	/**
	 * 初始化语音合成客户端并启动
	 */
	private void initialTts()
	{
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(context);
		mSpeechSynthesizer.setSpeechSynthesizerListener(this);
		mSpeechSynthesizer.setAppId(APP_ID);
		mSpeechSynthesizer.setApiKey(API_KEY, SECRET_KEY);
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
		if (authInfo.isSuccess())
		{
			Log.i(TAG, ">>>auth success.");
		}
		else
		{
			String errorMsg = authInfo.getTtsError().getDetailMessage();
			Log.i(TAG, ">>>auth failed errorMsg: " + errorMsg);
		}
		mSpeechSynthesizer.initTts(TtsMode.MIX);
	}

	@Override
	public void onSynthesizeStart(String s)
	{
		Log.i(TAG, ">>>onSynthesizeStart()<<< s: " + s);
	}

	@Override
	public void onSynthesizeDataArrived(String s, byte[] bytes, int i)
	{
		Log.i(TAG, ">>>onSynthesizeDataArrived()<<< s: " + s + "  总字节数:" + bytes.length);
	}

	@Override
	public void onSynthesizeFinish(String s)
	{
		Log.i(TAG, ">>>onSynthesizeFinish()<<< s: " + s);
	}

	@Override
	public void onSpeechStart(String s)
	{
		Log.i(TAG, ">>>onSpeechStart()<<< s: " + s);
	}

	@Override
	public void onSpeechProgressChanged(String s, int i)
	{
		Log.i(TAG, ">>>onSpeechProgressChanged()<<< s: " + s);
	}

	@Override
	public void onSpeechFinish(String s)
	{
		Log.i(TAG, ">>>onSpeechFinish()<<< s: " + s);
	}

	@Override
	public void onError(String s, SpeechError speechError)
	{
		Log.i(TAG, ">>>onError()<<< description: " + speechError.description + ", code: " + speechError.code);
	}
}
