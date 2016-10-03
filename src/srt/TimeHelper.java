package srt;

import java.util.List;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;

public class TimeHelper
{
	public static String getTimeBeforeOneMinute(String seekTime)
	{
		List<String> patternStrings = PatternUtil.getPatternStrings(seekTime, "\\d+");
		int hour = BasicNumberUtil.getNumber(patternStrings.get(0));
		int minute = BasicNumberUtil.getNumber(patternStrings.get(1));
		int seconds = BasicNumberUtil.getNumber(patternStrings.get(2));
		System.out.println(hour + "  " + minute);
		if (hour > 0)
		{
			if (minute > 0)
			{
				minute--;
			}
			else
			{
				hour--;
				minute = 59;
			}
		}
		else
		{
			if (minute > 0)
			{
				minute--;
			}
			else
			{
				seconds = 0;
			}
		}
		return BasicStringUtil.fillLeftString(hour + "", 2, "0") + ":" + BasicStringUtil.fillLeftString(minute + "", 2, "0") + ":" + BasicStringUtil.fillLeftString(seconds + "", 2, "0") + ",000";
	}

	public static String getTimeAfterOneMinute(String seekTime)
	{
		List<String> patternStrings = PatternUtil.getPatternStrings(seekTime, "\\d+");
		int hour = BasicNumberUtil.getNumber(patternStrings.get(0));
		int minute = BasicNumberUtil.getNumber(patternStrings.get(1));
		int seconds = BasicNumberUtil.getNumber(patternStrings.get(2));
		System.out.println(hour + "  " + minute);
		if (minute == 59)
		{
			hour++;
			minute = 0;
		}
		else
		{
			minute++;
		}
		return BasicStringUtil.fillLeftString(hour + "", 2, "0") + ":" + BasicStringUtil.fillLeftString(minute + "", 2, "0") + ":" + BasicStringUtil.fillLeftString(seconds + "", 2, "0") + ",000";
	}

	public static long getTime(int h, int m, int s, int mill)
	{
		return 1000L * (3600 * h + 60 * m + s) + mill;
	}

	public static long getTime(TimeInfo timeInfo)
	{
		if (timeInfo == null)
		{
			return 0l;
		}
		return 1000L * (3600 * timeInfo.getHour() + 60 * timeInfo.getMinute() + timeInfo.getSecond()) + timeInfo.getMillSecond();
	}

	public static TimeInfo parseTimeInfo(String timeStr)
	{
		List<String> patternStrings = PatternUtil.getPatternStrings(timeStr, "\\d+");
		int hour = BasicNumberUtil.getNumber(patternStrings.get(0));
		int minute = BasicNumberUtil.getNumber(patternStrings.get(1));
		int seconds = BasicNumberUtil.getNumber(patternStrings.get(2));
		int millSecond = BasicNumberUtil.getNumber(patternStrings.get(3));
		TimeInfo timeInfo = new TimeInfo();
		timeInfo.setHour(hour);
		timeInfo.setMinute(minute);
		timeInfo.setSecond(seconds);
		timeInfo.setMillSecond(millSecond);
		return timeInfo;
	}
}
