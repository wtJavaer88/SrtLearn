package com.wnc.srtlearn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.tts.CallBack;
import com.wnc.srtlearn.tts.Config;
import com.wnc.srtlearn.tts.RecDialogUtil;

public class TTSActivity extends Activity implements CallBack
{

    // 百度自定义对话框
    private BaiduASRDigitalDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);

        ((Button) findViewById(R.id.recbtn))
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Config.setCurrentLanguageIndex(0);
                        speak_Baidu();
                    }
                });
        ((Button) findViewById(R.id.recbtn2))
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Config.setCurrentLanguageIndex(2);
                        speak_Baidu();
                    }
                });
        ((Button) findViewById(R.id.sayBtn))
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        startActivity(new Intent(getApplicationContext(),
                                ReadActivity.class));
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
