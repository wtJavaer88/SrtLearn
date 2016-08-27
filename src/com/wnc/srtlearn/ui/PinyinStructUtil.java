package com.wnc.srtlearn.ui;

import java.io.IOException;
import java.util.Arrays;

public class PinyinStructUtil
{
    // a、o、e、i、u、v
    // ai 、ei、 ui 、ao、 ou、 iu 、ie 、ve、 er、 an 、en 、in、 un 、vn 、ang 、eng、 ing
    // 、ong
    // b、p、m、f、d、t、n、l、g、k、h、j、q、x、zh、ch、sh、z、c、s 、 y、w、r
    public static String[][] basic = new String[][]
    {
    { "a", "ā", "á", "ǎ", "à" },
    { "e", "ē", "é", "ě", "è" },
    { "i", "ī", "í", "ǐ", "ì" },
    { "o", "ō", "ó", "ǒ", "ǒ" },
    { "u", "ū", "ú", "ǔ", "ù" },
    { "ü", "ǖ", "ǘ", "ǚ", "ǜ" } };

    public static String[] getBasicShengdiao(String pinyin)
    {
        if (isContains(0, pinyin, "ang", "an", "ao", "ai"))
        {
            return basic[0];
        }
        if (isContains(1, pinyin, "eng", "en", "ei", "ie", "er", "ue", "üe"))
        {
            return basic[1];
        }
        if (isContains(2, pinyin, "ing", "in", "ui"))
        {
            return basic[2];
        }
        if (isContains(3, pinyin, "ong", "ou"))
        {
            return basic[3];
        }
        if (isContains(4, pinyin, "un", "iu"))
        {
            return basic[4];
        }
        if (isContains(5, pinyin, "ün"))
        {
            return basic[5];
        }
        if (isContains(0, pinyin, "a"))
        {
            return basic[0];
        }
        if (isContains(1, pinyin, "e"))
        {
            return basic[1];
        }
        if (isContains(2, pinyin, "i"))
        {
            return basic[2];
        }
        if (isContains(3, pinyin, "o"))
        {
            return basic[3];
        }
        if (isContains(4, pinyin, "u"))
        {
            return basic[4];
        }
        if (isContains(5, pinyin, "ü"))
        {
            return basic[5];
        }
        return null;
    }

    public static boolean isContains(int keyIndex, String pinyin,
            String... strings)
    {
        String key = basic[keyIndex][0];
        for (String s : strings)
        {
            for (String bs : basic[keyIndex])
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
        String a = "请下载后24小时内删除，有能力请支持正版";
        for (int i = 0; i < a.length(); i++)
        {
            if (PinYinUtil.isChinese(a.charAt(i)))
            {
                String pinyin = PinYinUtil.getSinglePinYin(a.charAt(i));
                System.out.println(pinyin + ": "
                        + Arrays.toString(getBasicShengdiao(pinyin)));
            }
        }
    }
}
