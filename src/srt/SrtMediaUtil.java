package srt;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import com.wnc.basic.BasicFileUtil;
import common.uihelper.MyAppParams;

public class SrtMediaUtil
{
	public static Queue<String> getSrtVoicesInRange(String srtFile, String voiceTimeStr1, String voiceTimeStr2)
	{
		Queue<String> queue = new LinkedList<String>();
		final String folder = SrtTextHelper.getSrtVoiceFolder(srtFile);
		String m1 = folder + File.separator + voiceTimeStr1.replace(":", "") + ".mp3";
		String m2 = folder + File.separator + voiceTimeStr2.replace(":", "") + ".mp3";
		if (BasicFileUtil.isExistFile(m1))
		{
			queue.offer(m1);
		}
		if (BasicFileUtil.isExistFile(m2))
		{
			queue.offer(m2);
		}

		return queue;
	}

	public static String getVoicePath(String season, String epodise, String fromTime)
	{
		return MyAppParams.SRT_FOLDER + season + File.separator + epodise + File.separator + fromTime + ".mp3";
	}

	public static String getVideoFile(String baseFolder, String series, String episodeKey)
	{
		String ret = null;
		String makeFilePath = BasicFileUtil.getMakeFilePath(baseFolder, series);
		if (BasicFileUtil.isExistFolder(makeFilePath))
		{
			for (File f : new File(makeFilePath).listFiles())
			{
				if (f.getName().contains(episodeKey))
				{
					return f.getAbsolutePath();
				}
			}
		}
		else
		{
			System.out.println("not this folder.." + makeFilePath);
		}
		return ret;
	}
}
