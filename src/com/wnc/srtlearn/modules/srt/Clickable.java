package com.wnc.srtlearn.modules.srt;

import android.app.Activity;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Clickable extends ClickableSpan implements OnClickListener
{
	private String filePath;
	private Activity activity;

	public Clickable(Activity activity, String uri)
	{
		this.activity = activity;
		this.filePath = uri;
	}

	@Override
	public void onClick(View v)
	{
		if (activity != null)
		{
			try
			{
				Log.i("linkfile click", filePath);

			}
			catch (Exception ex)
			{
			}
		}
		else
		{
			Log.e("context", "没有设置上下文Context!");
		}
	}
}
