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

    public String getSrtFile();

    public int getSrtLineCounts();
}
