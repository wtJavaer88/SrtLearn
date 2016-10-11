package com.wnc.srtlearn.vo;

import srt.SrtInfo;

/**
 * 该类综合了数据库里的两个收藏表的结果
 * 
 * @author cpr216
 * 
 */
public class FavoriteSrtInfoVo extends SrtInfo
{
    private String favoriteTime;// 收藏的时间
    private String srtFile;// 字幕文件: 剧名/集数
    private String tag;// 字幕标签,暂时为replay和normal,今天更新以前的默认为空字符串
    private int sublings;// 跟自己一同被收藏的字幕数
    private int id;

    public String getFavoriteTime()
    {
        return favoriteTime;
    }

    public void setFavoriteTime(String favoriteTime)
    {
        this.favoriteTime = favoriteTime;
    }

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
        return "FavoriteSrtInfo [id=" + id + ",favoriteTime=" + favoriteTime
                + ", srtFile=" + srtFile + ", tag=" + tag + ", sublings="
                + sublings + ", fromTime=" + fromTime + ", toTime=" + toTime
                + ", srtIndex=" + srtIndex + ", chs=" + chs + ", eng=" + eng
                + "]";
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public int getSublings()
    {
        return sublings;
    }

    public void setSublings(int sublings)
    {
        this.sublings = sublings;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
