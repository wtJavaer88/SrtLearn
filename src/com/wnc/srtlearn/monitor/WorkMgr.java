package com.wnc.srtlearn.monitor;

import java.text.SimpleDateFormat;

import com.wnc.basic.BasicDateUtil;
import com.wnc.srtlearn.dao.SrtDao;

public class WorkMgr
{

	public static void insertWork(WORKTYPE type, int runId)
	{
		SrtDao.insertWorkMgr(runId, type, StudyMonitor.getWorkCount(type), StudyMonitor.getWorkTime(type));
	}

	public static String getDateTimeStr(long time)
	{
		return BasicDateUtil.getDateTimeFromLongTime(time);
	}

	public static int insertRunRecord(long time1, long time2)
	{
		return SrtDao.insertRunRecord(getDateTimeStr(time1), getDateTimeStr(time2), getDuration(time2 - time1));
	}

	private static String getDuration(long l)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");// 初始化Formatter的转换格式。
		return formatter.format(l);
	}
}
