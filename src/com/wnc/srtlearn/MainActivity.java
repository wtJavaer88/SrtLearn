package com.wnc.srtlearn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

public class MainActivity extends Activity implements
        SpeechSynthesizerListener, UncaughtExceptionHandler
{
    private SpeechSynthesizer mSpeechSynthesizer;// �ٶ������ϳɿͻ���
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license_2016-08-12";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private static final String APP_ID = "8483663";// �����Ϊ�Լ�������Ӧ��
    private static final String API_KEY = "9YM9iZpG45u67k4GFLpr1VNG";// �����Ϊ�Լ�������Ӧ��
    private static final String SECRET_KEY = "3b517268f52cb6ae123f3eb4ee305d38";// �����Ϊ�Լ�������Ӧ��
    private final String TAG = "SPEEK";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ����δ�����쳣UncaughtExceptionHandler�Ĵ�����
        Thread.setDefaultUncaughtExceptionHandler(this);
        initialEnv();
        initialTts();
        initView();
    }

    @Override
    protected void onDestroy()
    {
        this.mSpeechSynthesizer.release();// �ͷ���Դ
        super.onDestroy();
    }

    private EditText edt_content;
    private Button btn_speak;

    private void initView()
    {
        edt_content = (EditText) findViewById(R.id.edt_content);
        btn_speak = (Button) findViewById(R.id.btn_speak);
        btn_speak.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String content = edt_content.getText().toString();
                mSpeechSynthesizer.speak(content);
                Log.i(TAG, ">>>say: " + edt_content.getText().toString());
            }
        });
    }

    /**
     * ��ʼ�������ϳɿͻ��˲�����
     */
    private void initialTts()
    {
        // ��ȡ�����ϳɶ���ʵ��
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        // ����Context
        this.mSpeechSynthesizer.setContext(this);
        // ���������ϳ�״̬����
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // �ı�ģ���ļ�·�� (��������ʹ��)
        this.mSpeechSynthesizer.setParam(
                SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath
                        + "/" + TEXT_MODEL_NAME);
        // ��ѧģ���ļ�·�� (��������ʹ��)
        this.mSpeechSynthesizer.setParam(
                SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath
                        + "/" + SPEECH_FEMALE_MODEL_NAME);
        // ������Ȩ�ļ�·��,��δ���ý�ʹ��Ĭ��·��.������ʱ��Ȩ�ļ�·����LICENCE_FILE_NAME���滻����ʱ��Ȩ�ļ���ʵ��·����
        // ����ʹ����ʱlicense�ļ�ʱ��Ҫ�������ã������[Ӧ�ù���]�п�ͨ��������Ȩ��
        // ����Ҫ���øò��������齫���д���ɾ�����������棩
        this.mSpeechSynthesizer.setParam(
                SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                        + LICENSE_FILE_NAME);
        // ���滻Ϊ����������ƽ̨��ע��Ӧ�õõ���App ID (������Ȩ)
        this.mSpeechSynthesizer.setAppId(APP_ID);
        // ���滻Ϊ����������ƽ̨ע��Ӧ�õõ���apikey��secretkey (������Ȩ)
        this.mSpeechSynthesizer.setApiKey(API_KEY, SECRET_KEY);
        // �����ˣ��������棩�����ò���Ϊ0,1,2,3������
        // ���������˻ᶯ̬���ӣ���ֵ����ο��ĵ������ĵ�˵��Ϊ׼��0--��ͨŮ����1--��ͨ������2--�ر�������3--���������������
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // ����Mixģʽ�ĺϳɲ���
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE,
                SpeechSynthesizer.MIX_MODE_DEFAULT);
        // ��Ȩ���ӿ�(���Բ�ʹ�ã�ֻ����֤��Ȩ�Ƿ�ɹ�)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess())
        {
            Log.i(TAG, ">>>auth success.");
        }
        else
        {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Log.i(TAG, ">>>auth failed errorMsg: " + errorMsg);
        }
        // �����ʼ��tts�ӿ�
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // ��������Ӣ����Դ���ṩ����Ӣ�ĺϳɹ��ܣ�
        int result = mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        Log.i(TAG, ">>>loadEnglishModel result: " + result);
    }

    @Override
    public void onSynthesizeStart(String s)
    {
        // �������ϳɿ�ʼ
        Log.i(TAG, ">>>onSynthesizeStart()<<< s: " + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i)
    {
        // �������кϳ����ݵ���
        Log.i(TAG, ">>>onSynthesizeDataArrived()<<< s: " + s);
    }

    @Override
    public void onSynthesizeFinish(String s)
    {
        // �������ϳɽ���
        Log.i(TAG, ">>>onSynthesizeFinish()<<< s: " + s);
    }

    @Override
    public void onSpeechStart(String s)
    {
        // �������ϳɲ���ʼ����
        Log.i(TAG, ">>>onSpeechStart()<<< s: " + s);
    }

    @Override
    public void onSpeechProgressChanged(String s, int i)
    {
        // ���������Ž����б仯
        Log.i(TAG, ">>>onSpeechProgressChanged()<<< s: " + s);
    }

    @Override
    public void onSpeechFinish(String s)
    {
        // ���������Ž���
        Log.i(TAG, ">>>onSpeechFinish()<<< s: " + s);
    }

    @Override
    public void onError(String s, SpeechError speechError)
    {
        // ����������
        Log.i(TAG, ">>>onError()<<< description: " + speechError.description
                + ", code: " + speechError.code);
    }

    private void initialEnv()
    {
        if (mSampleDirPath == null)
        {
            String sdcardPath = Environment.getExternalStorageDirectory()
                    .toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
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

    /**
     * ��������Ҫ����Դ�ļ�������SD����ʹ�ã���Ȩ�ļ�Ϊ��ʱ��Ȩ�ļ�����ע����ʽ��Ȩ��
     * 
     * @param isCover
     *            �Ƿ񸲸��Ѵ��ڵ�Ŀ���ļ�
     * @param source
     * @param dest
     */
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
                is = getResources().getAssets().open(source);
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
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }

}
