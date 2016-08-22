package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wnc.srtlearn.R;
import com.wnc.srtlearn.R.id;
import com.wnc.srtlearn.R.layout;
import com.wnc.srtlearn.tts.BdTextToLocalSpeech;
import com.wnc.srtlearn.tts.BdTextToOnlineSpeech;
import com.wnc.srtlearn.tts.BdTextToSpeech;

public class ReadActivity extends Activity implements UncaughtExceptionHandler
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置未捕获异常UncaughtExceptionHandler的处理方法
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
                                .getInstance(ReadActivity.this);
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
                                .getInstance(ReadActivity.this);
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
