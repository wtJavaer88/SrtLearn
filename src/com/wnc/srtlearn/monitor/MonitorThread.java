package com.wnc.srtlearn.monitor;

import com.wnc.srtlearn.monitor.work.WORKTYPE;

public class MonitorThread extends Thread
{
	private volatile boolean running = true;

	@Override
	public void run()
	{
		while (running)
		{
			try
			{
				Thread.sleep(30000);
				System.out.println("当前学习的字幕数:" + StudyMonitor.getWorkCount(WORKTYPE.SRT));
				System.out.println("当前学习的朗读数:" + StudyMonitor.getWorkCount(WORKTYPE.TTS_REC));
			}
			catch (InterruptedException e)
			{
				// e.printStackTrace();
			}
		}
	}

	public void stopRun()
	{
		this.interrupt();
		running = false;
	}
}
