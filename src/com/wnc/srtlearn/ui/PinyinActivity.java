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

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.R;
import com.wnc.string.PatternUtil;
import common.app.GalleryUtil;
import common.uihelper.AfterGalleryChooseListener;
import common.utils.PinYinUtil;

public class PinyinActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler, AfterGalleryChooseListener
{
    final String swfHtml = Environment.getExternalStorageDirectory().getPath()
            + "/wnc/app/swfplayer/swfplayer.htm";
    private Gallery gallery;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

        Thread.setDefaultUncaughtExceptionHandler(this);
        setContentView(R.layout.activity_pinyin);
        initView();
    }

    String dialog = "武汉恒信,欢迎您的光临!";
    String pinyin = PinYinUtil.getSeveralPinyin(dialog);

    private void initView()
    {
        ((Button) findViewById(R.id.pinyin_ok)).setOnClickListener(this);
        gallery = (Gallery) findViewById(R.id.pinyin_gallery);
        et = ((EditText) findViewById(R.id.pinyin_et));

        // GalleryUtil.getPinyinGallery(this, gallery, dialog, this);
        GalleryUtil.getPinyinGallery(this, gallery, dialog, pinyin, this);
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

    /**
     * 返回选中汉字的原始索引和原拼音
     */
    @Override
    public void afterGalleryChoose(String str)
    {
        final int number = BasicNumberUtil.getNumber(PatternUtil
                .getFirstPattern(str, "\\d+"));
        et.setText(PatternUtil.getFirstPattern(str, ":.*+").replace(":", ""));
        char charAt = dialog.charAt(number);
        System.out.println(charAt);
    }
}
