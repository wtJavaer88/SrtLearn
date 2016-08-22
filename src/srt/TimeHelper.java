package srt;

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
}
