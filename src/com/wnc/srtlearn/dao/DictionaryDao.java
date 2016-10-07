package com.wnc.srtlearn.dao;

import java.util.HashSet;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.srtlearn.modules.translate.Topic;
import common.uihelper.MyAppParams;

public class DictionaryDao
{
	static SQLiteDatabase database;

	public static void openDatabase()
	{
		try
		{
			String databaseFilename = MyAppParams.DICTIONARY_DB;
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
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

	public static Set<Topic> getCETTopic(int srtId)
	{
		Set<Topic> topics = new HashSet<Topic>();
		if (srtId == 0)
		{
			return topics;
		}
		String topicStr = SrtInfoDao.getRelateTopicSrt(srtId);
		openDatabase();
		try
		{
			if (topicStr.length() > 0)
			{
				String sql = "select * from topic_resource res,dictionary dict,books" + " where res.topic=dict.topic_id and books.id=res.book_id and topic_id in (" + topicStr + ") order by book_id desc";
				Cursor c = database.rawQuery(sql, null);
				c.moveToFirst();
				while (!c.isAfterLast())
				{
					Topic topic = new Topic();
					topic.setBookName(c.getString(c.getColumnIndex("name")));
					topic.setTopic_id("" + c.getInt(c.getColumnIndex("topic_id")));
					topic.setTopic_word(c.getString(c.getColumnIndex("topic_word")));
					topic.setMean_cn(c.getString(c.getColumnIndex("mean_cn")));
					topic.setTopic_base_word(c.getString(c.getColumnIndex("topic_word")));
					topic.setState("BASIC");
					topics.add(topic);
					c.moveToNext();
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			closeDatabase();
		}
		return topics;
	}
}
