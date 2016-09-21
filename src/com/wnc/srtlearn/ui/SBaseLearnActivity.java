package com.wnc.srtlearn.ui;

import srt.SrtInfo;
import srt.SrtPlayService;
import android.os.Handler;

public abstract class SBaseLearnActivity extends BaseHorActivity
{
    protected SrtPlayService srtPlayService;

    public abstract void stopSrtPlay();

    public abstract void play(SrtInfo srtInfo);

    public abstract Handler getHanlder();

    public abstract SrtPlayService getSrtPlayService();

    public abstract void playNext();

    public abstract void playCurrent();

    /**
     * 播放字幕文件
     * 
     * @param strFile
     */
    public abstract void enter(String strFile);
}
