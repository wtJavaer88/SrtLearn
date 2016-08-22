package srt;

public class TimeInfo
{
    private int hour;
    private int minute;
    private int second;
    private int millSecond;

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public int getSecond()
    {
        return second;
    }

    public void setSecond(int second)
    {
        this.second = second;
    }

    public int getMillSecond()
    {
        return millSecond;
    }

    public void setMillSecond(int millSecond)
    {
        this.millSecond = millSecond;
    }

    @Override
    public String toString()
    {
        return String.format("%s:%s:%s,%s", align(hour, 2), align(minute, 2),
                align(second, 2), align(millSecond, 3));
    }

    private String align(int i, int len)
    {
        if (len == 2)
        {
            if (i < 10)
            {
                return "0" + i;
            }
        }
        else if (len == 3)
        {
            if (i < 10)
            {
                return "00" + i;
            }
            else if (i < 100)
            {
                return "0" + i;
            }
        }
        return i + "";
    }
}
