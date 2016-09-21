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
        int hour = BasicNumberUtil.getNumber(PatternUtil.getFirstPatternGroup(
                timeStr, "(\\d{2}):"));
        int minute = BasicNumberUtil.getNumber(PatternUtil.getLastPatternGroup(
                timeStr, "(\\d{2}):"));
        int second = BasicNumberUtil.getNumber(PatternUtil
                .getFirstPatternGroup(timeStr, "(\\d{2}),"));
        int millSecond = BasicNumberUtil.getNumber(PatternUtil
                .getFirstPatternGroup(timeStr, "\\d{3}"));
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setHour(hour);
        timeInfo.setMinute(minute);
        timeInfo.setSecond(second);
        timeInfo.setMillSecond(millSecond);
        return timeInfo;
    }
}
