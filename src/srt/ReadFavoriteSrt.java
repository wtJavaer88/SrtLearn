package srt;

import java.util.ArrayList;
import java.util.List;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;
import common.uihelper.MyAppParams;

public class ReadFavoriteSrt
{
    public static List<FavoriteSrtInfo> getFSInfos()
    {
        List<String> readFrom = FileOp.readFrom(MyAppParams.FAVORITE_TXT,
                "UTF-8");
        List<FavoriteSrtInfo> list = new ArrayList<FavoriteSrtInfo>();
        for (String info : readFrom)
        {
            list.addAll(getSrtInfos(info));
        }
        return list;
    }

    private static FavoriteSrtInfo getSrtInfo(String info)
    {
        FavoriteSrtInfo fsInfo = new FavoriteSrtInfo();
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
    private static List<FavoriteSrtInfo> getSrtInfos(String info)
    {
        List<FavoriteSrtInfo> list = new ArrayList<FavoriteSrtInfo>();
        String[] childs = info.split("]");
        String tag = PatternUtil.getFirstPattern(info, "tag<.*?>")
                .replace("tag<", "").replace(">", "");
        String ftime = PatternUtil.getFirstPattern(info,
                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        String srtfile = PatternUtil.getFirstPattern(info, "\".*?\"").replace(
                "\"", "");
        for (String child : childs)
        {
            FavoriteSrtInfo fsInfo = getSrtInfo(child + "]");
            fsInfo.setTag(tag);
            fsInfo.setFavoriteTime(ftime);
            fsInfo.setSrtFile(srtfile);
            list.add(fsInfo);
        }
        for (FavoriteSrtInfo fsInfo : list)
        {
            fsInfo.setSublings(list.size());
            System.out.println(fsInfo);
        }
        return list;
    }

}
