package com.wnc.srtlearn.monitor;

import java.text.SimpleDateFormat;

import com.wnc.basic.BasicDateUtil;
import com.wnc.srtlearn.dao.WorkDao;
import com.wnc.srtlearn.monitor.work.WORKTYPE;

public class WorkMgr
{

	public static void insertWork(WORKTYPE type, int runId)
	{
		StudyMonitor.clearWork(type);
		WorkDao.insertWorkMgr(runId, type, StudyMonitor.getWorkCount(type), StudyMonitor.getWorkTime(type));
	}

	public static String getDateTimeStr(long time)
	{
		return BasicDateUtil.getDateTimeFromLongTime(time);
	}

	public static int insertRunRecord(long time1, long time2)
	{
		StudyMonitor.clearWork(WORKTYPE.APPLICATION);
		return WorkDao.insertRunRecord(getDateTimeStr(time1), getDateTimeStr(time2), getDuration(time2 - time1));
	}

	private static String getDuration(long l)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");// 初始化Formatter的转换格式。
		return formatter.format(l);
	}

	public static int getTypeId(WORKTYPE work_type)
	{
		int type = 0;
		switch (work_type)
		{
		case APPLICATION:
			type = 1;
			break;
		case SRT:
			type = 2;
			break;
		case TTS_REC:
			type = 3;
			break;
		case SRT_SEARCH:
			type = 4;
			break;
		case PINYIN:
			type = 5;
			break;

		}
		return type;
	}

}
