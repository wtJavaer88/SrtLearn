package com.wnc.srtlearn.modules.srt;

import srt.SrtInfo;
import srt.TimeInfo;

/**
 * 用于视频播放中
 * 
 * @author wnc
 *
 */
public class EmptySrtInfo extends SrtInfo
{
	public EmptySrtInfo()
	{
		this.eng = "";
		this.chs = "";
		fromTime = new TimeInfo();
		toTime = new TimeInfo();
	}
}
