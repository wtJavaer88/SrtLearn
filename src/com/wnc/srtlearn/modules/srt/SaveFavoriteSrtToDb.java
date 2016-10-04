package com.wnc.srtlearn.modules.srt;

import java.util.ArrayList;
import java.util.List;

import srt.TimeHelper;
import android.content.Context;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.dao.FavDao;
import com.wnc.srtlearn.pojo.FavoriteMultiSrt;
import com.wnc.srtlearn.pojo.FavoriteSingleSrt;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;
import common.uihelper.MyAppParams;
import common.utils.TextFormatUtil;

public class SaveFavoriteSrtToDb
{
	public static void save(Context context)
	{
		try
		{
			List<String> readFrom = FileOp.readFrom(MyAppParams.FAVORITE_TXT, "UTF-8");
			for (String info : readFrom)
			{
				String[] childs = info.split("]");
				String tag = PatternUtil.getFirstPatternGroup(info, "tag<(.*?)>");
				String ftime = PatternUtil.getFirstPatternGroup(info, "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
				String srtfile = PatternUtil.getFirstPatternGroup(info, "\"(.*?)\"");
				FavoriteMultiSrt mfav = new FavoriteMultiSrt();
				mfav.setTag(tag);
				mfav.setFavTime(ftime);
				mfav.setSrtFile(srtfile);
				List<FavoriteSingleSrt> list = new ArrayList<FavoriteSingleSrt>();
				for (String child : childs)
				{
					FavoriteSingleSrt fsInfo = getSrtInfo(child + "]");
					list.add(fsInfo);
				}
				if (list.size() > 0)
				{
					mfav.setFromTimeStr(list.get(0).getFromTimeStr().toString());
					mfav.setToTimeStr(list.get(list.size() - 1).getToTimeStr().toString());
				}
				mfav.setHasChild(list.size());
				FavDao.insertFavMulti(context, mfav, list);
			}
		}
		catch (Exception e)
		{
			System.out.println("err:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private static FavoriteSingleSrt getSrtInfo(String info)
	{
		FavoriteSingleSrt fsInfo = new FavoriteSingleSrt();
		final String chs = PatternUtil.getFirstPatternGroup(info, "chs=(.*?), eng");
		final String eng = PatternUtil.getFirstPatternGroup(info, "eng=(.*?)]");
		// 对于字幕里英文与中文颠倒的,用这种方法
		if (TextFormatUtil.containsChinese(eng) && !TextFormatUtil.containsChinese(chs))
		{
			fsInfo.setChs(eng);
			fsInfo.setEng(chs);
		}
		else
		{
			fsInfo.setChs(chs);
			fsInfo.setEng(eng);
		}
		fsInfo.setsIndex(BasicNumberUtil.getNumber(PatternUtil.getFirstPatternGroup(info, "srtIndex=(\\d+)")));
		fsInfo.setFromTimeStr(TimeHelper.parseTimeInfo(PatternUtil.getFirstPatternGroup(info, "fromTime=(\\d{2}:\\d{2}:\\d{2},\\d{3}), toTime")).toString());
		fsInfo.setToTimeStr(TimeHelper.parseTimeInfo(PatternUtil.getFirstPatternGroup(info, "toTime=(\\d{2}:\\d{2}:\\d{2},\\d{3}), srtIndex")).toString());
		return fsInfo;
	}
}
