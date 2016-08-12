package com.wnc.srtlearn;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements UncaughtExceptionHandler
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
                // mSpeechSynthesizer.speak(content);
                // Log.i(TAG, ">>>say: " + edt_content.getText().toString());
                BdTextToSpeech bdTextToSpeech = BdTextToLocalSpeech
                        .getInstance(MainActivity.this);
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
