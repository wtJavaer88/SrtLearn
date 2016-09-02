package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.MyActAdapter;
import net.widget.act.token.SemicolonTokenizer;
import srt.SearchSrtInfo;
import srt.SrtTextHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.dao.SrtDao;
import com.wnc.srtlearn.modules.search.ActSrtWord;
import com.wnc.srtlearn.modules.search.SrtWordAutoAdapter;
import common.uihelper.MyAppParams;
import common.utils.TextFormatUtil;

public class SrtSearchActivity extends Activity implements OnClickListener, UncaughtExceptionHandler
{

	String dialog;
	private List<AutoCompletable> items = new ArrayList<AutoCompletable>();
	private MultiAutoCompleteTextView act;
	private ListView searchLv;

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
		if (intent != null && BasicStringUtil.isNotNullString(intent.getStringExtra("dialog")))
		{
			dialog = intent.getStringExtra("dialog").trim();
		}
		else
		{
			dialog = "Today is a big day.".trim();
		}
		for (String word : dialog.split("[ .,!。，！]"))
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
		((TextView) findViewById(R.id.tvSearchDialog)).setText(dialog);
		searchLv = (ListView) findViewById(R.id.lvSrtSearch);

		act = (MultiAutoCompleteTextView) this.findViewById(R.id.actSrtSearch);
		MyActAdapter adapter = new SrtWordAutoAdapter(this, items, 2);
		act.setAdapter(adapter);
		act.setThreshold(1);
		act.setTokenizer(new SemicolonTokenizer(" "));
		act.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
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
			setLv(searchResult);
			break;
		}
	}

	private void setLv(List<SearchSrtInfo> searchResult)
	{
		SimpleAdapter adapter = new SimpleAdapter(this, getData(searchResult), R.layout.lv_srtsearch, new String[] { "eng", "chs" }, new int[] { R.id.tvEng, R.id.evChs });
		searchLv.setAdapter(adapter);
		searchLv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				HashMap map = (HashMap) ((ListView) arg0).getItemAtPosition(arg2);
				showDialog(map);
			}
		});
	}

	public void showDialog(HashMap map)
	{
		SearchSrtInfo ssrt = (SearchSrtInfo) map.get("obj");
		int index = BasicNumberUtil.getNumber(String.valueOf(map.get("index")));
		Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
		dialog.setContentView(R.layout.common_wdailog);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();

		WindowManager.LayoutParams lp = window.getAttributes();
		int width = MyAppParams.getScreenWidth();
		lp.width = (int) (0.6 * width);

		((TextView) dialog.findViewById(R.id.tvTimeLine)).setText(SrtTextHelper.concatTimeline(ssrt.getFromTime(), ssrt.getToTime()));
		((TextView) dialog.findViewById(R.id.tvEpidoseInfo)).setText("(" + index + ") " + ssrt.getSrtFile());
		dialog.show();
	}

	private List<Map<String, Object>> getData(List<SearchSrtInfo> searchResult)
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		int i = 0;
		for (SearchSrtInfo ssrt : searchResult)
		{
			map = new HashMap<String, Object>();
			// map.put("timeline",
			// SrtTextHelper.concatTimeline(ssrt.getFromTime(),
			// ssrt.getToTime()));
			map.put("eng", ssrt.getEng());
			map.put("chs", ssrt.getChs());
			map.put("index", ++i);
			map.put("obj", ssrt);
			list.add(map);
		}
		return list;
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
