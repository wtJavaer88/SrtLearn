package srt.picker;

import java.util.ArrayList;
import java.util.List;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;

import srt.SrtInfo;
import srt.SrtTextHelper;
import srt.TimeInfo;
import srt.ex.SrtParseErrorException;

public class AssPicker implements Picker {
	List<String> segments;

	public AssPicker(String srtFile) throws SrtParseErrorException {
		this.srtFile = srtFile;
		try {
			segments = FileOp.readFrom(srtFile);
		} catch (Exception ex) {
			throw new SrtParseErrorException();
		}
	}

	String srtFile;

	@Override
	public List<SrtInfo> getSrtInfos() {
		return getSrtInfos(0, segments.size());
	}

	private boolean valid(String[] parts) {
		if (parts.length >= 9 && parts[0].startsWith("Dialogue:") && parts[1].matches("\\d:\\d{2}:\\d{2}\\.\\d{2}")
				&& parts[2].matches("\\d:\\d{2}:\\d{2}\\.\\d{2}")) {
			return true;
		}
		return false;
	}

	private TimeInfo parseTimeInfo(String timeStr) {
		int hour = BasicNumberUtil.getNumber(PatternUtil.getFirstPatternGroup(timeStr, "(\\d+):"));
		int minute = BasicNumberUtil.getNumber(PatternUtil.getLastPatternGroup(timeStr, "(\\d+):"));
		int second = BasicNumberUtil.getNumber(PatternUtil.getFirstPatternGroup(timeStr, "(\\d{2})\\."));
		int millSecond = BasicNumberUtil.getNumber(PatternUtil.getLastPatternGroup(timeStr, "\\d+"));
		if (millSecond < 100) {
			millSecond = millSecond * 10;
		}
		TimeInfo timeInfo = new TimeInfo();
		timeInfo.setHour(hour);
		timeInfo.setMinute(minute);
		timeInfo.setSecond(second);
		timeInfo.setMillSecond(millSecond);
		return timeInfo;
	}

	@Override
	public List<SrtInfo> getSrtInfos(int start, int end) {
		return dataHelper.getSrtInfosCommon(start, end);
	}

	private String getDialogAfterEight(String[] parts) {
		String ret = "";
		for (int i = 9; i < parts.length; i++) {
			ret += parts[i].trim() + " ";
		}
		return ret;
	}

	@Override
	public String getSrtFile() {
		return srtFile;
	}

	@Override
	public int getSrtLineCounts() {
		return segments.size();
	}

	@Override
	public List<SrtInfo> get10CacheSrtInfos(String fromTimeStr) {
		return dataHelper.getSrtInfosCommon(fromTimeStr, 10);
	}

	DataHelper dataHelper = new DataHelper() {

		@Override
		public List<SrtInfo> getSrtInfosCommon(int start, int end, String fromTimeStr, int count) {
			List<SrtInfo> srtInfos = new ArrayList<SrtInfo>();
			int index = 1;
			TimeInfo fromTime = null;
			TimeInfo toTime = null;
			String chs = null;
			String eng = null;
			for (int i = start; i < end && i < segments.size(); i++) {
				String str = segments.get(i);
				String[] parts = str.split(",");
				if (valid(parts)) {
					String dialogue = getDialogAfterEight(parts);

					fromTime = parseTimeInfo(parts[1]);

					if (fromTimeStr != null && fromTimeStr.compareTo(fromTime.toString()) > 0) {
						continue;
					}

					toTime = parseTimeInfo(parts[2]);
					int pos = dialogue.indexOf("\\N");
					if (pos != -1) {
						chs = SrtTextHelper.getClearText(dialogue.substring(0, pos));
						eng = SrtTextHelper.getClearText(dialogue.substring(pos + 2));
					}

					if (fromTime != null && toTime != null && chs != null && eng != null) {
						SrtInfo srtInfo = new SrtInfo();
						srtInfo.setSrtIndex(index);
						srtInfo.setFromTime(fromTime);
						srtInfo.setToTime(toTime);
						srtInfo.setChs(chs);
						srtInfo.setEng(eng);

						srtInfos.add(srtInfo);
						/**
						 * 如果达到指定的个数,则直接返回
						 */
						if (srtInfos.size() == count) {
							return srtInfos;
						}
						index++;
						fromTime = null;
						toTime = null;
						chs = null;
						eng = null;
					} else {
						// System.out.println("Cause A Err, Not Match In File<"
						// +
						// srtFile + "> Line " + i + "...");
					}
				}

			}
			return srtInfos;
		}

	};
}
