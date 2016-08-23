package common.srt.picker;

import java.util.ArrayList;
import java.util.List;

import srt.SrtInfo;
import srt.SrtTextHelper;
import srt.TimeInfo;


import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;

public class AssPicker implements Picker
{
    List<String> segments;

    public AssPicker(String srtFile)
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

    private boolean valid(String[] parts)
    {
        if (parts.length >= 9 && parts[0].startsWith("Dialogue:")
                && parts[1].matches("\\d:\\d{2}:\\d{2}\\.\\d{2}")
                && parts[2].matches("\\d:\\d{2}:\\d{2}\\.\\d{2}"))
        {
            return true;
        }
        return false;
    }

    private TimeInfo parseTimeInfo(String timeStr)
    {
        int hour = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d+:").replace(":", ""));
        int minute = BasicNumberUtil.getNumber(PatternUtil.getLastPattern(
                timeStr, "\\d+:").replace(":", ""));
        int second = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2}\\.").replace(".", ""));
        int millSecond = BasicNumberUtil.getNumber(PatternUtil.getLastPattern(
                timeStr, "\\d+"));
        if (millSecond < 100)
        {
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
    public List<SrtInfo> getSrtInfos(int start, int end)
    {
        List<SrtInfo> srtInfos = new ArrayList<SrtInfo>();
        int index = 1;
        TimeInfo fromTime = null;
        TimeInfo toTime = null;
        String chs = null;
        String eng = null;
        for (int i = start; i < end && i < segments.size(); i++)
        {
            String str = segments.get(i);
            String[] parts = str.split(",");
            if (valid(parts))
            {
                String dialogue = getDialogAfterEight(parts);

                fromTime = parseTimeInfo(parts[1]);
                toTime = parseTimeInfo(parts[2]);
                int pos = dialogue.indexOf("\\N");
                if (pos != -1)
                {
                    chs = SrtTextHelper
                            .getClearText(dialogue.substring(0, pos));
                    eng = SrtTextHelper.getClearText(dialogue
                            .substring(pos + 2));
                }
                if (fromTime != null && toTime != null && chs != null
                        && eng != null)
                {
                    // System.out.println("CHS:" + chs + " ENG:" + eng);
                    // System.out.println("FROMTIME:" + fromTime + " TOTIME:"
                    // + toTime);
                    SrtInfo srtInfo = new SrtInfo();
                    srtInfo.setSrtIndex(index);
                    srtInfo.setFromTime(fromTime);
                    srtInfo.setToTime(toTime);
                    srtInfo.setChs(chs);
                    srtInfo.setEng(eng);
                    srtInfos.add(srtInfo);
                    index++;
                    fromTime = null;
                    toTime = null;
                    chs = null;
                    eng = null;
                }
                else
                {
                    // System.out.println("Cause A Err, Not Match In File<" +
                    // srtFile + "> Line " + i + "...");
                }
            }

        }
        return srtInfos;
    }

    private String getDialogAfterEight(String[] parts)
    {
        String ret = "";
        for (int i = 9; i < parts.length; i++)
        {
            ret += parts[i].trim() + " ";
        }
        return ret;
    }
}
