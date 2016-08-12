package com.wnc.srtlearn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * Created by JunkChen on 2016/4/5 0005.
 */
public class BdTextToLocalSpeech implements SpeechSynthesizerListener,
        BdTextToSpeech
{
    private static final String TAG = "BdTextToSpeech";

    private Context context;
    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license_2016-08-12";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private static final String APP_ID = "8483663";
    private static final String API_KEY = "9YM9iZpG45u67k4GFLpr1VNG";
    private static final String SECRET_KEY = "3b517268f52cb6ae123f3eb4ee305d38";
    private static BdTextToLocalSpeech ourInstance;

    public static synchronized BdTextToLocalSpeech getInstance(Context context)
    {
        if (ourInstance == null)
        {
            ourInstance = new BdTextToLocalSpeech(context);
        }
        return ourInstance;
    }

    private BdTextToLocalSpeech(Context context)
    {
        Log.i(TAG, ">>>BdTextToSpeech executed.<<<");
        this.context = context;
        initialEnv();
        initialTts();
    }

    /**
     * ��ʼ������ϳɿͻ��˲���
     */
    private void initialTts()
    {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(context);
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        mSpeechSynthesizer.setParam(
                SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath
                        + "/" + TEXT_MODEL_NAME);
        mSpeechSynthesizer.setParam(
                SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath
                        + "/" + SPEECH_FEMALE_MODEL_NAME);
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
        int result = mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        Log.i(TAG, ">>>loadEnglishModel result: " + result);
    }

    private void initialEnv()
    {
        if (mSampleDirPath == null)
        {
            String sdcardPath = Environment.getExternalStorageDirectory()
                    .toString();
            mSampleDirPath = sdcardPath + "/RiiECG/" + SAMPLE_DIR_NAME;
        }
        File file = new File(mSampleDirPath);
        if (!file.exists())
        {
            file.mkdirs();
        }
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath
                + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath
                + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME,
                mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME);
    }

    public void copyFromAssetsToSdcard(boolean isCover, String source,
            String dest)
    {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists()))
        {
            InputStream is = null;
            FileOutputStream fos = null;
            try
            {
                is = context.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0)
                {
                    fos.write(buffer, 0, size);
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (fos != null)
                {
                    try
                    {
                        fos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                try
                {
                    if (is != null)
                    {
                        is.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
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
