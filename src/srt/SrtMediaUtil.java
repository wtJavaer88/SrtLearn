package srt;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import srt.ex.SrtException;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.dao.SrtInfoDao;
import com.wnc.string.PatternUtil;
import common.uihelper.MyAppParams;
import common.utils.MyFileUtil;
import common.utils.TextFormatUtil;

public class SrtMediaUtil
{
	public static String getCurSeason()
	{
		String srtFile = DataHolder.getFileKey();
		String season;
		if (isLocalSrt(srtFile))
		{
			season = new File(srtFile).getParentFile().getName();
		}
		else
		{
			season = PatternUtil.getFirstPatternGroup(srtFile, "(.*?)/");
		}
		return season;
	}

	private static boolean isLocalSrt(String srtFile)
	{
		return BasicFileUtil.isExistFile(srtFile);
	}

	public static String getCurEposide()
	{
		String srtFile = DataHolder.getFileKey();
		String eposide;
		if (isLocalSrt(srtFile))
		{
			eposide = TextFormatUtil.getFileNameNoExtend(srtFile);
		}
		else
		{
			eposide = PatternUtil.getFirstPatternGroup(srtFile, "/(.*+)");
		}
		return eposide;
	}

	/**
	 * 自动获取下一集
	 * 
	 * @return
	 */
	public static String getNextEp(String srtFile)
	{
		if (isLocalSrt(srtFile))
		{
			File folder = new File(BasicFileUtil.getFileParent(srtFile));
			List<File> sortedList = MyFileUtil.getSortFiles(folder.listFiles());
			System.out.println(sortedList);

			List<File> validFiles = new ArrayList<File>();
			for (File f : sortedList)
			{
				if (SrtTextHelper.isSrtfile(f))
				{
					validFiles.add(f);
				}
			}
			for (int i = 0; i < validFiles.size(); i++)
			{
				String absolutePath = validFiles.get(i).getAbsolutePath();
				if (i < validFiles.size() - 1 && absolutePath.equals(srtFile))
				{
					String nextFile = validFiles.get(i + 1).getAbsolutePath();
					return nextFile;
				}
			}
		}
		else
		{
			String nextSrt = SrtInfoDao.getNextSrt(getCurSeason(), getCurEposide());
			if (nextSrt != null)
			{
				int i = nextSrt.lastIndexOf(".");
				int j = nextSrt.lastIndexOf("/");
				if (i > -1 && i > j)
				{
					return nextSrt.substring(0, i);
				}
			}
			return nextSrt;
		}
		return null;
	}

	public static Queue<String> getSrtVoicesInRange(String srtFile, String voiceTimeStr1, String voiceTimeStr2)
	{
		Queue<String> queue = new LinkedList<String>();
		final String folder = getSrtVoiceFolder(srtFile);
		String m1 = folder + File.separator + voiceTimeStr1.replace(":", "") + ".mp3";
		String m2 = folder + File.separator + voiceTimeStr2.replace(":", "") + ".mp3";
		if (isLocalSrt(m1))
		{
			queue.offer(m1);
		}
		if (isLocalSrt(m2))
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
		if (isLocalSrt(srtFilePath))
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
			if (!isLocalSrt(filePath))
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
		if (isLocalSrt(pic1))
		{
			return pic1;
		}
		String pic2 = filePath + File.separator + current.getChs().replace(" ", "") + ".gif";
		if (isLocalSrt(pic2))
		{
			return pic2;
		}
		return "";
	}
}
