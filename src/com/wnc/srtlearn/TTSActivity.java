package com.wnc.srtlearn;

import java.lang.Thread.UncaughtExceptionHandler;

import com.wnc.srtlearn.tts.BdTextToLocalSpeech;
import com.wnc.srtlearn.tts.BdTextToOnlineSpeech;
import com.wnc.srtlearn.tts.BdTextToSpeech;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TTSActivity extends Activity implements UncaughtExceptionHandler
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ����δ�����쳣UncaughtExceptionHandler�Ĵ�����
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private EditText edt_content;
    private Button btn_speak;
    BdTextToSpeech bdTextToSpeech;

    private void initView()
    {
        edt_content = (EditText) findViewById(R.id.edt_content);
        ((Button) findViewById(R.id.btn_speakonline))
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String content = edt_content.getText().toString();
                        bdTextToSpeech = BdTextToOnlineSpeech
                                .getInstance(TTSActivity.this);
                        bdTextToSpeech.speak(content);
                    }
                });
        ((Button) findViewById(R.id.btn_speaklocal))
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String content = edt_content.getText().toString();
                        bdTextToSpeech = BdTextToLocalSpeech
                                .getInstance(TTSActivity.this);
                        bdTextToSpeech.speak(content);
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
