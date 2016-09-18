package srt.picker;

import java.util.ArrayList;
import java.util.List;

import srt.SrtInfo;
import srt.TimeInfo;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;

public class LrcPicker implements Picker
{
    List<String> segments;

    public static void main(String[] args)
    {
        List<SrtInfo> srtInfos = PickerFactory.getPicker("10.ai ei ui.lrc")
                .getSrtInfos();
        for (SrtInfo srtInfo : srtInfos)
        {
            System.out.println(srtInfo);
        }
    }

    public LrcPicker(String srtFile)
    {
        this.srtFile = srtFile;
        segments = FileOp.readFrom(srtFile, "UTF-8");
    }

    String srtFile;

    @Override
    public List<SrtInfo> getSrtInfos()
    {
        return getSrtInfos(0, segments.size());
    }

    private TimeInfo parseTimeInfo(String timeStr)
    {
        int hour = 0;
        int minute = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int second = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2}\\.").replace(".", ""));
        int millSecond = 10 * BasicNumberUtil.getNumber(PatternUtil
                .getLastPattern(timeStr, "\\d{2}"));
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setHour(hour);
        timeInfo.setMinute(minute);
        timeInfo.setSecond(second);
        timeInfo.setMillSecond(millSecond);
        return timeInfo;
    }

    @Override
    public List<SrtInfo> getSrtInfos(int start, int end)
    {
        List<SrtInfo> srtInfos = new ArrayList<SrtInfo>();

        int index = 0;
        TimeInfo fromTime = null;
        String chs = null;
        String eng = null;

        for (int i = start; i < end && i < segments.size(); i++)
        {
            String str = segments.get(i);
            if (!str.matches("^\\[\\d{2}:\\d{2}\\.\\d{2}\\].*+"))
            {
                continue;
            }

            fromTime = parseTimeInfo(PatternUtil.getFirstPattern(str,
                    "\\d{2}:\\d{2}\\.\\d{2}"));
            if (index > 0 && srtInfos.get(index - 1).getToTime() == null)
            {
                srtInfos.get(index - 1).setToTime(fromTime);
            }
            chs = PatternUtil.getFirstPattern(str, "\\].*?\\\\N")
                    .replace("]", "").replace("\\N", "");
            eng = PatternUtil.getFirstPattern(str, "\\\\N.*+").replace("\\N",
                    "");
            if (fromTime != null && !BasicStringUtil.isNullAllString(chs, eng))
            {
                index++;
                SrtInfo srtInfo = new SrtInfo();
                srtInfo.setSrtIndex(index);
                srtInfo.setFromTime(fromTime);
                srtInfo.setChs(chs);
                srtInfo.setEng(eng);
                srtInfos.add(srtInfo);
                fromTime = null;
                chs = null;
                eng = null;
            }
            else
            {
                System.out.println("Cause A Err, Not Match In File<" + srtFile
                        + "> Line " + i + "...");
            }
        }
        return srtInfos;
    }

    @Override
    public String getSrtFile()
    {
        return srtFile;
    }

    @Override
    public int getSrtLineCounts()
    {
        return segments.size();
    }
}
