package common.utils;

import java.io.IOException;
import java.util.Arrays;

public class PinyinStructUtil
{
	// a、o、e、i、u、v
	// ai 、ei、 ui 、ao、 ou、 iu 、ie 、ve、 er、 an 、en 、in、 un 、vn 、ang 、eng、 ing
	// 、ong
	// b、p、m、f、d、t、n、l、g、k、h、j、q、x、zh、ch、sh、z、c、s 、 y、w、r
	public static String[][] basicAll = new String[][] { { "a", "ā", "á", "ă", "à" }, { "e", "ē", "é", "ĕ", "è" }, { "i", "ī", "í", "ĭ", "ì" }, { "o", "ō", "ó", "ŏ", "ò" }, { "u", "ū", "ú", "ŭ", "ù" }, { "ü", "ǖ", "ǘ", "ǚ", "ǜ" } };
	public final static String[] basic = new String[] { "a", "e", "i", "o", "u", "ü" };

	public static int getBasicShengdiao(String pinyin)
	{
		if (isContains(0, pinyin, "ang", "an", "ao", "ai", "ua"))
		{
			return 0;
		}
		if (isContains(1, pinyin, "eng", "en", "ei", "ie", "er", "ue", "üe"))
		{
			return 1;
		}
		if (isContains(2, pinyin, "ing", "in", "ui"))
		{
			return 2;
		}
		if (isContains(3, pinyin, "ong", "ou"))
		{
			return 3;
		}
		if (isContains(4, pinyin, "un", "iu"))
		{
			return 4;
		}
		if (isContains(5, pinyin, "ün"))
		{
			return 5;
		}
		if (isContains(0, pinyin, "a"))
		{
			return 0;
		}
		if (isContains(1, pinyin, "e"))
		{
			return 1;
		}
		if (isContains(2, pinyin, "i"))
		{
			return 2;
		}
		if (isContains(3, pinyin, "o"))
		{
			return 3;
		}
		if (isContains(4, pinyin, "u"))
		{
			return 4;
		}
		if (isContains(5, pinyin, "ü"))
		{
			return 5;
		}
		return 0;
	}

	public static boolean isContains(int keyIndex, String pinyin, String... strings)
	{
		String key = basic[keyIndex];
		for (String s : strings)
		{
			for (String bs : basicAll[keyIndex])
			{
				String newS = s.replace(key, bs);
				if (pinyin.contains(newS))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException
	{
		testdialog();
		// testA();
		// testE();
		// testI();
		// testO();
		// System.out.println(Arrays.toString(getBasicShengdiao("zăi")));
	}

	private static void testI()
	{
		System.out.println(PinYinUtil.getSinglePinYin('机'));
		System.out.println(PinYinUtil.getSinglePinYin('及'));
		System.out.println(PinYinUtil.getSinglePinYin('几'));
		System.out.println(PinYinUtil.getSinglePinYin('季'));
	}

	private static void testO()
	{
		System.out.println(PinYinUtil.getSinglePinYin('欧'));
		System.out.println(PinYinUtil.getSinglePinYin('猴'));
		System.out.println(PinYinUtil.getSinglePinYin('偶'));
		System.out.println(PinYinUtil.getSinglePinYin('沤'));
	}

	private static void testE()
	{
		System.out.println(PinYinUtil.getSinglePinYin('黑'));
		System.out.println(PinYinUtil.getSinglePinYin('赔'));
		System.out.println(PinYinUtil.getSinglePinYin('姐'));
		System.out.println(PinYinUtil.getSinglePinYin('配'));
	}

	private static void testA()
	{
		System.out.println(PinYinUtil.getSinglePinYin('该'));
		System.out.println(PinYinUtil.getSinglePinYin('孩'));
		System.out.println(PinYinUtil.getSinglePinYin('载'));
		System.out.println(PinYinUtil.getSinglePinYin('爱'));
	}

	public static void testdialog()
	{
		String a = "据女人推算，锰结核的分布范围几乎相当于北海道面积的一半，约达4.4万平方公里。据悉，虽然这些锰结核分布在天然资源管辖范围所及的专属经济区(EEZ)之内，但因其位于水深5500～5800米的深海，目前尚未确立有效的打捞方法。";
		for (int i = 0; i < a.length(); i++)
		{
			if (PinYinUtil.isChinese(a.charAt(i)))
			{
				String pinyin = PinYinUtil.getSinglePinYin(a.charAt(i));
				System.out.println(pinyin + ": " + Arrays.toString(basicAll[getBasicShengdiao(pinyin)]));
			}
		}
	}

	public static void testCode()
	{
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				String x = basicAll[i][j];
				int c = (int) (x.charAt(0));
				System.out.println(x + ":" + c);
			}
		}

	}
}
