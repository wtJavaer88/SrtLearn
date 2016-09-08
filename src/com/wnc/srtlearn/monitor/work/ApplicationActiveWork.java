package com.wnc.srtlearn.monitor.work;

public class ApplicationActiveWork extends ActiveWork
{
	public ApplicationActiveWork()
	{
		setEntertime(System.currentTimeMillis());
		setType(WORKTYPE.APPLICATION);
	}
}
