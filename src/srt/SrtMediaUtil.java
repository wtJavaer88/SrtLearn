package srt;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import srt.ex.SrtException;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;
import common.uihelper.MyAppParams;
import common.utils.TextFormatUtil;

public class SrtMediaUtil
{
	public static String getCurSeason()
	{
		String srtFile = DataHolder.getFileKey();
		String season;
		if (BasicFileUtil.isExistFile(srtFile))
		{
			season = new File(srtFile).getParentFile().getName();
		}
		else
		{
			season = PatternUtil.getFirstPatternGroup(srtFile, "(.*?)/");
		}
		return season;
	}

	public static String getCurEposide()
	{
		String srtFile = DataHolder.getFileKey();
		String eposide;
		if (BasicFileUtil.isExistFile(srtFile))
		{
			eposide = TextFormatUtil.getFileNameNoExtend(srtFile);
		}
		else
		{
			eposide = PatternUtil.getFirstPatternGroup(srtFile, "/(.*+)");
		}
		return eposide;
	}

	public static Queue<String> getSrtVoicesInRange(String srtFile, String voiceTimeStr1, String voiceTimeStr2)
	{
		Queue<String> queue = new LinkedList<String>();
		final String folder = getSrtVoiceFolder(srtFile);
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

	/**
	 * 根据当前字幕文件获取字幕同名的文件夹
	 * 
	 * @param srtFilePath
	 * @return
	 */
	public static String getSrtVoiceFolder(String srtFilePath)
	{
		if (BasicFileUtil.isExistFile(srtFilePath))
		{
			return BasicFileUtil.getFileParent(srtFilePath) + File.separator + common.utils.TextFormatUtil.getFileNameNoExtend(srtFilePath);
		}
		String season = PatternUtil.getFirstPatternGroup(srtFilePath, "(.*?)/");
		String eposide = PatternUtil.getFirstPatternGroup(srtFilePath, "/(.*+)");
		return MyAppParams.SRT_FOLDER + season + File.separator + eposide;
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

	/**
	 * 获取图片文件路径
	 * 
	 * @param srtFile
	 * @return
	 * @throws SrtException
	 */
	public static String getThumbPicPath(String srtFile) throws SrtException
	{
		String filePath = MyAppParams.THUMB_PICFOLDER + getCurSeason() + File.separator + getCurEposide();

		File picFolder = new File(filePath);
		if (picFolder.exists())
		{
			if (srtFile.endsWith(".lrc"))
			{
				// 如果有单词图片,则打开单词图片,否则打开剧集图片
				final String wordPic = getWordPic(filePath);
				if (BasicStringUtil.isNotNullString(wordPic))
				{
					return wordPic;
				}
			}
			filePath = filePath + File.separator + getCurEposide() + "_p1.pic";
			if (!BasicFileUtil.isExistFile(filePath))
			{
				if (picFolder.listFiles().length > 0)
				{
					filePath = picFolder.listFiles()[0].getAbsolutePath();
				}
			}
		}
		else
		{
			filePath = "";
		}
		return filePath;
	}

	private static String getWordPic(String filePath) throws SrtException
	{
		SrtInfo current = DataHolder.getCurrent();
		String pic1 = filePath + File.separator + current.getChs().replace(" ", "") + ".pic";
		if (BasicFileUtil.isExistFile(pic1))
		{
			return pic1;
		}
		String pic2 = filePath + File.separator + current.getChs().replace(" ", "") + ".gif";
		if (BasicFileUtil.isExistFile(pic2))
		{
			return pic2;
		}
		return "";
	}
}
