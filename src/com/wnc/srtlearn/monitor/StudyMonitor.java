package com.wnc.srtlearn.monitor;

import java.util.ArrayList;
import java.util.List;

import com.wnc.srtlearn.monitor.work.ActiveWork;
import com.wnc.srtlearn.monitor.work.ApplicationActiveWork;
import com.wnc.srtlearn.monitor.work.WORKTYPE;

/**
 * 学习监控, 可以考虑加个读写的锁
 * 
 * @author wnc
 *
 */
public class StudyMonitor
{
	private static ApplicationActiveWork appwork;
	static List<ActiveWork> activeWorks = new ArrayList<ActiveWork>();

	// 封装监控
	public static void runMonitor()
	{
		appwork = new ApplicationActiveWork();
		new MonitorThread().start();
	}

	/**
	 * App活动记录
	 * 
	 * @return
	 */
	public static ActiveWork getApplicationActiveWork()
	{
		return appwork;
	}

	public static long getRunTime()
	{
		if (appwork == null)
		{
			return 0;
		}
		appwork.setExitTime(System.currentTimeMillis());
		return appwork.getExitTime() - appwork.getEntertime();
	}

	public static synchronized void clearWork(WORKTYPE type)
	{
		for (int i = 0; i < activeWorks.size(); i++)
		{
			ActiveWork work = activeWorks.get(i);
			if (work.getType() == type)
			{
				activeWorks.remove(i);
			}
		}
	}

	public static synchronized void clearAllWorks()
	{
		for (int i = 0; i < activeWorks.size(); i++)
		{
			activeWorks.clear();
		}
	}

	// 传入监控类型,返回一个监控对象给客户端
	public static synchronized ActiveWork peekWork(WORKTYPE type)
	{
		ActiveWork activeWork = new ActiveWork();
		activeWork.setType(type);
		activeWork.setEntertime(System.currentTimeMillis());
		return activeWork;
	}

	/**
	 * 监控结束,真正添加该监控
	 * 
	 * @param work
	 */
	public synchronized static void addActiveWork(ActiveWork work)
	{
		if (work.getExitTime() == 0)
		{
			work.setExitTime(System.currentTimeMillis());
		}
		activeWorks.add(work);
	}

	/**
	 * 获得某类监控的总运行时间
	 * 
	 * @param type
	 * @return
	 */
	public static long getWorkTime(WORKTYPE type)
	{
		long total = 0;
		for (ActiveWork work : activeWorks)
		{
			if (work.getType() == type)
			{
				total += work.getExitTime() - work.getEntertime();
			}
		}
		return total;
	}

	/**
	 * 获取某类业务监控的数目
	 * 
	 * @param type
	 * @return
	 */
	public static int getWorkCount(WORKTYPE type)
	{
		int total = 0;
		for (ActiveWork work : activeWorks)
		{
			if (work.getType() == type)
			{
				total += 1;
			}
		}
		return total;
	}
}
