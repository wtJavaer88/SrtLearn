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
		SrtInfoDao.openDatabase();
		Cursor topicCursor = SrtInfoDao.getRelateTopicSrt(srtId);
		openDatabase();
		try
		{
			if (topicCursor != null && topicCursor.getCount() > 0)
			{
				topicCursor.moveToFirst();
				while (!topicCursor.isAfterLast())
				{
					String real_word = topicCursor.getString(topicCursor.getColumnIndex("real_word"));
					int topic_id = topicCursor.getInt(topicCursor.getColumnIndex("topic_id"));
					String sql = "select * from topic_resource res,dictionary dict,books" + " where res.topic=dict.topic_id and books.id=res.book_id and topic_id =" + topic_id + " order by book_id desc";
					Cursor c = database.rawQuery(sql, null);
					c.moveToFirst();
					while (!c.isAfterLast())
					{
						Topic topic = new Topic();
						topic.setBookName(c.getString(c.getColumnIndex("name")));
						topic.setTopic_id("" + c.getInt(c.getColumnIndex("topic_id")));
						topic.setTopic_word(real_word);
						topic.setMean_cn(c.getString(c.getColumnIndex("mean_cn")));
						topic.setTopic_base_word(c.getString(c.getColumnIndex("topic_word")));
						topic.setState("BASIC");
						topics.add(topic);
						c.moveToNext();
					}
					topicCursor.moveToNext();
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
			SrtInfoDao.closeDatabase();
			closeDatabase();
		}
		return topics;
	}
}
