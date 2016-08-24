package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.wnc.srtlearn.R;
import common.utils.PinYinUtil;

public class MainActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

        Thread.setDefaultUncaughtExceptionHandler(this);
        setContentView(R.layout.activity_main);
        initView();
    }

    String dialog = "武汉恒信,欢迎您的光临!";
    String pinyin = PinYinUtil.getSeveralPinyin(dialog);

    private void initView()
    {
        ((Button) findViewById(R.id.btn_bihua)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_pinyin)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_tts)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_srt)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_favsrt)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_bihua:
            gotoAct(BihuaActivity.class);
            break;
        case R.id.btn_pinyin:
            gotoAct(PinyinActivity.class);
            break;
        case R.id.btn_tts:
            gotoAct(TTSActivity.class);
            break;
        case R.id.btn_srt:
            gotoAct(SrtActivity.class);
            break;
        case R.id.btn_favsrt:
            gotoAct(FavoriteSrtActivity.class);
            break;
        }
    }

    private void gotoAct(Class clazz)
    {
        startActivity(new Intent(this, clazz));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        System.out.println(ex.getMessage());
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }

}
