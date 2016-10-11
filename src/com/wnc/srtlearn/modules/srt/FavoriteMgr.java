package com.wnc.srtlearn.modules.srt;

import java.util.ArrayList;
import java.util.List;

import srt.SrtInfo;
import android.content.Context;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.srtlearn.dao.FavDao;
import com.wnc.srtlearn.pojo.FavoriteMultiSrt;
import com.wnc.srtlearn.pojo.FavoriteSingleSrt;
import common.uihelper.MyAppParams;

public class FavoriteMgr
{
    Favoritable favoritable;

    public FavoriteMgr(Favoritable favoritable, Context context)
    {
        this.favoritable = favoritable;
    }

    public boolean save(List<SrtInfo> currentPlaySrtInfos)
    {
        FavoriteMultiSrt mfav = new FavoriteMultiSrt();
        mfav.setFavTime(BasicDateUtil.getCurrentDateTimeString());
        mfav.setFromTimeStr(currentPlaySrtInfos.get(0).getFromTime().toString());
        mfav.setToTimeStr(currentPlaySrtInfos
                .get(currentPlaySrtInfos.size() - 1).getToTime().toString());
        mfav.setSrtFile(favoritable.getCurFile().replace(
                MyAppParams.SRT_FOLDER, ""));
        mfav.setHasChild(currentPlaySrtInfos.size());
        mfav.setTag(favoritable.getFavTag().replace("tag<", "")
                .replace(">", ""));

        List<FavoriteSingleSrt> sfavs = new ArrayList<FavoriteSingleSrt>();
        for (SrtInfo srtInfo : currentPlaySrtInfos)
        {
            FavoriteSingleSrt sfav = new FavoriteSingleSrt();
            sfav.setFromTimeStr(srtInfo.getFromTime().toString());
            sfav.setToTimeStr(srtInfo.getToTime().toString());
            sfav.setsIndex(srtInfo.getSrtIndex());
            sfav.setEng(srtInfo.getEng());
            sfav.setChs(srtInfo.getChs());
            sfavs.add(sfav);
        }
        if (FavDao.isExistMulti(mfav))
        {
            return true;
        }

        boolean insertFavMulti = FavDao.insertFavMulti(mfav, sfavs);
        if (insertFavMulti)
        {
            writeFavoritetxt(currentPlaySrtInfos);
        }
        return insertFavMulti;
    }

    private boolean writeFavoritetxt(List<SrtInfo> currentPlaySrtInfos)
    {
        String favoriteCurrContent = getFavoriteCurrContent(currentPlaySrtInfos);
        return BasicFileUtil.writeFileString(MyAppParams.FAVORITE_TXT,
                favoriteCurrContent, "UTF-8", true);
    }

    public String getFavoriteCurrContent(List<SrtInfo> currentPlaySrtInfos)
    {
        String tag = favoritable.getFavTag();
        return BasicDateUtil.getCurrentDateTimeString() + " \""
                + favoritable.getCurFile().replace(MyAppParams.SRT_FOLDER, "")
                + "\" " + tag + " " + currentPlaySrtInfos + "\r\n";
    }
}
