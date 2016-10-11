package com.wnc.srtlearn.pojo;

public class FavoriteSingleSrt
{
    private int id;
    private int pId;// 父Id
    private int srt_id;// 字幕ID
    private int sIndex;// 在字幕中的序号
    private String fromTimeStr;
    private String toTimeStr;
    private String eng;
    private String chs;

    @Override
    public String toString()
    {
        return "FavoriteSingleSrt [id=" + id + ", pId=" + pId + ", sIndex="
                + sIndex + ", fromTimeStr=" + fromTimeStr + ", toTimeStr="
                + toTimeStr + ", eng=" + eng + ", chs=" + chs + "]";
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getpId()
    {
        return pId;
    }

    public void setpId(int pId)
    {
        this.pId = pId;
    }

    public int getsIndex()
    {
        return sIndex;
    }

    public void setsIndex(int sIndex)
    {
        this.sIndex = sIndex;
    }

    public String getEng()
    {
        return eng;
    }

    public void setEng(String eng)
    {
        this.eng = eng;
    }

    public String getChs()
    {
        return chs;
    }

    public void setChs(String chs)
    {
        this.chs = chs;
    }

    public void setFromTimeStr(String fromTimeStr)
    {
        this.fromTimeStr = fromTimeStr;
    }

    public void setToTimeStr(String toTimeStr)
    {
        this.toTimeStr = toTimeStr;
    }

    public String getFromTimeStr()
    {
        return fromTimeStr;
    }

    public String getToTimeStr()
    {
        return toTimeStr;
    }

    public int getSrt_id()
    {
        return srt_id;
    }

    public void setSrt_id(int srt_id)
    {
        this.srt_id = srt_id;
    }
}
