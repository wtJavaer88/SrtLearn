package srt;

public class SearchSrtInfo extends SrtInfo
{
    private String srtFile;// 字幕文件: 剧名/集数

    public String getSrtFile()
    {
        return srtFile;
    }

    public void setSrtFile(String srtFile)
    {
        this.srtFile = srtFile;
    }

    @Override
    public String toString()
    {
        return "SearchSrtInfo [srtFile=" + srtFile + ", fromTime=" + fromTime
                + ", toTime=" + toTime + ", srtIndex=" + srtIndex + ", chs="
                + chs + ", eng=" + eng + "]";
    }
}
