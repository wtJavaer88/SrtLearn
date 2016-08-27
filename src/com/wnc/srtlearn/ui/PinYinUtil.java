package com.wnc.srtlearn.ui;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil
{
    /**
     * 单个汉字转拼音
     * 
     * @param hanzi
     * @return
     */
    public static String getSinglePinYin(char hanzi)
    {
        HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
        hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        hanyuPinyin.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[] pinyinArray = null;
        try
        {
            // 判断是否是汉字
            if (isChinese(hanzi))
            {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi,
                        hanyuPinyin);
            }
        }
        catch (BadHanyuPinyinOutputFormatCombination e)
        {
            e.printStackTrace();
        }
        return pinyinArray[0];
    }

    public static boolean isChinese(char hanzi)
    {
        return hanzi >= 0x4e00 && hanzi <= 0x9fa5;
    }

    /**
     * 获取一段汉字的拼音, 中间用空格隔开
     * 
     * @param s
     * @return
     */
    public static String getSeveralPinyin(String s)
    {
        String result = "";
        for (int i = 0; i < s.length(); i++)
        {
            if (isChinese(s.charAt(i)))
            {
                result += getSinglePinYin(s.charAt(i)) + " ";
            }
            else
            {
                result += s.charAt(i) + " ";
            }
        }
        return result.trim();
    }

    public static void main(String[] args)
    {
        String severalPinyin = getSeveralPinyin("武汉恒信");
        System.out.println(severalPinyin);
        // wŭ hàn héng xìn
    }

}
