package com.wnc.srtlearn.modules.srt;

import java.util.ArrayList;
import java.util.List;

import srt.TimeHelper;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.vo.FavoriteSrtInfoVo;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;
import common.uihelper.MyAppParams;

public class ReadFavoriteSrt
{
    public static List<FavoriteSrtInfoVo> getFSInfos()
    {
        List<String> readFrom = FileOp.readFrom(MyAppParams.FAVORITE_TXT,
                "UTF-8");
        List<FavoriteSrtInfoVo> list = new ArrayList<FavoriteSrtInfoVo>();
        for (String info : readFrom)
        {
            list.addAll(getSrtInfos(info));
        }
        return list;
    }

    private static FavoriteSrtInfoVo getSrtInfo(String info)
    {
        FavoriteSrtInfoVo fsInfo = new FavoriteSrtInfoVo();
        fsInfo.setChs(PatternUtil.getFirstPattern(info, "chs=.*?, eng")
                .replace("chs=", "").replace("eng", "").replace(", ", ""));
        fsInfo.setEng(PatternUtil.getFirstPattern(info, "eng=.*?]")
                .replace("eng=", "").replace("]", ""));
        fsInfo.setSrtIndex(BasicNumberUtil.getNumber(PatternUtil
                .getFirstPattern(info, "srtIndex=\\d+")
                .replace("srtIndex=", "")));
        fsInfo.setFromTime(TimeHelper.parseTimeInfo(PatternUtil
                .getFirstPattern(info,
                        "fromTime=\\d{2}:\\d{2}:\\d{2},\\d{3}, toTime")
                .replace("fromTime=", "").replace(", toTime", "")));
        fsInfo.setToTime(TimeHelper.parseTimeInfo(PatternUtil
                .getFirstPattern(info,
                        "toTime=\\d{2}:\\d{2}:\\d{2},\\d{3}, srtIndex")
                .replace("toTime=", "").replace(", srtIndex", "")));
        return fsInfo;
    }

    /**
     * 对每一行的内容进行解析
     * 
     * @param info
     * @return
     */
    private static List<FavoriteSrtInfoVo> getSrtInfos(String info)
    {
        List<FavoriteSrtInfoVo> list = new ArrayList<FavoriteSrtInfoVo>();
        String[] childs = info.split("]");
        String tag = PatternUtil.getFirstPattern(info, "tag<.*?>")
                .replace("tag<", "").replace(">", "");
        String ftime = PatternUtil.getFirstPattern(info,
                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        String srtfile = PatternUtil.getFirstPattern(info, "\".*?\"").replace(
                "\"", "");
        for (String child : childs)
        {
            FavoriteSrtInfoVo fsInfo = getSrtInfo(child + "]");
            fsInfo.setTag(tag);
            fsInfo.setFavoriteTime(ftime);
            fsInfo.setSrtFile(srtfile);
            list.add(fsInfo);
        }
        for (FavoriteSrtInfoVo fsInfo : list)
        {
            fsInfo.setSublings(list.size());
            System.out.println(fsInfo);
        }
        return list;
    }

}
