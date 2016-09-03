package com.wnc.srtlearn.monitor;

public class ActiveWork
{
	private long entertime;
	private long exitTime;
	private WORKTYPE type;

	public long getEntertime()
	{
		return entertime;
	}

	public void setEntertime(long entertime)
	{
		this.entertime = entertime;
	}

	public long getExitTime()
	{
		return exitTime;
	}

	public void setExitTime(long exitTime)
	{
		this.exitTime = exitTime;
	}

	@Override
	public String toString()
	{
		return "ActiveWork [entertime=" + entertime + ", exittime=" + exitTime + ", type=" + type + "]";
	}

	public WORKTYPE getType()
	{
		return type;
	}

	public void setType(WORKTYPE type)
	{
		this.type = type;
	}

}
