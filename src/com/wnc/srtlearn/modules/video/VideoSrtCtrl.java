package com.wnc.srtlearn.modules.video;

public class VideoSrtCtrl
{
	private boolean isEngShow = true;
	private boolean isChsShow = true;

	@Override
	public String toString()
	{
		return "VideoSrtCtrl [isEngShow=" + isEngShow + ", isChsShow=" + isChsShow + "]";
	}

	public boolean isEngShow()
	{
		return isEngShow;
	}

	public void setEngShow(boolean isEngShow)
	{
		this.isEngShow = isEngShow;
	}

	public boolean isChsShow()
	{
		return isChsShow;
	}

	public void setChsShow(boolean isChsShow)
	{
		this.isChsShow = isChsShow;
	}

}