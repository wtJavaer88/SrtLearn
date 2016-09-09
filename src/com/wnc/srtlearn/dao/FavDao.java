package com.wnc.srtlearn.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.pojo.FavoriteMultiSrt;
import com.wnc.srtlearn.pojo.FavoriteSingleSrt;

public class FavDao
{
    private static SQLiteDatabase db = null;

    public static void initDb(Context context)
    {
        db = context.openOrCreateDatabase("srtlearn.db", Context.MODE_PRIVATE,
                null);
    }

    public static boolean insertFavMulti(Context context,
            FavoriteMultiSrt mfav, List<FavoriteSingleSrt> sfavs)
            throws RuntimeException
    {
        initDb(context);
        try
        {
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
                insertFavChilds(mfav_Id, sfavs);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        finally
        {
            closeDb();
        }
        return true;
    }

    private static void insertFavChilds(int mfav_Id,
            List<FavoriteSingleSrt> sfavs)
    {
        for (FavoriteSingleSrt sfav : sfavs)
        {
            db.execSQL(
                    "INSERT INTO FAV_SINGLE(PID,SINDEX,FROM_TIME ,TO_TIME,ENG,CHS) VALUES (?,?,?,?,?,?)",
                    new Object[]
                    { mfav_Id, sfav.getsIndex(), sfav.getFromTimeStr(),
                            sfav.getToTimeStr(),
                            StringEscapeUtils.escapeSql(sfav.getEng()),
                            StringEscapeUtils.escapeSql(sfav.getChs()) });
        }
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
        }
    }
}
