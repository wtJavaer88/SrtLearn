package com.wnc.srtlearn.setting;

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
    // 是否播放背景声音
    private final static String PLAYBGVOICE = "S004";
    // 是否自动播放下一集
    private final static String AUTONEXTEP = "S005";

    public static boolean isVolKeyListen()
    {
        return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(
                VOLKEYLISTEN, "false"));
    }

    public static void setVolKeyListen(boolean flag)
    {
        SharedPreferenceUtil.changeValue(VOLKEYLISTEN, flag);
    }

    public static boolean isPlayVoice()
    {
        return SharedPreferenceUtil.getShareDataByKey(PLAYVOICE, false);
    }

    public static void setPlayVoice(boolean flag)
    {
        SharedPreferenceUtil.changeValue(PLAYVOICE, flag);
    }

    public static boolean isAutoPlayNext()
    {
        return SharedPreferenceUtil.getShareDataByKey(AUTOPLAYNEXT, false);
    }

    public static void setAutoPlayNext(boolean flag)
    {
        SharedPreferenceUtil.changeValue(AUTOPLAYNEXT, flag);
    }

    public static void setPlayBgVoice(boolean flag)
    {
        SharedPreferenceUtil.changeValue(PLAYBGVOICE, flag);
    }

    public static boolean isPlayBgVoice()
    {
        return SharedPreferenceUtil.getShareDataByKey(PLAYBGVOICE, true);
    }

    /**
     * 是否自动下一集
     * 
     * @return
     */
    public static boolean isAutoNextEP()
    {
        return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(
                AUTONEXTEP, "true"));
    }

    public static void setAutoNextEP(boolean flag)
    {
        SharedPreferenceUtil.changeValue(AUTONEXTEP, flag);
    }
}
