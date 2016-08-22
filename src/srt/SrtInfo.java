package srt;

public class SrtInfo
{
    private TimeInfo fromTime;
    private TimeInfo toTime;
    private int srtIndex;
    private String chs;
    private String eng;

    public TimeInfo getFromTime()
    {
        return fromTime;
    }

    public void setFromTime(TimeInfo fromTime)
    {
        this.fromTime = fromTime;
    }

    public TimeInfo getToTime()
    {
        return toTime;
    }

    public void setToTime(TimeInfo toTime)
    {
        this.toTime = toTime;
    }

    public int getSrtIndex()
    {
        return srtIndex;
    }

    public void setSrtIndex(int srtIndex)
    {
        this.srtIndex = srtIndex;
    }

    public String getChs()
    {
        return chs;
    }

    public void setChs(String chs)
    {
        this.chs = chs;
    }

    public String getEng()
    {
        return eng;
    }

    public void setEng(String eng)
    {
        this.eng = eng;
    }

    @Override
    public String toString()
    {
        return "SrtInfo [fromTime=" + fromTime + ", toTime=" + toTime
                + ", srtIndex=" + srtIndex + ", chs=" + chs + ", eng=" + eng
                + "]";
    }
}
