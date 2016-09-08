package com.wnc.srtlearn.pojo;

public class FavoriteMultiSrt
{
    private int id;
    private String favTime;// 收藏时间
    private String srtFile;// 字幕文件
    private int hasChild;// 子字幕条数
    private String tag;// 标签
    private String fromTimeStr;
    private String toTimeStr;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFavTimeStr()
    {
        return favTime;
    }

    public void setFavTime(String favTime)
    {
        this.favTime = favTime;
    }

    public String getSrtFile()
    {
        return srtFile;
    }

    public void setSrtFile(String srtFile)
    {
        this.srtFile = srtFile;
    }

    public int getHasChild()
    {
        return hasChild;
    }

    public void setHasChild(int hasChild)
    {
        this.hasChild = hasChild;
    }

    public String getFromTimeStr()
    {
        return fromTimeStr;
    }

    public void setFromTimeStr(String fromTime)
    {
        this.fromTimeStr = fromTime;
    }

    public String getToTimeStr()
    {
        return toTimeStr;
    }

    public void setToTimeStr(String toTime)
    {
        this.toTimeStr = toTime;
    }

    @Override
    public String toString()
    {
        return "FavoriteMultiSrt [id=" + id + ", favTime=" + favTime
                + ", srtFile=" + srtFile + ", hasChild=" + hasChild
                + ", fromTime=" + fromTimeStr + ", toTime=" + toTimeStr + "]";
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }
}
