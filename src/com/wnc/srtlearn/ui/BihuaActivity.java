package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;

import com.wnc.basic.BasicFileUtil;
import com.wnc.srtlearn.R;
import common.app.GalleryUtil;
import common.app.ToastUtil;
import common.uihelper.AfterGalleryChooseListener;

public class BihuaActivity extends Activity implements OnClickListener,
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
        setContentView(R.layout.activity_bihua);
        initView();
    }

    private void initView()
    {
        ((Button) findViewById(R.id.button1)).setOnClickListener(this);
        gallery = (Gallery) findViewById(R.id.gallery);
        et = ((EditText) findViewById(R.id.editText1));
        String dialog = "武汉恒信,欢迎您的光临!";
        GalleryUtil.getBihuaGallery(this, gallery, dialog, this);
    }

    @Override
    public void onClick(View v)
    {
        writeSwfData(et.getText().toString());
        startBhHtml();
    }

    private void startBhHtml()
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("file:///" + swfHtml);
        intent.setData(content_url);
        intent.setClassName("com.android.browser",
                "com.android.browser.BrowserActivity");
        startActivity(intent);
    }

    private String writeSwfData(String hanzi)
    {
        StringBuilder accum = new StringBuilder(1024);
        accum.append("<object id=\"forfun\" classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"600\" height=\"600\" "
                + "codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\">\n");
        accum.append(" <param name=\"movie\" value=\"http://zd.diyifanwen.com/Files/WordSwf/"
                + hanzi + ".swf\">\n");
        accum.append("<param name=\"quality\" value=\"high\">\n");
        accum.append("<param name=\"bgcolor\" value=\"#F0F0F0\">\n");
        accum.append("<param name=\"menu\" value=\"false\">\n");
        accum.append("<param name=\"wmode\" value=\"opaque\">\n");
        accum.append("<param name=\"FlashVars\" value=\"\">\n");
        accum.append("<param name=\"allowScriptAccess\" value=\"sameDomain\">\n");
        accum.append("<embed id=\"forfunex\" src=\"http://zd.diyifanwen.com/Files/WordSwf/"
                + hanzi
                + ".swf\" width=\"600\" height=\"600\" align=\"middle\" allowScriptAccess=\"sameDomain\" menu=\"false\""
                + " type=\"application/x-shockwave-flash\" pluginspage=\"http://www.adobe.com/go/getflashplayer\">\n");
        accum.append("</object>");
        BasicFileUtil
                .writeFileString(swfHtml, accum.toString(), "UTF-8", false);
        return accum.toString();
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

    @Override
    public void afterGalleryChoose(String str)
    {
        et.setText(str);
        ToastUtil.showShortToast(this, str);
    }
}