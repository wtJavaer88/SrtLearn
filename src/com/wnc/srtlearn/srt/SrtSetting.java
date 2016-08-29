package com.wnc.srtlearn.srt;

import common.app.SharedPreferenceUtil;

/**
 * 关于字幕的全局设置,存入手机本地数据库
 * 
 * @author cpr216
 * 
 */
public class SrtSetting
{

    // 是否自动播放下一条
    private final static String PLAYVOICE = "S001";
    // 是否自动播放下一条
    private final static String AUTOPLAYNEXT = "S002";
    // 是否启动音量键翻页的监控
    private final static String VOLKEYLISTEN = "S003";

    public static boolean isVolKeyListen()
    {
        return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(
                VOLKEYLISTEN, "false"));
    }

    public static void setVolKeyListen(boolean flag)
    {
        SharedPreferenceUtil.changeValue(VOLKEYLISTEN, flag + "");
    }

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
