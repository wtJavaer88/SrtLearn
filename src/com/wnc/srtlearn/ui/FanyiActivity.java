package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import translate.abs.ITranslate;
import translate.site.baidu.BaiduPrographTranslate;
import translate.site.dict.DictTranslate;
import translate.site.iciba.CibaTranslate;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import common.app.ToastUtil;

public class FanyiActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{

    private EditText et;
    String dialog = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

        Thread.setDefaultUncaughtExceptionHandler(this);
        setContentView(R.layout.activity_fanyi);
        initData();
        initView();
    }

    private void initData()
    {
        // dialog = "武汉恒信,欢迎您的光临!".trim();
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("dialog") != null)
        {
            dialog = intent.getStringExtra("dialog").trim();
        }
        else
        {
            dialog = "pretty".trim();
        }
    }

    private void initView()
    {
        ((Button) findViewById(R.id.btnBDFanyi))
                .setOnClickListener(new TransListener());
        ((Button) findViewById(R.id.btnCBFanyi))
                .setOnClickListener(new TransListener());
        ((Button) findViewById(R.id.btnHCFanyi))
                .setOnClickListener(new TransListener());
        et = ((EditText) findViewById(R.id.etFanyi));
        et.setText(dialog);
    }

    class TransListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            String word = et.getText().toString().trim();
            if (BasicStringUtil.isNullString(word))
            {
                return;
            }

            ITranslate engTranslate = null;
            switch (v.getId())
            {
            case R.id.btnBDFanyi:
                engTranslate = new BaiduPrographTranslate(word);
                break;
            case R.id.btnCBFanyi:
                if (isSingleWord(word))
                {
                    engTranslate = new CibaTranslate(word);
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(),
                            "词霸只能查单个单词!");
                }
                break;
            case R.id.btnHCFanyi:
                if (isSingleWord(word))
                {
                    engTranslate = new DictTranslate(word);
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(),
                            "海词只能查单个单词!");
                }
                break;
            default:
                break;
            }
            if (engTranslate != null)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(engTranslate.getWebUrlForMobile()));
                startActivity(intent);
            }
        }

        private boolean isSingleWord(String word)
        {
            return word.split(" ").length == 1;
        }

    }

    @Override
    public void onClick(View v)
    {

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
