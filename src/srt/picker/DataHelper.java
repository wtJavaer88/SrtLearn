package srt.picker;

import java.util.List;

import srt.SrtInfo;

public abstract class DataHelper
{
    public List<SrtInfo> getSrtInfosCommon(String fromTimeStr, String toTimeStr)
    {
        return getSrtInfosCommon(0, Integer.MAX_VALUE, fromTimeStr, toTimeStr,
                Integer.MAX_VALUE);
    }

    public List<SrtInfo> getSrtInfosCommon(String fromTimeStr, int count)
    {
        return getSrtInfosCommon(0, Integer.MAX_VALUE, fromTimeStr, null, count);
    }

    public List<SrtInfo> getSrtInfosCommon(int start, int end)
    {
        return getSrtInfosCommon(start, end, null, null, Integer.MAX_VALUE);
    }

    public abstract List<SrtInfo> getSrtInfosCommon(int start, int end,
            String fromTimeStr, String toTimeString, int count);

}
