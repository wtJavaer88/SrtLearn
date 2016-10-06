package common.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;

public class TextFormatUtil
{
	/**
	 * 获取格式化的只有一位小数的数字
	 * 
	 * @param num
	 * @return
	 */
	public static String getFormatMoneyStr(double num)
	{
		return BasicNumberUtil.getDoubleFormat(num, 1);
	}

	/**
	 * 获取当前的月份,省略开头的0
	 * 
	 * @param yearMonthStr
	 * @return
	 */
	public static String getMonthStr(String yearMonthStr)
	{
		if (BasicStringUtil.isNullString(yearMonthStr))
		{
			return "1";
		}
		return yearMonthStr.substring(4, 6).replaceFirst("0", "");
	}

	/**
	 * 从日期格式中移除-符号,以便插入数据库
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String getDateStrForDb(String dateStr)
	{
		return dateStr.replace("-", "");
	}

	/**
	 * 在日期中加上分隔符-
	 * 
	 * @param day
	 * @return
	 */
	public static String addSeparatorToDay(String day)
	{
		return day.substring(0, 4) + "-" + day.substring(4, 6) + "-" + day.substring(6, 8);
	}

	/**
	 * 在时间中加上分隔符冒号:
	 * 
	 * @param time
	 * @return
	 */
	public static String addSeparatorToTime(String time)
	{
		return time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
	}

	/**
	 * 在日期中加上年月日
	 * 
	 * @param day
	 * @return
	 */
	public static String addChnToDay(String day)
	{
		return day.replaceFirst("-", "年").replaceFirst("-", "月") + "日";
	}

	/**
	 * 在日期中加上年月日
	 * 
	 * @param day
	 * @return
	 */
	public static String addChnToDayNoSeperate(String day)
	{
		StringBuilder accum = new StringBuilder();
		accum.append(day.substring(0, 4));
		accum.append("年");
		accum.append(day.substring(4, 6));
		accum.append("月");
		accum.append(day.substring(6, 8));
		accum.append("日");
		return accum.toString();
	}

	/**
	 * 根据指定字符串获取日期
	 * 
	 * @param timeStr
	 * @return
	 */
	public static Date getFormatedDate(String timeStr)
	{
		Date date = null;
		String format = "yyyy-MM-dd HH:mm:ss";
		if (!timeStr.contains(":"))
		{
			format = "yyyy-MM-dd";
		}
		if (!timeStr.contains("-"))
		{
			format = format.replace("-", "");
		}

		try
		{
			date = new SimpleDateFormat(format).parse(timeStr);
		}
		catch (ParseException e)
		{
			Log.i("err", "日期转换异常:" + e.getMessage());
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取某一项消费的数据格式
	 * 
	 * @param percent
	 * @param cost
	 * @param name
	 * @return
	 */
	public static String getPercentWithName(float percent, double cost, String name)
	{
		String percentStr = TextFormatUtil.getFormatMoneyStr(percent * 100);
		String costStr = TextFormatUtil.getFormatMoneyStr(cost);
		StringBuilder accumStr = new StringBuilder();
		accumStr.append(name + ":\n");
		accumStr.append(percentStr + "%  ");
		accumStr.append("¥" + costStr);
		return accumStr.toString();
	}

	/***
	 * 从备注中分割出文件,规则是"[文件名]",括号左右可能会有纯备注文字
	 * 
	 * @param memo
	 * @return
	 */
	public static String[] getSegmentsInMemo(String memo)
	{
		String[] list = new String[] {};
		if (memo != null && BasicStringUtil.isNotNullString(memo.trim()))
		{
			list = memo.split("[\\[\\]]");
		}
		return list;
	}

	/**
	 * 支持中英文的逗号冒号
	 * 
	 * @param rangeStr
	 * @return
	 */
	public static boolean isNumberRange(String rangeStr)
	{
		if (BasicStringUtil.isNullString(rangeStr))
		{
			return false;
		}
		if (rangeStr.replace(" ", "").matches("\\d+\\.?\\d*[,，:：]\\d+\\.?\\d*"))
		{
			return true;
		}
		return false;
	}

	/**
	 * 获取不带后缀的文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoExtend(String filePath)
	{
		int dotPos = filePath.lastIndexOf(".");
		int separatorPos = filePath.lastIndexOf(File.separator);
		if (dotPos != -1)
		{
			if (separatorPos != -1)
			{
				return filePath.substring(separatorPos + 1, dotPos);
			}
			else
			{
				return filePath.substring(0, dotPos);
			}
		}
		else
		{
			if (separatorPos != -1)
			{
				return filePath.substring(separatorPos + 1);
			}

		}
		return filePath;
	}

	/**
	 * 去掉路径结尾的后缀
	 * 
	 * @param filePath
	 * @return
	 */
	public static String removeFileExtend(String filePath)
	{
		int dotPos = filePath.lastIndexOf(".");
		int j = filePath.lastIndexOf(File.separator);
		if (dotPos != -1 && dotPos > j)
		{
			return filePath.substring(0, dotPos);
		}
		return filePath;
	}

	/**
	 * 判断字符串是否包含中文
	 * 
	 * @param string
	 * @return
	 */
	public static boolean containsChinese(String string)
	{
		return BasicStringUtil.isNotNullString(PatternUtil.getFirstPattern(string, "[\u4e00-\u9fa5]"));
	}

	/**
	 * 判断字符是否为中文
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isChineseChar(char ch)
	{
		return ch >= 0x4e00 && ch <= 0x9fa5;
	}

	/**
	 * 把字符串变成url通用的编码,一般用于中文
	 * 
	 * @param str
	 * @return
	 */
	public static String getUrlEncodeStr(String str)
	{
		return java.net.URLEncoder.encode(str);
	}

	/**
	 * 去掉标点符号
	 * 
	 * @param s
	 * @return
	 */
	public static String getTextNoSymbol(String s)
	{
		return s.trim().replaceAll("[\",\\.!?，。！？、\\s]", "");
	}

}
