package com.wnc.srtlearn.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习监控, 可以考虑加个读写的锁
 * 
 * @author wnc
 *
 */
public class StudyMonitor
{
	static List<ActiveWork> activeWorks = new ArrayList<ActiveWork>();

	// 封装监控
	public static void runMonitor()
	{
		StudyMonitor.addActiveWork(new ApplicationActiveWork());
		new MonitorThread().start();
	}

	public static long getRunTime()
	{
		for (ActiveWork work : activeWorks)
		{
			if (work.getType() == WORKTYPE.APPLICATION)
			{
				return System.currentTimeMillis() - work.getEntertime();
			}
		}
		return 0;
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
	 * 获取某类监控的数目
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
