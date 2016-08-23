package srt;

public class SrtInfo
{
	protected TimeInfo fromTime;
	protected TimeInfo toTime;
	protected int srtIndex;
	protected String chs;
	protected String eng;

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
		return "SrtInfo [fromTime=" + fromTime + ", toTime=" + toTime + ", srtIndex=" + srtIndex + ", chs=" + chs + ", eng=" + eng + "]";
	}
}
