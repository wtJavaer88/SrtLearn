package com.wnc.srtlearn.modules.video;

import android.os.Message;

import com.wnc.srtlearn.ui.VideoActivity;

public class MenuDispossThread extends Thread
{
	int DISPOSS_TIME = 5000;
	int process = 0;
	private static final int SLEEP_TIME = 1000;
	VideoActivity activity;
	private volatile boolean isShowing = false;

	public MenuDispossThread(VideoActivity activity)
	{
		this.activity = activity;
	}

	/**
	 * 开启新的监听
	 */
	public void refresh()
	{
		this.process = 0;
		isShowing = true;
	}

	/**
	 * 停止监听
	 */
	public void stopListen()
	{
		isShowing = false;
		this.process = 0;
	}

	@Override
	public void run()
	{

		while (true)
		{
			if (process >= DISPOSS_TIME)
			{
				if (isShowing)
				{
					Message msg = new Message();
					msg.what = VideoActivity.ON_MENU_DISPOSS_CODE;
					activity.getHandler().sendMessage(msg);
					stopListen();
				}
			}
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			if (isShowing)
			{
				process += SLEEP_TIME;
			}
		}

	}
}