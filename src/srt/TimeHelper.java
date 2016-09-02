package srt;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;

public class TimeHelper
{
    public static long getTime(int h, int m, int s, int mill)
    {
        return 1000L * (3600 * h + 60 * m + s) + mill;
    }

    public static long getTime(TimeInfo timeInfo)
    {
        return 1000L
                * (3600 * timeInfo.getHour() + 60 * timeInfo.getMinute() + timeInfo
                        .getSecond()) + timeInfo.getMillSecond();
    }

    public static TimeInfo parseTimeInfo(String timeStr)
    {
        int hour = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int minute = BasicNumberUtil.getNumber(PatternUtil.getLastPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int second = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2},").replace(",", ""));
        int millSecond = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{3}"));
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setHour(hour);
        timeInfo.setMinute(minute);
        timeInfo.setSecond(second);
        timeInfo.setMillSecond(millSecond);
        return timeInfo;
    }
}
