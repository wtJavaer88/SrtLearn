package com.wnc.srtlearn;

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
public class BdTextToNetSpeech implements SpeechSynthesizerListener,
        BdTextToSpeech
{
    private static final String TAG = "BdTextToSpeech";

    private static Context context;
    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String LICENSE_FILE_NAME = "temp_license_2016-08-12";
    private static final String APP_ID = "8483663";// 请更换为自己创建的应用
    private static final String API_KEY = "9YM9iZpG45u67k4GFLpr1VNG";// 请更换为自己创建的应用
    private static final String SECRET_KEY = "3b517268f52cb6ae123f3eb4ee305d38";// 请更换为自己创建的应用

    private static BdTextToNetSpeech ourInstance;

    public static synchronized BdTextToNetSpeech getInstance(Context context)
    {
        if (ourInstance == null)
        {
            ourInstance = new BdTextToNetSpeech(context);
        }
        return ourInstance;
    }

    private BdTextToNetSpeech(Context context)
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
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE,
                mSampleDirPath + "/" + LICENSE_FILE_NAME);
        mSpeechSynthesizer.setAppId(APP_ID);
        mSpeechSynthesizer.setApiKey(API_KEY, SECRET_KEY);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE,
                SpeechSynthesizer.MIX_MODE_DEFAULT);
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
    public int speak(String text)
    {
        return this.mSpeechSynthesizer.speak(text);
    }

    public void release()
    {
        if (mSpeechSynthesizer != null)
        {
            this.mSpeechSynthesizer.release();
        }
    }

    @Override
    public void onSynthesizeStart(String s)
    {
        Log.i(TAG, ">>>onSynthesizeStart()<<< s: " + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i)
    {
        Log.i(TAG, ">>>onSynthesizeDataArrived()<<< s: " + s + "  总字节数:"
                + bytes.length);
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
        Log.i(TAG, ">>>onError()<<< description: " + speechError.description
                + ", code: " + speechError.code);
    }
}
