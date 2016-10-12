package common.utils;

import java.util.ArrayList;
import java.util.List;

public class WordSplit
{
	private static List<String> wordAndChars;

	public static void main(String[] args)
	{
		String s = "Long ago in ancient China, big boom.!";
		wordAndChars = getWordAndChars(s);
		System.out.println(wordAndChars);
	}

	public static List<String> getWordAndChars(String s)
	{
		List<String> list = new ArrayList<String>();
		int i = 0;
		int j = i;
		while (j < s.length())
		{

			if (isEngChar(s.charAt(j)))
			{
				if (j > 0 && !isEngChar(s.charAt(j - 1)))
				{
					// System.out.println("split1:" + s.substring(j - 1, j) +
					// "@");
					list.add(s.substring(j - 1, j));
					i = j;
				}
			}
			else
			{
				// System.out.println("split2:" + s.substring(i, j) + "@");
				list.add(s.substring(i, j));
				i = j;
			}
			j++;
		}
		if (j > 0 && !isEngChar(s.charAt(j - 1)))
		{
			// System.out.println("split3:" + s.substring(i, j) + "@");
			list.add(s.substring(i, j));
		}
		return list;
	}

	private static boolean isEngChar(int charAt)
	{
		return (charAt >= 65 && charAt <= 90) || (charAt >= 97 && charAt <= 122);
	}
}
