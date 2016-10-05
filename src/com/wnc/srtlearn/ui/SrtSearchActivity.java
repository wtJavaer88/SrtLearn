package com.wnc.srtlearn.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.MyActAdapter;
import net.widget.act.token.SemicolonTokenizer;
import srt.SearchSrtInfo;
import srt.SrtMediaUtil;
import srt.SrtTextHelper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import com.wnc.srtlearn.dao.SrtInfoDao;
import com.wnc.srtlearn.dao.WorkDao;
import com.wnc.srtlearn.modules.search.ActSrtWord;
import com.wnc.srtlearn.modules.search.SrtWordAutoAdapter;
import com.wnc.srtlearn.modules.srt.SrtVoiceHelper;
import com.wnc.srtlearn.monitor.StudyMonitor;
import com.wnc.srtlearn.monitor.work.ActiveWork;
import com.wnc.srtlearn.monitor.work.WORKTYPE;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ToastUtil;
import common.utils.TextFormatUtil;

public class SrtSearchActivity extends BaseVerActivity implements OnClickListener, UncaughtExceptionHandler
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
		for (String word : PatternUtil.getPatternStrings(dialog.toLowerCase(), "\\w+"))
		{
			items.add(new ActSrtWord(word));
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
		act.setTokenizer(new SemicolonTokenizer(" ", ""));
		act.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				ListView lv = (ListView) parent;
				ActSrtWord word = (ActSrtWord) lv.getItemAtPosition(position);
				// act.append(word.getWord() + " ");
			}
		});
		showKeyBoard();
	}

	private void showKeyBoard()
	{
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				InputMethodManager inputManager = (InputMethodManager) act.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(act, 0);
			}
		}, 998);
	}

	int curArrIndex = -1;

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_srtsearch:
			SrtInfoDao.openDatabase();
			final String keyword = this.act.getText().toString();
			ActiveWork activeWork = StudyMonitor.peekWork(WORKTYPE.SRT_SEARCH);
			if (BasicStringUtil.isNullString(keyword) || keyword.trim().length() == 0)
			{
				ToastUtil.showShortToast(getApplicationContext(), "请输入内容!");
			}
			else
			{
				List<SearchSrtInfo> searchResult = SrtInfoDao.searchByLan(true, keyword);

				int size = searchResult.size();
				WorkDao.log(this, WORKTYPE.SRT_SEARCH, keyword + ":" + size);
				StudyMonitor.addActiveWork(activeWork);

				if (size > 0)
					setLv(searchResult);
			}
			break;
		}
	}

	private void setLv(List<SearchSrtInfo> searchResult)
	{
		SimpleAdapter adapter = new SimpleAdapter(this, getData(searchResult), R.layout.lv_item_srtsearch, new String[] { "eng", "chs" }, new int[] { R.id.tvEng, R.id.evChs });
		searchLv.setAdapter(adapter);
		searchLv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				HashMap map = (HashMap) ((ListView) arg0).getItemAtPosition(arg2);
				showDialog(map);
				// show(map);
			}
		});
	}

	public void showDialog(HashMap map)
	{
		final SearchSrtInfo ssrt = (SearchSrtInfo) map.get("obj");
		int index = BasicNumberUtil.getNumber(String.valueOf(map.get("index")));

		Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
		dialog.setContentView(R.layout.common_wdailog);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();

		WindowManager.LayoutParams lp = window.getAttributes();
		int width = BasicPhoneUtil.getScreenWidth(this);
		lp.width = (int) (0.8 * width);

		final String epStr = TextFormatUtil.removeFileExtend(ssrt.getSrtFile());
		final TextView tvEp = (TextView) dialog.findViewById(R.id.tvEpidoseInfo);
		String season = PatternUtil.getFirstPatternGroup(epStr, "(.*?)/");
		String eposide = PatternUtil.getFirstPatternGroup(epStr, "/(.*+)");
		tvEp.setText(season + "\n" + eposide);

		((ImageButton) dialog.findViewById(R.id.imgbtn_ToContext)).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ToastUtil.showShortToast(getApplicationContext(), "上下文");
				startActivity(new Intent().setClass(getApplicationContext(), SrtActivity.class).putExtra("srtFilePath", ssrt.getSrtFile()).putExtra("seektime", ssrt.getFromTime().toString()));
			}
		});

		final String voicePath = SrtMediaUtil.getVoicePath(season, eposide, ssrt.getFromTime().toString().replace(":", ""));
		ImageButton imgbtVoice = (ImageButton) dialog.findViewById(R.id.imgbtn_PlayVoice);
		if (!BasicFileUtil.isExistFile(voicePath))
		{
			imgbtVoice.setVisibility(View.INVISIBLE);
		}
		imgbtVoice.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SrtVoiceHelper.play(voicePath);
			}
		});
		// String timelineStr = SrtTextHelper.concatTimeline(ssrt.getFromTime(),
		// ssrt.getToTime());
		((TextView) dialog.findViewById(R.id.tvTimeLine)).setText(ssrt.getFromTime().toString());
		dialog.show();
	}

	private void show(HashMap map)
	{
		SearchSrtInfo ssrt = (SearchSrtInfo) map.get("obj");
		int index = BasicNumberUtil.getNumber(String.valueOf(map.get("index")));
		final String items[] = { "(" + index + ") " + ssrt.getSrtFile(), SrtTextHelper.concatTimeline(ssrt.getFromTime(), ssrt.getToTime()) };
		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		TextView tv = new TextView(this);
		tv.setText("(" + index + ") \n" + TextFormatUtil.getFileNameNoExtend(ssrt.getSrtFile()).replace("/", "\n") + "\n" + SrtTextHelper.concatTimeline(ssrt.getFromTime(), ssrt.getToTime())); // 内容
		tv.setTextSize(20);// 字体大小
		tv.setPadding(30, 20, 10, 10);// 位置

		tv.setTextColor(Color.parseColor("#fa800a"));// 颜色
		builder.setCustomTitle(tv);// 不是setTitle()
		builder.create().show();
	}

	private List<Map<String, Object>> getData(List<SearchSrtInfo> searchResult)
	{
		ToastUtil.showLongToast(this, "搜索到" + searchResult.size() + "个字幕");
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
