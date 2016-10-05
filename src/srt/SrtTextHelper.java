package srt;

import java.io.File;

import com.wnc.basic.BasicFileUtil;
import common.utils.TextFormatUtil;

public class SrtTextHelper
{
	public static String getClearText(String text)
	{
		return text.replaceAll("\\{.*?\\}", "").replaceAll("<.*?>", "");
	}

	/**
	 * 根据当前字幕文件获取字幕同名的文件夹
	 * 
	 * @param srtFilePath
	 * @return
	 */
	public static String getSrtVoiceFolder(String srtFilePath)
	{
		return BasicFileUtil.getFileParent(srtFilePath) + File.separator + common.utils.TextFormatUtil.getFileNameNoExtend(srtFilePath);
	}

	public static String getSrtVoiceLocation(String srtFile, String voiceTimeStr)
	{
		return getSrtVoiceFolder(srtFile) + File.separator + voiceTimeStr.replace(":", "") + ".mp3";
	}

	public static boolean isSrtfile(File f)
	{
		return f.isFile() && (f.getName().endsWith(".ass") || f.getName().endsWith(".srt") || f.getName().endsWith(".ssa") || f.getName().endsWith(".cnpy") || f.getName().endsWith(".lrc"));
	}

	public static String concatTimeline(TimeInfo fTimeinfo, TimeInfo tTimeinfo)
	{
		return fTimeinfo.toString() + " ---> " + tTimeinfo.toString();
	}

	public static String getSxFile(String srtFilePath)
	{
		if (BasicFileUtil.isExistFile(srtFilePath))
		{
			File f = new File(srtFilePath);
			String folder = f.getParent();
			int i = folder.lastIndexOf("/");
			if (i != -1)
			{
				folder = folder.substring(i + 1);
				String name = TextFormatUtil.getFileNameNoExtend(f.getName());
				return folder + " / " + name;
			}
		}
		return "";
	}

	public static String timeToText(int millistime)
	{
		if (millistime < 1000)
		{
			return "00:00";
		}

		millistime = millistime / 1000;
		int hours = millistime / 3600;
		int minutes = millistime % 3600 / 60;
		int seconds = millistime % 60;
		if (hours > 0)
		{
			return aligntime(hours) + ":" + aligntime(minutes) + ":" + aligntime(seconds);
		}
		return aligntime(minutes) + ":" + aligntime(seconds);
	}

	public static String aligntime(int t)
	{
		return (t >= 10 ? t + "" : "0" + t);
	}
}
