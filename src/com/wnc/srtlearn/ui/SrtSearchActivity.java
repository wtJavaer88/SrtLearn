package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.MyActAdapter;
import net.widget.act.token.SemicolonTokenizer;
import srt.SearchSrtInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.wnc.srtlearn.R;
import com.wnc.srtlearn.dao.SrtDao;
import com.wnc.srtlearn.modules.search.ActSrtWord;
import com.wnc.srtlearn.modules.search.SrtWordAutoAdapter;
import common.utils.TextFormatUtil;

public class SrtSearchActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{

    String dialog;
    private List<AutoCompletable> items = new ArrayList<AutoCompletable>();
    private MultiAutoCompleteTextView act;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题

        Thread.setDefaultUncaughtExceptionHandler(this);
        setContentView(R.layout.activity_srtsearch);
        initData();
        initView();
    }

    private void initData()
    {
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("dialog") != null)
        {
            dialog = intent.getStringExtra("dialog").trim();
        }
        else
        {
            dialog = "Today is a big day.".trim();
        }
        for (String word : dialog.split(" "))
        {
            word = TextFormatUtil.getTextNoSymbol(word);
            ActSrtWord srtWord = new ActSrtWord();
            srtWord.setWord(word);
            items.add(srtWord);
        }
    }

    private void initView()
    {
        ((Button) findViewById(R.id.btn_srtsearch)).setOnClickListener(this);
        act = (MultiAutoCompleteTextView) this.findViewById(R.id.actSrtSearch);
        MyActAdapter adapter = new SrtWordAutoAdapter(this, items, 2);
        act.setAdapter(adapter);
        act.setThreshold(1);
        act.setTokenizer(new SemicolonTokenizer(" "));
        act.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                ListView lv = (ListView) parent;
                ActSrtWord word = (ActSrtWord) lv.getItemAtPosition(position);
                System.out.println("clickWord:" + word);
                // act.append(word.getWord() + " ");
            }

        });
    }

    int curArrIndex = -1;

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_srtsearch:
            SrtDao.openDatabase(this);
            final String keyword = this.act.getText().toString();
            List<SearchSrtInfo> searchResult = SrtDao.search(keyword);
            System.out.println(searchResult);
            break;
        }
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
