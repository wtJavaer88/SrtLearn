package srt.picker;

import java.util.List;

import srt.SrtInfo;

public abstract class DataHelper {
	public List<SrtInfo> getSrtInfosCommon(String fromTimeStr, int count) {
		return getSrtInfosCommon(0, Integer.MAX_VALUE, fromTimeStr, count);
	}

	public List<SrtInfo> getSrtInfosCommon(int start, int end) {
		return getSrtInfosCommon(start, end, null, Integer.MAX_VALUE);
	}

	public abstract List<SrtInfo> getSrtInfosCommon(int start, int end, String fromTimeStr, int count);

}
