package com.wnc.srtlearn.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import srt.SearchSrtInfo;
import srt.SrtInfo;
import srt.TimeHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.pojo.FavoriteMultiSrt;
import com.wnc.srtlearn.pojo.FavoriteSingleSrt;
import com.wnc.srtlearn.setting.Backup;
import com.wnc.srtlearn.vo.FavoriteSrtInfoVo;
import common.uihelper.MyAppParams;
import common.utils.TextFormatUtil;

public class FavDao
{
    private static SQLiteDatabase db = null;

    public static void openDatabase()
    {
        try
        {
            String databaseFilename = MyAppParams.FAVORITE_DB;
            db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isExistSingle(SrtInfo mfav, String srtFile)
    {
        openDatabase();
        try
        {
            Cursor c = db
                    .rawQuery(
                            "SELECT * FROM FAV_MULTI M LEFT JOIN FAV_SINGLE S ON M.ID=S.PID  WHERE SRTFILE=? AND S.FROM_TIME=? AND S.TO_TIME=?",
                            new String[]
                            { srtFile, mfav.getFromTime().toString(),
                                    mfav.getToTime().toString() });// 注意大小写
            if (c.moveToNext())
            {
                return true;
            }
            c.close();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        finally
        {
            closeDb();
        }
        return false;
    }

    public static boolean isExistMulti(FavoriteMultiSrt mfav)
    {
        openDatabase();
        try
        {
            Cursor c = db
                    .rawQuery(
                            "SELECT * FROM FAV_MULTI WHERE SRTFILE=? AND FROM_TIME=? AND TO_TIME=?",
                            new String[]
                            { mfav.getSrtFile(), mfav.getFromTimeStr(),
                                    mfav.getToTimeStr() });// 注意大小写
            if (c.moveToNext())
            {
                return true;
            }
            c.close();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        finally
        {
            closeDb();
        }
        return false;
    }

    public static boolean insertFavMulti(FavoriteMultiSrt mfav,
            List<FavoriteSingleSrt> sfavs)
    {
        boolean ret = false;
        try
        {
            openDatabase();
            db.beginTransaction();// 开启事务
            db.execSQL(
                    "INSERT INTO FAV_MULTI(FAV_TIME,SRTFILE,FROM_TIME ,TO_TIME,HAS_CHILD,TAG) VALUES (?,?,?,?,?,?)",
                    new Object[]
                    { mfav.getFavTimeStr(), mfav.getSrtFile(),
                            mfav.getFromTimeStr(), mfav.getToTimeStr(),
                            mfav.getHasChild(), mfav.getTag() });
            Cursor c = db.rawQuery("SELECT MAX(ID) MAXID FROM FAV_MULTI", null);// 注意大小写
            int mfav_Id = 0;
            if (c.moveToNext())
            {
                mfav_Id = BasicNumberUtil.getNumber(getStrValue(c, "MAXID"));
            }
            c.close();
            if (mfav_Id > 0)
            {
                ret = insertFavChilds(mfav_Id, sfavs);
                if (ret)
                {
                    Backup.canBackup = true;
                    db.setTransactionSuccessful();// 设置成功标记
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            ret = false;
        }
        finally
        {
            db.endTransaction();// 成功的时候会提交, 不然会回滚
            closeDb();
        }
        return ret;
    }

    public static List<FavoriteSrtInfoVo> search(boolean isEng, String keyWord)
    {
        keyWord = StringEscapeUtils.escapeSql(keyWord);
        List<FavoriteSrtInfoVo> list = new ArrayList<FavoriteSrtInfoVo>();
        try
        {
            final String engOrchs = isEng ? "eng" : "chs";
            String sql = "select m.srtfile,s.* from fav_multi m,fav_single s where m.id=s.pid and "
                    + engOrchs + " like '%" + keyWord + "%' order by s.id asc";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                FavoriteSrtInfoVo srtInfoVo = new FavoriteSrtInfoVo();
                srtInfoVo.setSrtFile(c.getString(c.getColumnIndex("srtfile")));

                srtInfoVo.setChs(c.getString(c.getColumnIndex("chs")));
                srtInfoVo.setEng(c.getString(c.getColumnIndex("eng")));
                srtInfoVo.setFromTime(TimeHelper.parseTimeInfo(c.getString(c
                        .getColumnIndex("from_time"))));
                srtInfoVo.setToTime(TimeHelper.parseTimeInfo(c.getString(c
                        .getColumnIndex("to_time"))));
                srtInfoVo.setDbId(BasicNumberUtil.getNumber(c.getString(c
                        .getColumnIndex("srt_id"))));
                srtInfoVo.setId(BasicNumberUtil.getNumber(c.getString(c
                        .getColumnIndex("id"))));
                list.add(srtInfoVo);
                c.moveToNext();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateSrtId()
    {
        try
        {
            openDatabase();
            SrtInfoDao.openDatabase();
            List<FavoriteSrtInfoVo> list = search(true, "");
            System.out.println(list.size() + " 搜索到");
            for (FavoriteSrtInfoVo favSrtInfo : list)
            {
                if (favSrtInfo.getDbId() > 0)
                {
                    continue;
                }
                boolean hasFind = false;
                List<SearchSrtInfo> searchByLan = SrtInfoDao
                        .findSrtInFile(TextFormatUtil
                                .removeFileExtend(favSrtInfo.getSrtFile()),
                                favSrtInfo.getFromTime().toString(), favSrtInfo
                                        .getToTime().toString());
                for (SearchSrtInfo searchSrtInfo2 : searchByLan)
                {
                    if (TextFormatUtil
                            .removeFileExtend(favSrtInfo.getSrtFile())
                            .equalsIgnoreCase(
                                    TextFormatUtil
                                            .removeFileExtend(searchSrtInfo2
                                                    .getSrtFile())))
                    {
                        hasFind = true;
                        final int srtid = searchSrtInfo2.getDbId();
                        System.out.println(favSrtInfo.getEng() + " find ID:"
                                + srtid);
                        ContentValues cv = new ContentValues();
                        cv.put("srt_id", srtid);// 更新字段
                        if (db.update("fav_single", cv, "id = ?", new String[]
                        { String.valueOf(favSrtInfo.getId()) }) > 0)
                        {
                            System.out.println("更新成功!");
                        }
                        else
                        {
                            System.out.println("error");
                        }
                        break;
                    }
                }
                if (!hasFind)
                {
                    System.out.println(favSrtInfo.getEng() + " not found ");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            SrtInfoDao.closeDatabase();
            closeDb();
        }
        return true;
    }

    private static boolean insertFavChilds(int mfav_Id,
            List<FavoriteSingleSrt> sfavs)
    {
        try
        {
            for (FavoriteSingleSrt sfav : sfavs)
            {

                db.execSQL(
                        "INSERT INTO FAV_SINGLE(PID,SINDEX,FROM_TIME ,TO_TIME,ENG,CHS,SRT_ID) VALUES (?,?,?,?,?,?,?)",
                        new Object[]
                        { mfav_Id, sfav.getsIndex(), sfav.getFromTimeStr(),
                                sfav.getToTimeStr(),
                                StringEscapeUtils.escapeSql(sfav.getEng()),
                                StringEscapeUtils.escapeSql(sfav.getChs()),
                                sfav.getSrt_id() });

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getStrValue(Cursor c, String columnName)
    {
        return c.getString(c.getColumnIndex(columnName));
    }

    public static void closeDb()
    {
        if (db != null)
        {
            db.close();
            db = null;
        }
    }
}
