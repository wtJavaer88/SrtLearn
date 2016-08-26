package com.wnc.srtlearn.ui;

import java.util.ArrayList;
import java.util.List;

import srt.FavoriteSrtInfo;
import srt.TimeInfo;
import android.os.Environment;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;

public class TestReadSrt
{
    public static List<FavoriteSrtInfo> getFSInfos()
    {
        List<String> readFrom = FileOp.readFrom(Environment
                .getExternalStorageDirectory().getPath()
                + "/wnc/app/srtlearn/favorite.txt", "UTF-8");
        List<FavoriteSrtInfo> list = new ArrayList<FavoriteSrtInfo>();
        for (String info : readFrom)
        {
            list.add(getSrtInfo(info));
        }
        return list;
    }

    private static FavoriteSrtInfo getSrtInfo(String info)
    {
        FavoriteSrtInfo fsInfo = new FavoriteSrtInfo();
        fsInfo.setFavoriteTime(PatternUtil.getFirstPattern(info,
                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        fsInfo.setChs(PatternUtil.getFirstPattern(info, "chs=.*?, eng")
                .replace("chs=", "").replace("eng", "").replace(", ", ""));
        fsInfo.setEng(PatternUtil.getFirstPattern(info, "eng=.*?]")
                .replace("eng=", "").replace("]", ""));
        fsInfo.setSrtFile(PatternUtil.getFirstPattern(info, "\".*?\"").replace(
                "\"", ""));
        fsInfo.setSrtIndex(BasicNumberUtil.getNumber(PatternUtil
                .getFirstPattern(info, "srtIndex=\\d+")
                .replace("srtIndex=", "")));
        fsInfo.setFromTime(parseTimeInfo(PatternUtil
                .getFirstPattern(info,
                        "fromTime=\\d{2}:\\d{2}:\\d{2},\\d{3}, toTime")
                .replace("fromTime=", "").replace(", toTime", "")));
        fsInfo.setToTime(parseTimeInfo(PatternUtil
                .getFirstPattern(info,
                        "toTime=\\d{2}:\\d{2}:\\d{2},\\d{3}, srtIndex")
                .replace("toTime=", "").replace(", srtIndex", "")));
        return fsInfo;
    }

    private static TimeInfo parseTimeInfo(String timeStr)
    {
        int hour = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int minute = BasicNumberUtil.getNumber(PatternUtil.getLastPattern(
                timeStr, "\\d{2}:").replace(":", ""));
        int second = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{2},").replace(",", ""));
        int millSecond = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                timeStr, "\\d{3}"));
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setHour(hour);
        timeInfo.setMinute(minute);
        timeInfo.setSecond(second);
        timeInfo.setMillSecond(millSecond);
        return timeInfo;
    }

}
