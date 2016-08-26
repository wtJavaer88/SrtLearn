package com.wnc.srtlearn.tts;

import com.baidu.tts.client.SpeechSynthesizer;

/**
 * 加abstract是为了防止别人直接实例化
 * 
 * @author wnc
 *
 */
public abstract class BdTextToSpeech
{
	public static final String APP_ID = "8483663";// 请更换为自己创建的应用
	public static final String API_KEY = "9YM9iZpG45u67k4GFLpr1VNG";// 请更换为自己创建的应用
	public static final String SECRET_KEY = "3b517268f52cb6ae123f3eb4ee305d38";// 请更换为自己创建的应用

	protected SpeechSynthesizer mSpeechSynthesizer;

	public int speak(String text)
	{
		if (this.mSpeechSynthesizer != null)
			return this.mSpeechSynthesizer.speak(text);
		return 0;
	}

	public int stop()
	{
		if (this.mSpeechSynthesizer != null)
		{
			return this.mSpeechSynthesizer.stop();
		}
		return 0;
	}

	public void release()
	{
		if (mSpeechSynthesizer != null)
		{
			this.mSpeechSynthesizer.release();
		}
	}

}
