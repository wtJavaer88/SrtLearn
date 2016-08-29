package net.widget.act.sample;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.MyActAdapter;
import net.widget.act.token.SemicolonTokenizer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;

import com.wnc.srtlearn.R;

public class ActTestActivity extends Activity implements
        UncaughtExceptionHandler
{

    private List<AutoCompletable> items = new ArrayList<AutoCompletable>();
    private MultiAutoCompleteTextView act;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acttest);
        initData();
        initView();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    private void initView()
    {
        act = (MultiAutoCompleteTextView) this.findViewById(R.id.myact);
        MyActAdapter adapter = new BookAutoAdapter(this, items, 2);
        act.setAdapter(adapter);
        act.setThreshold(1);
        act.setTokenizer(new SemicolonTokenizer(",. "));
    }

    private void initData()
    {
        Book b1 = new Book(1, "三国演义", "罗贯中", 38, "sanguoyanyi");
        Book b2 = new Book(2, "红楼梦", "曹雪芹", 25, "hongloumeng");
        Book b21 = new Book(2, "红楼梦1", "曹雪芹1", 25, "hongloumeng");
        Book b22 = new Book(2, "红楼梦2", "曹雪芹2", 25, "hongloumeng");
        Book b3 = new Book(3, "西游记", "吴承恩", 43, "xiyouji");
        Book b4 = new Book(4, "水浒传", "施耐庵", 72, "shuihuzhuan");
        Book b5 = new Book(5, "随园诗话", "袁枚", 32, "suiyuanshihua");
        Book b6 = new Book(6, "说文解字", "许慎", 14, "shuowenjiezi");
        Book b7 = new Book(7, "文心雕龙", "刘勰", 18, "wenxindiaolong");
        items.add(b1);
        items.add(b2);
        items.add(b21);
        items.add(b22);
        items.add(b3);
        items.add(b4);
        items.add(b5);
        items.add(b6);
        items.add(b7);
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
