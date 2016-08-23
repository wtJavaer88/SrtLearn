package common.srt.picker;

import java.util.ArrayList;
import java.util.List;

import srt.SrtInfo;
import srt.SrtTextHelper;
import srt.TimeInfo;


import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;

public class SrtPicker implements Picker
{
    List<String> segments;

    public SrtPicker(String srtFile)
    {
        this.srtFile = srtFile;
        segments = FileOp.readFrom(srtFile, "GBK");
    }

    String srtFile;

    @Override
    public List<SrtInfo> getSrtInfos()
    {
        return getSrtInfos(0, segments.size());
    }

    private TimeInfo parseTimeInfo(String timeStr)
    {
        int hour = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int minute = BasicNumberUtil.getNumber(PatternUtil.getLastPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int second = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2},").replace(",", ""));
        int millSecond = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{3}"));
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setHour(hour);
        timeInfo.setMinute(minute);
        timeInfo.setSecond(second);
        timeInfo.setMillSecond(millSecond);
        return timeInfo;
    }

    private boolean isIndexLine(String string)
    {
        return string.matches("\\d+");
    }

    private boolean isEmptyLine(String string)
    {
        return string.trim().length() == 0;
    }

    @Override
    public List<SrtInfo> getSrtInfos(int start, int end)
    {
        List<SrtInfo> srtInfos = new ArrayList<SrtInfo>();

        int index = 0;
        TimeInfo fromTime = null;
        TimeInfo toTime = null;
        String chs = null;
        String eng = null;
        int indexLineNumber = 0;
        if (end < segments.size() && !isEmptyLine(segments.get(end - 1)))
        {
            end += 4;
        }
        for (int i = start; i < end && i < segments.size(); i++)
        {
            {
                String str = segments.get(i);
                if (isIndexLine(str))
                {
                    indexLineNumber = i;
                    index = BasicNumberUtil.getNumber(str.trim());
                }
                if (i == indexLineNumber + 1)
                {
                    fromTime = parseTimeInfo(PatternUtil.getFirstPattern(str,
                            "\\d{2}:\\d{2}:\\d{2},\\d{3}"));
                    toTime = parseTimeInfo(PatternUtil.getLastPattern(str,
                            "\\d{2}:\\d{2}:\\d{2},\\d{3}"));
                }
                if (i == indexLineNumber + 2)
                {
                    chs = SrtTextHelper.getClearText(str);
                }
                if (i == indexLineNumber + 3)
                {
                    eng = SrtTextHelper.getClearText(str);
                }
                if (i == indexLineNumber + 4)
                {
                    if (index > 0 && fromTime != null && toTime != null
                            && chs != null && eng != null)
                    {
                        // System.out.println("一段字幕" + index + "已经结束...");
                        // System.out.println("CHS:" + chs + " ENG:" + eng);
                        // System.out.println("FROMTIME:" + fromTime + "
                        // TOTIME:" +
                        // toTime);
                        SrtInfo srtInfo = new SrtInfo();
                        srtInfo.setSrtIndex(index);
                        srtInfo.setFromTime(fromTime);
                        srtInfo.setToTime(toTime);
                        srtInfo.setChs(chs);
                        srtInfo.setEng(eng);
                        srtInfos.add(srtInfo);
                        index = 0;
                        fromTime = null;
                        toTime = null;
                        chs = null;
                        eng = null;
                    }
                    else
                    {
                        System.out.println("Cause A Err, Not Match In File<"
                                + srtFile + "> Line " + i + "...");
                    }
                }
            }
        }
        return srtInfos;
    }
}
