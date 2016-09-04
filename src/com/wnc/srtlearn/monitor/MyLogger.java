package com.wnc.srtlearn.monitor;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import common.uihelper.MyAppParams;

public class MyLogger
{
	private final static String logFileFormat = "yyyy-MM-dd";
	private final static String logFolder = MyAppParams.getInstance().getLocalLogPath();

	public static void log(String msg)
	{
		BasicFileUtil.writeFileString(getLogFilePath(), msg + "\r\n", null, true);
	}

	private static String getLogFilePath()
	{
		return BasicFileUtil.getMakeFilePath(logFolder, BasicDateUtil.getCurrentDateFormatString(logFileFormat) + ".txt");
	}
}
