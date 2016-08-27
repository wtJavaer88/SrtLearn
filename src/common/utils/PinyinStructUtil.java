package common.utils;

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
        if (pinyin.contains(""))
        {

        }
        return basic[1];
    }
}
