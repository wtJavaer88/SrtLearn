package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.srtlearn.R;
import com.wnc.srtlearn.dao.DictionaryDao;
import com.wnc.srtlearn.dao.FavDao;
import com.wnc.srtlearn.modules.translate.Topic;
import com.wnc.srtlearn.modules.tts.BdTextToOfflineSpeech;
import com.wnc.srtlearn.modules.tts.BdTextToOnlineSpeech;
import com.wnc.srtlearn.modules.tts.BdTextToSpeech;
import com.wnc.srtlearn.vo.FavoriteSrtInfoVo;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

public class FavoriteSrtActivity extends BaseHorActivity implements
        UncaughtExceptionHandler, OnClickListener,
        CtrlableHorGestureDetectorListener
{

    List<com.wnc.srtlearn.vo.FavoriteSrtInfoVo> list = new ArrayList<FavoriteSrtInfoVo>();
    int index = 0;
    TextView topictip_tv;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srtview);
        // 设置未捕获异常UncaughtExceptionHandler的处理方法
        Thread.setDefaultUncaughtExceptionHandler(this);

        // list = ReadFavoriteSrt.getFSInfos();
        FavDao.openDatabase();
        list = FavDao.search(true, "");
        FavDao.closeDb();

        initView();
        setContent();
        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.1, 0.2, this, null));
        // SaveFavoriteSrtToDb.save(this);
    }

    BdTextToSpeech bdTextToSpeech;
    private TextView chsTv;
    private TextView engTv;
    Collection<Topic> curTopics;

    private void setContent()
    {
        final FavoriteSrtInfoVo favoriteSrtInfoVo = list.get(index);
        this.chsTv.setText(favoriteSrtInfoVo.getChs());
        this.engTv.setText(favoriteSrtInfoVo.getEng());

        if (favoriteSrtInfoVo.getDbId() > 0)
        {
            curTopics = DictionaryDao.getCETTopic(favoriteSrtInfoVo.getDbId());
            Iterator<Topic> iterator = curTopics.iterator();
            String tpContent = "";
            while (iterator.hasNext())
            {
                Topic next = iterator.next();
                tpContent += next.getTopic_word() + "  "
                        + next.getMean_cn().replace("\n", "\n    ") + "\n\n";
            }
            topictip_tv.setText(tpContent);
        }
        else
        {
            System.out.println("srtid = 0");
        }

    }

    private void initView()
    {
        chsTv = (TextView) findViewById(R.id.chs_text);
        engTv = (TextView) findViewById(R.id.eng_text);
        topictip_tv = (TextView) findViewById(R.id.topictip_tv);
        chsTv.setMovementMethod(new ScrollingMovementMethod());
        engTv.setMovementMethod(new ScrollingMovementMethod());
        topictip_tv.setMovementMethod(new ScrollingMovementMethod());

        ((Button) findViewById(R.id.btnViewContext)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnFirst)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnLast)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnSpeakCh)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnSpeakEn)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnSrtSrh)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnUpdateId)).setOnClickListener(this);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btnViewContext:
            startActivity(new Intent()
                    .setClass(this, SrtActivity.class)
                    .putExtra("srtFilePath", list.get(index).getSrtFile())
                    .putExtra("seektime",
                            list.get(index).getFromTime().toString()));
            break;
        case R.id.btnFirst:
            index = 0;
            setContent();
            break;
        case R.id.btnLast:
            index = list.size() - 1;
            setContent();
            break;
        case R.id.btnSpeakCh:
            if (bdTextToSpeech != null)
            {
                bdTextToSpeech.stop();
            }
            bdTextToSpeech = BdTextToOnlineSpeech
                    .getInstance(FavoriteSrtActivity.this);
            bdTextToSpeech.speak(chsTv.getText().toString());
            break;
        case R.id.btnSpeakEn:
            if (bdTextToSpeech != null)
            {
                bdTextToSpeech.stop();
            }
            bdTextToSpeech = BdTextToOfflineSpeech
                    .getInstance(FavoriteSrtActivity.this);
            bdTextToSpeech.speak(engTv.getText().toString());
            break;
        case R.id.btnSrtSrh:
            Intent intent = new Intent(FavoriteSrtActivity.this,
                    SrtSearchActivity.class).putExtra("dialog", engTv.getText()
                    .toString());
            startActivity(intent);
            break;
        case R.id.btnUpdateId:
            FavDao.updateSrtId();
            break;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
        if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
        {
            return super.dispatchTouchEvent(paramMotionEvent);
        }
        return true;
    }

    @Override
    public void doLeft(FlingPoint p1, FlingPoint p2)
    {
        if (index > 0)
        {
            index--;
        }
        setContent();
    }

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        if (index < list.size() - 1)
        {
            index++;
        }
        setContent();
    }
}
