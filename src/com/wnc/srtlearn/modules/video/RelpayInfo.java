package com.wnc.srtlearn.modules.video;

public class RelpayInfo
{
    int preLoad;
    int afterLoad;
    int srtcounts;

    public int getPreLoad()
    {
        return preLoad;
    }

    public void setPreLoad(int preLoad)
    {
        this.preLoad = preLoad;
    }

    public int getAfterLoad()
    {
        return afterLoad;
    }

    public void setAfterLoad(int afterLoad)
    {
        this.afterLoad = afterLoad;
    }

    public int getSrtcounts()
    {
        return srtcounts;
    }

    public void setSrtcounts(int srtcounts)
    {
        this.srtcounts = srtcounts;
    }

    @Override
    public String toString()
    {
        return "RelpayInfo [preLoad=" + preLoad + ", afterLoad=" + afterLoad
                + ", srtcounts=" + srtcounts + "]";
    }
}