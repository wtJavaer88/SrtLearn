package com.wnc.srtlearn.ui;

import srt.SrtInfo;
import android.os.Handler;

public abstract class SBaseLearnActivity extends BaseHorActivity
{
    public abstract void stopSrtPlay();

    public abstract void play(SrtInfo srtInfo);

    public abstract Handler getHanlder();
}
