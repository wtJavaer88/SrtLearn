package com.wnc.srtlearn.ui;

import srt.SrtInfo;
import srt.SrtPlayService;
import android.os.Handler;

public abstract class SBaseLearnActivity extends BaseHorActivity
{
	public static final int MESSAGE_TOPIC_IN_SRT = 100;
	public static final int MESSAGE_GET_CACHED_SRT = 101;
	public static final int MESSAGE_GET_ALL_SRT = 102;
	public static final int MESSAGE_GET_ERROR_SRT = 103;

	protected SrtPlayService srtPlayService;

	public abstract void stopSrtPlay();

	public abstract void play(SrtInfo srtInfo);

	public abstract Handler getHanlder();

	public abstract Handler getBackGroundHanlder();

	public abstract SrtPlayService getSrtPlayService();

	public abstract void playNext();

	public abstract void playCurrent();

	/**
	 * 播放字幕文件
	 * 
	 * @param strFile
	 */
	public abstract void enter(String strFile);
}
