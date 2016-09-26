package srt.picker;

import java.util.List;

import srt.SrtInfo;

public interface Picker
{
    public List<SrtInfo> getSrtInfos();

    /**
     * 用于分页的方法
     * 
     * @param start
     * @param end
     * @return
     */
    public List<SrtInfo> getSrtInfos(int start, int end);

    /**
     * 获取从指定位置(00:00:00,000)开始的10条缓存记录,可以默认为null
     * 
     * @param fromTimeStr
     * @return
     */
    public List<SrtInfo> get10CacheSrtInfos(String fromTimeStr);

    public List<SrtInfo> getCacheSrtInfosInRange(String startTime,
            String endTime);

    public String getSrtFile();

    public int getSrtLineCounts();
}
