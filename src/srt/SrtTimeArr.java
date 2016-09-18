package srt;

public class SrtTimeArr
{
    private String srtFile;
    private String[] leftTimelineArr;
    private String[] rightTimelineArr;

    public String getSrtFile()
    {
        return srtFile;
    }

    public void setSrtFile(String srtFile)
    {
        this.srtFile = srtFile;
    }

    public String[] getLeftTimelineArr()
    {
        return leftTimelineArr;
    }

    public void setLeftTimelineArr(String[] leftTimelineArr)
    {
        this.leftTimelineArr = leftTimelineArr;
    }

    public String[] getRightTimelineArr()
    {
        return rightTimelineArr;
    }

    public void setRightTimelineArr(String[] rightTimelineArr)
    {
        this.rightTimelineArr = rightTimelineArr;
    }
}
