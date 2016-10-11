package com.wnc.srtlearn.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import srt.SearchSrtInfo;
import srt.SrtInfo;
import srt.TimeHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicNumberUtil;
import common.uihelper.MyAppParams;

public class SrtInfoDao
{
    static SQLiteDatabase database;

    public static void openDatabase()
    {
        try
        {
            String databaseFilename = MyAppParams.SRT_DB;
            database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
                    null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void closeDatabase()
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    public static boolean isConnect()
    {
        return database != null && database.isOpen();
    }

    public static List<SearchSrtInfo> searchByLan(boolean isEng, String keyWord)
    {
        keyWord = StringEscapeUtils.escapeSql(keyWord);
        List<SearchSrtInfo> list = new ArrayList<SearchSrtInfo>();
        try
        {
            if (isConnect())
            {
                final String engOrchs = isEng ? "eng" : "chs";
                final String engOrchs2 = !isEng ? "eng" : "chs";
                String sql = "select s.*,t.fromtime,t.totime,e.name from (select min(id) id,min(episode_id) episode_id, min("
                        + engOrchs2
                        + ") "
                        + engOrchs2
                        + ", min(sindex) sindex, "
                        + engOrchs
                        + " from srtinfo group by "
                        + engOrchs
                        + ") s  left join episode e on s.episode_id=e.id left join timeline t on s.id=t.id where  "
                        + engOrchs
                        + " like '%"
                        + keyWord
                        + "%' order by s.episode_id,s.id asc";
                Cursor c = database.rawQuery(sql, null);
                c.moveToFirst();
                while (!c.isAfterLast())
                {
                    SearchSrtInfo srtInfo = new SearchSrtInfo();
                    srtInfo.setSrtFile(c.getString(c.getColumnIndex("name")));
                    srtInfo.setDbId(BasicNumberUtil.getNumber(c.getString(c
                            .getColumnIndex("id"))));
                    srtInfo.setChs(c.getString(c.getColumnIndex("chs")));
                    srtInfo.setEng(c.getString(c.getColumnIndex("eng")));
                    srtInfo.setFromTime(TimeHelper.parseTimeInfo(c.getString(c
                            .getColumnIndex("fromtime"))));
                    srtInfo.setToTime(TimeHelper.parseTimeInfo(c.getString(c
                            .getColumnIndex("totime"))));
                    srtInfo.setSrtIndex(BasicNumberUtil.getNumber(c.getString(c
                            .getColumnIndex("sindex"))));
                    list.add(srtInfo);
                    c.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    public static List<SearchSrtInfo> findSrtInFile(String file,
            String fromtime, String totime)
    {
        List<SearchSrtInfo> list = new ArrayList<SearchSrtInfo>();
        try
        {
            if (isConnect())
            {
                String sql = "select s.*,t.fromtime,t.totime,e.name from srtinfo s  left join episode e on s.episode_id=e.id left join timeline t on s.id=t.id where name like '"
                        + file
                        + "%' and fromtime='"
                        + fromtime
                        + "' and totime='" + totime + "'";
                Cursor c = database.rawQuery(sql, null);
                c.moveToFirst();
                while (!c.isAfterLast())
                {
                    SearchSrtInfo srtInfo = new SearchSrtInfo();
                    srtInfo.setSrtFile(c.getString(c.getColumnIndex("name")));
                    srtInfo.setDbId(BasicNumberUtil.getNumber(c.getString(c
                            .getColumnIndex("id"))));
                    srtInfo.setChs(c.getString(c.getColumnIndex("chs")));
                    srtInfo.setEng(c.getString(c.getColumnIndex("eng")));
                    srtInfo.setFromTime(TimeHelper.parseTimeInfo(c.getString(c
                            .getColumnIndex("fromtime"))));
                    srtInfo.setToTime(TimeHelper.parseTimeInfo(c.getString(c
                            .getColumnIndex("totime"))));
                    srtInfo.setSrtIndex(BasicNumberUtil.getNumber(c.getString(c
                            .getColumnIndex("sindex"))));
                    list.add(srtInfo);
                    c.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isExistEpidose(String srtfile)
    {
        openDatabase();
        boolean b = false;
        try
        {
            String sql = "select * from episode where name like '%" + srtfile
                    + "%'";
            Cursor c = database.rawQuery(sql, null);
            b = c.moveToFirst();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
        return b;
    }

    public static List<SrtInfo> getSrtInfos(String season, String episode)
    {
        openDatabase();
        season = StringEscapeUtils.escapeSql(season);
        episode = StringEscapeUtils.escapeSql(episode);
        List<SrtInfo> list = new ArrayList<SrtInfo>();
        try
        {
            if (isConnect())
            {
                String sql = "select * from srtinfo s LEFT JOIN timeline t on s.id=t.id where s.episode_id=(select min(id) from episode where name LIKE '%"
                        + season + "%" + episode + "%') order by id asc";
                Cursor c = database.rawQuery(sql, null);
                c.moveToFirst();
                while (!c.isAfterLast())
                {
                    SrtInfo srtInfo = new SrtInfo();
                    srtInfo.setDbId(c.getInt(c.getColumnIndex("id")));
                    srtInfo.setChs(c.getString(c.getColumnIndex("chs")));
                    srtInfo.setEng(c.getString(c.getColumnIndex("eng")));
                    srtInfo.setFromTime(TimeHelper.parseTimeInfo(c.getString(c
                            .getColumnIndex("fromtime"))));
                    srtInfo.setToTime(TimeHelper.parseTimeInfo(c.getString(c
                            .getColumnIndex("totime"))));
                    srtInfo.setSrtIndex(BasicNumberUtil.getNumber(c.getString(c
                            .getColumnIndex("sindex"))));
                    list.add(srtInfo);
                    c.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
        return list;
    }

    public static String getNextSrt(String season, String episode)
    {
        openDatabase();
        season = StringEscapeUtils.escapeSql(season);
        episode = StringEscapeUtils.escapeSql(episode);
        System.out.println(season + " getNextSrt  " + episode);
        try
        {
            if (isConnect())
            {
                String sql = "select * from episode where id>(select min(id) from episode where name like '%"
                        + episode
                        + "%' and name like '%"
                        + season
                        + "%') and name like '%" + season + "%'";
                Cursor c = database.rawQuery(sql, null);
                if (c.getCount() > 0)
                {
                    c.moveToFirst();
                    return c.getString(c.getColumnIndex("name"));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
        return null;
    }

    public static Cursor getRelateTopicSrt(int srtId)
    {
        Cursor c = null;
        try
        {
            if (isConnect())
            {
                String sql = "select * from srt_word where srt_id=" + srtId;
                c = database.rawQuery(sql, null);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
}
