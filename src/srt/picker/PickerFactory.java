package srt.picker;

import srt.SrtMediaUtil;
import srt.ex.SrtParseErrorException;

public class PickerFactory
{
	public static Picker getPicker(String srtFile) throws SrtParseErrorException
	{
		if (srtFile.endsWith(".srt"))
		{
			return new SrtPicker(srtFile);
		}
		else if (srtFile.endsWith(".ass") || srtFile.endsWith(".ssa"))
		{
			return new AssPicker(srtFile);
		}
		else if (srtFile.endsWith(".cnpy"))
		{
			return new SrtPicker(srtFile);
		}
		else if (srtFile.endsWith(".lrc"))
		{
			return new LrcPicker(srtFile);
		}
		else
		{
			// 从数据库中获取
			String season = SrtMediaUtil.getCurSeason();
			String eposide = SrtMediaUtil.getCurEposide();
			return new DBPicker(season, eposide);
		}
	}
}
