package com.wnc.srtlearn.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.srtlearn.monitor.WorkMgr;
import com.wnc.srtlearn.monitor.work.WORKTYPE;

public class WorkDao
{
	private static SQLiteDatabase db = null;

	public static void initDb(Context context)
	{
		db = context.openOrCreateDatabase("srtlearn.db", Context.MODE_PRIVATE, null);
	}

	public static boolean log(Context context, WORKTYPE worktype, String info)
	{
		int typeId = WorkMgr.getTypeId(worktype);
		if (db == null)
		{
			initDb(context);
		}
		try
		{
			db.execSQL("INSERT INTO LOG(type, info,create_time) VALUES (?,?,?)", new Object[] { typeId, info, BasicDateUtil.getCurrentDateTimeString() });
			closeDb();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		return true;
	}

	/**
	 * 本次运行的各项工作记录
	 * 
	 * @param run_id
	 * @param work_type
	 * @param work_count
	 * @param work_time
	 * @return
	 * @throws RuntimeException
	 */
	public static boolean insertWorkMgr(int run_id, WORKTYPE work_type, int work_count, long work_time) throws RuntimeException
	{
		int typeId = WorkMgr.getTypeId(work_type);
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return false;
		}
		try
		{
			db.execSQL("INSERT INTO WORKMGR(DAY,RUN_ID,WORK_TYPE,WORK_COUNT ,WORK_TIME) VALUES (?,?,?,?,?)", new Object[] { BasicDateUtil.getCurrentDateString(), run_id, typeId, work_count, work_time });
			// trigger();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		return true;
	}

	/**
	 * 每次运行记录
	 * 
	 * @param entertime
	 * @param exittime
	 * @param duration
	 * @return
	 * @throws RuntimeException
	 */
	public static int insertRunRecord(String entertime, String exittime, String duration) throws RuntimeException
	{
		int runId = 0;
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return runId;
		}
		try
		{
			db.execSQL("INSERT INTO RUN_RECORD(ENTER_TIME, EXIT_TIME,DURATION) VALUES (?,?,?)", new Object[] { entertime, exittime, duration });
			Cursor c = db.rawQuery("SELECT MAX(ID) MAXID FROM RUN_RECORD", null);// 注意大小写
			if (c.moveToNext())
			{
				runId = BasicNumberUtil.getNumber(getStrValue(c, "MAXID"));
			}
			c.close();
			// trigger();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		return runId;
	}

	private static boolean checkExist()
	{
		Cursor c = db.rawQuery("SELECT * FROM RUN_RECORD", new String[] {});
		if (c.moveToNext())
		{
			return true;
		}
		c.close();
		return false;
	}

	public static int getRunCounts()
	{
		Cursor c = db.rawQuery("SELECT * FROM RUN_RECORD", new String[] {});
		int num = 0;
		while (c.moveToNext())
		{
			num++;
		}
		c.close();
		return num;
	}

	// private static void trigger()
	// {
	// BackUpDataUtil.canBackUpDb = true;
	// }

	private static String getStrValue(Cursor c, String columnName)
	{
		return c.getString(c.getColumnIndex(columnName));
	}

	public static List<String> getAllMembersForSearch()
	{
		List<String> searchMembers = new ArrayList<String>();
		if (list != null)
		{
			searchMembers.clear();
			searchMembers.add("全部成员");
			for (String member : list)
			{
				searchMembers.add(member);
			}
		}
		else
		{
			searchMembers.add("全部成员");
		}

		return searchMembers;
	}

	static List<String> list = null;

	public static List<String> getAllMembers()
	{
		if (list != null)
		{
			return list;
		}
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return list;
		}
		Cursor c = db.rawQuery("SELECT name FROM member WHERE ISDEL = 0 ORDER BY ITEM_ORDER ASC", null);
		list = new ArrayList<String>();
		while (c.moveToNext())
		{
			list.add(getStrValue(c, "NAME"));
		}
		c.close();
		return list;
	}

	public static boolean deleteByName(String name)
	{
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return false;
		}

		if (checkExistInTrasactions(name))
		{
			throw new RuntimeException("<" + name + ">该成员在消费表中已有引用!");
		}

		try
		{
			ContentValues cv = new ContentValues();
			cv.put("isdel", 1);
			if (db.update("member", cv, "name = ?", new String[] { String.valueOf(name) }) == 0)
			{
				return false;
			}
			list.remove(name);
			// trigger();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("修改成员表时异常," + ex.getMessage());
		}
		return true;
	}

	private static boolean checkExistInTrasactions(String name)
	{
		Cursor c = db.rawQuery("SELECT * FROM transactions WHERE ISDEL=0 AND member = ?", new String[] { name });
		if (c.moveToNext())
		{
			return true;
		}
		c.close();
		return false;
	}

	public static void closeDb()
	{
		if (db != null)
		{
			db.close();
		}
	}
}
