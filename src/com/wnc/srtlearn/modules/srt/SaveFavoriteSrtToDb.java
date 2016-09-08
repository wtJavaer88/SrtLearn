package com.wnc.srtlearn.modules.srt;

import java.util.ArrayList;
import java.util.List;

import srt.TimeHelper;
import android.content.Context;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.dao.FavDao;
import com.wnc.srtlearn.pojo.FavoriteMultiSrt;
import com.wnc.srtlearn.pojo.FavoriteSingleSrt;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;
import common.uihelper.MyAppParams;
import common.utils.TextFormatUtil;

public class SaveFavoriteSrtToDb
{
    public static void save(Context context)
    {
        List<String> readFrom = FileOp.readFrom(MyAppParams.FAVORITE_TXT,
                "UTF-8");
        for (String info : readFrom)
        {
            String[] childs = info.split("]");
            String tag = PatternUtil.getFirstPattern(info, "tag<.*?>")
                    .replace("tag<", "").replace(">", "");
            String ftime = PatternUtil.getFirstPattern(info,
                    "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            String srtfile = PatternUtil.getFirstPattern(info, "\".*?\"")
                    .replace("\"", "");
            FavoriteMultiSrt mfav = new FavoriteMultiSrt();
            mfav.setTag(tag);
            mfav.setFavTime(ftime);
            mfav.setFromTimeStr("");
            mfav.setToTimeStr("");
            mfav.setSrtFile(srtfile);
            List<FavoriteSingleSrt> list = new ArrayList<FavoriteSingleSrt>();
            for (String child : childs)
            {
                FavoriteSingleSrt fsInfo = getSrtInfo(child + "]");
                list.add(fsInfo);
            }
            mfav.setHasChild(list.size());
            FavDao.insertFavMulti(context, mfav, list);
        }
    }

    private static FavoriteSingleSrt getSrtInfo(String info)
    {
        FavoriteSingleSrt fsInfo = new FavoriteSingleSrt();
        final String chs = PatternUtil.getFirstPattern(info, "chs=.*?, eng")
                .replace("chs=", "").replace("eng", "").replace(", ", "");
        final String eng = PatternUtil.getFirstPattern(info, "eng=.*?]")
                .replace("eng=", "").replace("]", "");
        // 对于字幕里英文与中文颠倒的,用这种方法
        if (TextFormatUtil.containsChinese(eng)
                && !TextFormatUtil.containsChinese(chs))
        {
            fsInfo.setChs(eng);
            fsInfo.setEng(chs);
        }
        else
        {
            fsInfo.setChs(chs);
            fsInfo.setEng(eng);
        }
        fsInfo.setsIndex(BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                info, "srtIndex=\\d+").replace("srtIndex=", "")));
        fsInfo.setFromTimeStr(TimeHelper.parseTimeInfo(
                PatternUtil
                        .getFirstPattern(info,
                                "fromTime=\\d{2}:\\d{2}:\\d{2},\\d{3}, toTime")
                        .replace("fromTime=", "").replace(", toTime", ""))
                .toString());
        fsInfo.setToTimeStr(TimeHelper.parseTimeInfo(
                PatternUtil
                        .getFirstPattern(info,
                                "toTime=\\d{2}:\\d{2}:\\d{2},\\d{3}, srtIndex")
                        .replace("toTime=", "").replace(", srtIndex", ""))
                .toString());
        return fsInfo;
    }
}
