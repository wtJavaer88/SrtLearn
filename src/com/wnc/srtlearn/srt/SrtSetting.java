package com.wnc.srtlearn.srt;

import app.SharedPreferenceUtil;

public class SrtSetting
{

    // 是否自动播放下一条
    private final static String PLAYVOICE = "S001";
    // 是否自动播放下一条
    private final static String AUTOPLAYNEXT = "S002";

    public static boolean isPlayVoice()
    {
        return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(
                PLAYVOICE, "false"));
    }

    public static void setPlayVoice(boolean flag)
    {
        SharedPreferenceUtil.changeValue(PLAYVOICE, flag + "");
    }

    public static boolean isAutoPlayNext()
    {
        return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(
                AUTOPLAYNEXT, "false"));
    }

    public static void setAutoPlayNext(boolean flag)
    {
        SharedPreferenceUtil.changeValue(AUTOPLAYNEXT, flag + "");
    }
}
