package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.tts.CallBack;
import com.wnc.srtlearn.tts.Config;
import com.wnc.srtlearn.tts.RecDialogUtil;
import common.app.GalleryUtil;
import common.app.ToastUtil;
import common.app.WheelDialogShowUtil;
import common.uihelper.AfterGalleryChooseListener;
import common.uihelper.AfterWheelChooseListener;

public class RecWordActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler, AfterGalleryChooseListener, CallBack,
        AfterWheelChooseListener
{
    final String swfHtml = Environment.getExternalStorageDirectory().getPath()
            + "/wnc/app/swfplayer/swfplayer.htm";
    private Gallery gallery;
    private EditText et;
    // 百度自定义对话框
    private BaiduASRDigitalDialog mDialog = null;
    String dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

        Thread.setDefaultUncaughtExceptionHandler(this);
        setContentView(R.layout.activity_recword);
        initData();
        initView();
    }

    private void initData()
    {
        dialog = "武汉恒信,欢迎您的光临!".trim();
    }

    private void initView()
    {
        ((Button) findViewById(R.id.btn_recword)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_recdialog)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_reccustom)).setOnClickListener(this);
        gallery = (Gallery) findViewById(R.id.recword_gallery);
        et = ((EditText) findViewById(R.id.et_recresult));
        et.setVisibility(View.INVISIBLE);
        // GalleryUtil.getPinyinGallery(this, gallery, dialog, this);
        GalleryUtil.getYuyinGallery(this, gallery, dialog, this);
    }

    int READ_MODE = 1;
    String curWordContent = "";

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_recword:
            READ_MODE = 1;
            et.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.tv_rectip)).setText("单字 <"
                    + curWordContent + "> 朗读:");
            speakChs_Baidu();
            break;
        case R.id.btn_recdialog:
            READ_MODE = 2;
            et.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.tv_rectip)).setText("整段 <" + dialog
                    + "> 朗读:");
            speakChs_Baidu();
            break;
        case R.id.btn_reccustom:
            READ_MODE = 3;
            System.out.println(dialog + dialog.length());
            WheelDialogShowUtil.showHanziDialog(this, dialog, 0, 0, this);
            break;
        }
    }

    // 百度语音识别
    public void speakChs_Baidu()
    {
        Config.setCurrentLanguageIndex(0);
        if (mDialog != null)
        {
            mDialog.dismiss();
        }
        mDialog = RecDialogUtil.getDialog(this, this);
        mDialog.show();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        System.out.println("uncaughtException: " + ex.getMessage());
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }

    /**
     * 返回选中汉字的原始索引和原拼音
     */
    @Override
    public void afterGalleryChoose(String str)
    {
        if (READ_MODE == 1)
        {
            curWordContent = str;
        }
    }

    @Override
    public void listenComplete(String content)
    {
        et.setVisibility(View.VISIBLE);
        et.setText(content);
        content = getTextNoSymbol(content);
        boolean result = false;
        if ((READ_MODE == 1 || READ_MODE == 3)
                && content.equals(curWordContent))
        {
            result = true;
        }
        if (READ_MODE == 2 && content.equals(dialog))
        {
            result = true;
        }
        if (result)
        {
            System.out.println("OK!");
            ToastUtil.showLongToast(getApplicationContext(), "你好棒!");
        }
        else
        {
            ToastUtil.showLongToast(getApplicationContext(), "还差一点!");
        }
    }

    public String getTextNoSymbol(String s)
    {
        return s.trim().replaceAll("[,\\.!?，。！？、]", "");
    }

    @Override
    public void afterWheelChoose(Object... objs)
    {
        curWordContent = getTextNoSymbol(dialog.substring(
                Integer.parseInt(objs[0].toString()),
                1 + Integer.parseInt(objs[1].toString())));
        ((TextView) findViewById(R.id.tv_rectip)).setText("自定义 <"
                + curWordContent + "> 朗读:");
        speakChs_Baidu();
    }
}
