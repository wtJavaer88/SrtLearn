package srt.picker;

import java.util.ArrayList;
import java.util.List;

import srt.SrtInfo;

import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.dao.SrtInfoDao;

public class DBPicker implements Picker
{

	String season;
	String episode;
	List<SrtInfo> srtInfos = new ArrayList<SrtInfo>();

	public DBPicker(String season, String episode)
	{
		this.season = season;
		this.episode = episode;
		srtInfos = SrtInfoDao.getSrtInfos(season, episode);
		System.out.println(srtInfos.size());
	}

	@Override
	public List<SrtInfo> getSrtInfos()
	{
		return srtInfos;
	}

	public List<SrtInfo> getSrtInfos(int start, int end)
	{
		List<SrtInfo> ret = new ArrayList<SrtInfo>();
		for (int i = start; i < end && i < srtInfos.size(); i++)
		{
			ret.add(srtInfos.get(i));
		}
		return ret;
	}

	@Override
	public List<SrtInfo> get10CacheSrtInfos(String fromTimeStr)
	{
		List<SrtInfo> ret = new ArrayList<SrtInfo>();

		int i = 0;
		for (SrtInfo info : srtInfos)
		{
			if (BasicStringUtil.isNullString(fromTimeStr) || info.getFromTime().toString().compareTo(fromTimeStr) >= 0)
			{
				i++;
				ret.add(info);
				if (i == 10)
				{
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public List<SrtInfo> getCacheSrtInfosInRange(String startTime, String endTime)
	{
		List<SrtInfo> ret = new ArrayList<SrtInfo>();

		for (SrtInfo info : srtInfos)
		{
			if (info.getFromTime().toString().compareTo(startTime) >= 0 && info.getToTime().toString().compareTo(endTime) <= 0)
			{
				ret.add(info);
			}
		}
		return ret;
	}

	@Override
	public String getSrtFile()
	{
		return season + "/" + episode;
	}

	@Override
	public int getSrtLineCounts()
	{
		return srtInfos.size();
	}

}
