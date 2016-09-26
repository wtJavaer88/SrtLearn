package srt.picker;

import java.util.ArrayList;
import java.util.List;

import srt.SrtInfo;
import srt.TimeInfo;
import srt.ex.SrtParseErrorException;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;

public class LrcPicker implements Picker
{
    List<String> segments;

    public LrcPicker(String srtFile) throws SrtParseErrorException
    {
        this.srtFile = srtFile;
        try
        {
            segments = FileOp.readFrom(srtFile);
        }
        catch (Exception ex)
        {
            throw new SrtParseErrorException();
        }
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
        int minute = BasicNumberUtil.getNumber(PatternUtil
                .getFirstPatternGroup(timeStr, "(\\d{2}):"));
        int second = BasicNumberUtil.getNumber(PatternUtil
                .getFirstPatternGroup(timeStr, "(\\d{2})\\."));
        int millSecond = 10 * BasicNumberUtil.getNumber(PatternUtil
                .getLastPatternGroup(timeStr, "\\d{2}"));
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
        return dataHelper.getSrtInfosCommon(start, end);
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

    @Override
    public List<SrtInfo> get10CacheSrtInfos(String fromTimeStr)
    {
        return dataHelper.getSrtInfosCommon(fromTimeStr, 10);
    }

    DataHelper dataHelper = new DataHelper()
    {

        @Override
        public List<SrtInfo> getSrtInfosCommon(int start, int end,
                String fromTimeStr, String toTimeStr, int count)
        {
            List<SrtInfo> srtInfos = new ArrayList<SrtInfo>();

            int index = 0;
            TimeInfo fromTime = null;
            String chs = null;
            String eng = null;

            for (int i = start; i < end && i < segments.size(); i++)
            {
                String str = segments.get(i);
                if(!str.matches("^\\[\\d{2}:\\d{2}\\.\\d{2}\\].*+"))
                {
                    continue;
                }

                fromTime = parseTimeInfo(PatternUtil.getFirstPatternGroup(str,
                        "\\d{2}:\\d{2}\\.\\d{2}"));
                System.out.println(fromTime.toString());
                if(fromTimeStr != null
                        && fromTimeStr.compareTo(fromTime.toString()) > 0)
                {
                    continue;
                }
                if(index > 0 && srtInfos.get(index - 1).getToTime() == null)
                {
                    srtInfos.get(index - 1).setToTime(fromTime);
                }
                chs = PatternUtil.getFirstPatternGroup(str, "\\](.*?)\\\\N");
                eng = PatternUtil.getFirstPatternGroup(str, "\\\\N(.*+)");
                if(fromTime != null
                        && !BasicStringUtil.isNullAllString(chs, eng))
                {
                    index++;
                    SrtInfo srtInfo = new SrtInfo();
                    srtInfo.setSrtIndex(index);
                    srtInfo.setFromTime(fromTime);
                    srtInfo.setChs(chs);
                    srtInfo.setEng(eng);
                    srtInfos.add(srtInfo);
                    if(srtInfos.size() == count)
                    {
                        return srtInfos;
                    }
                    if(toTimeStr != null
                            && fromTime.toString().compareTo(toTimeStr) >= 0)
                    {
                        return srtInfos;
                    }
                    fromTime = null;
                    chs = null;
                    eng = null;
                }
                else
                {
                    System.out.println("Cause A Err, Not Match In File<"
                            + srtFile + "> Line " + i + "...");
                }
            }
            return srtInfos;
        }
    };

    @Override
    public List<SrtInfo> getCacheSrtInfosInRange(String startTime,
            String endTime)
    {
        return dataHelper.getSrtInfosCommon(startTime, endTime);
    }

}
