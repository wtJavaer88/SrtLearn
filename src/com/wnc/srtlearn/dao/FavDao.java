package com.wnc.srtlearn.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import srt.SrtInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.pojo.FavoriteMultiSrt;
import com.wnc.srtlearn.pojo.FavoriteSingleSrt;
import com.wnc.srtlearn.setting.Backup;

public class FavDao
{
	private static SQLiteDatabase db = null;

	public static void initDb(Context context)
	{
		if (db == null)
		{
			db = context.openOrCreateDatabase("srtlearn.db", Context.MODE_PRIVATE, null);
		}
	}

	public static boolean isExistSingle(Context context, SrtInfo mfav, String srtFile)
	{
		initDb(context);
		try
		{
			Cursor c = db.rawQuery("SELECT * FROM FAV_MULTI M LEFT JOIN FAV_SINGLE S ON M.ID=S.PID  WHERE SRTFILE=? AND S.FROM_TIME=? AND S.TO_TIME=?", new String[] { srtFile, mfav.getFromTime().toString(), mfav.getToTime().toString() });// 注意大小写
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

	public static boolean isExistMulti(Context context, FavoriteMultiSrt mfav)
	{
		initDb(context);
		try
		{
			Cursor c = db.rawQuery("SELECT * FROM FAV_MULTI WHERE SRTFILE=? AND FROM_TIME=? AND TO_TIME=?", new String[] { mfav.getSrtFile(), mfav.getFromTimeStr(), mfav.getToTimeStr() });// 注意大小写
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

	public static boolean insertFavMulti(Context context, FavoriteMultiSrt mfav, List<FavoriteSingleSrt> sfavs)
	{
		boolean ret = false;
		try
		{
			initDb(context);
			db.beginTransaction();
			db.execSQL("INSERT INTO FAV_MULTI(FAV_TIME,SRTFILE,FROM_TIME ,TO_TIME,HAS_CHILD,TAG) VALUES (?,?,?,?,?,?)", new Object[] { mfav.getFavTimeStr(), mfav.getSrtFile(), mfav.getFromTimeStr(), mfav.getToTimeStr(), mfav.getHasChild(), mfav.getTag() });
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
					Backup.canBackup = true;
				db.setTransactionSuccessful();// 设置成功
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

	private static boolean insertFavChilds(int mfav_Id, List<FavoriteSingleSrt> sfavs)
	{
		try
		{
			for (FavoriteSingleSrt sfav : sfavs)
			{

				db.execSQL("INSERT INTO FAV_SINGLE(PID,SINDEX,FROM_TIME ,TO_TIME,ENG,CHS) VALUES (?,?,?,?,?,?)",
						new Object[] { mfav_Id, sfav.getsIndex(), sfav.getFromTimeStr(), sfav.getToTimeStr(), StringEscapeUtils.escapeSql(sfav.getEng()), StringEscapeUtils.escapeSql(sfav.getChs()) });

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
