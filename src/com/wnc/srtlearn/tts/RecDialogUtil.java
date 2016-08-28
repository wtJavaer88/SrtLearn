package com.wnc.srtlearn.tts;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

public class RecDialogUtil
{
	// 提供一个方法,调用方推出的时候调用,清除静态变量
	public static void reset()
	{
		mRecognitionListener = null;
		mDialog = null;
	}

	static BaiduASRDigitalDialog mDialog;
	// 对话框监听
	static DialogRecognitionListener mRecognitionListener;

	public static BaiduASRDigitalDialog getDialog(Activity recActivity, final RecCallBack callback)
	{
		if (mDialog != null)
		{
			return mDialog;
		}
		Bundle params = new Bundle();
		// 设置注册百度开放平台得到的值 API_KEY,SECRET_KEY
		params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, BdTextToSpeech.API_KEY);
		params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, BdTextToSpeech.SECRET_KEY);
		// 设置对话框模式
		params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
		// 根据设置新建对话框
		mDialog = new BaiduASRDigitalDialog(recActivity, params);

		if (mRecognitionListener == null)
		{
			mRecognitionListener = new DialogRecognitionListener()
			{
				@Override
				public void onResults(Bundle results)
				{
					ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION) : null;
					if (rs != null && rs.size() > 0)
					{
						Log.i("RecResult", rs.get(0));
						callback.listenComplete(rs.get(0));
					}
				}
			};
		}
		// 设置对话框的监听
		mDialog.setDialogRecognitionListener(mRecognitionListener);
		// 对话框设置
		mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
		mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE, Config.getCurrentLanguage());
		mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
		mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
		mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);

		return mDialog;
	}
}
