package com.wnc.srtlearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.wnc.srtlearn.tts.CallBack;
import com.wnc.srtlearn.tts.RecDialogUtil;

public class RecActivity extends Activity implements CallBack
{

    // 百度自定义对话框
    private BaiduASRDigitalDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);

        Button btnButton = (Button) findViewById(R.id.mybtn);
        btnButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                speak_Baidu();
            }
        });
        Button ttsButton = (Button) findViewById(R.id.sayBtn);
        ttsButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),
                        TTSActivity.class));
            }
        });

    }

    // 百度语音识别
    public void speak_Baidu()
    {
        if (mDialog != null)
        {
            mDialog.dismiss();
        }
        mDialog = RecDialogUtil.getDialog(this, this);
        mDialog.show();
    }

    @Override
    protected void onDestroy()
    {
        if (mDialog != null)
        {
            mDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void listenComplete(String content)
    {

    }

}
