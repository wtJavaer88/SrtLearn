package srt;

import java.io.File;

import com.wnc.basic.BasicFileUtil;

public class SrtTextHelper
{
    public static String getClearText(String text)
    {
        return text.replaceAll("\\{.*?\\}", "").replaceAll("<.*?>", "");
    }

    /**
     * 根据当前字幕文件获取字幕同名的文件夹
     * 
     * @param srtFilePath
     * @return
     */
    public static String getSrtVoiceFolder(String srtFilePath)
    {
        return BasicFileUtil.getFileParent(srtFilePath) + File.separator
                + common.TextFormatUtil.getFileNameNoExtend(srtFilePath);
    }

    public static String getSrtVoiceLocation()
    {
        return getSrtVoiceFolder(DataHolder.getFileKey())
                + File.separator
                + DataHolder.getCurrent().getFromTime().toString()
                        .replace(":", "") + ".mp3";
    }

    public static boolean isSrtfile(File f)
    {
        return f.isFile()
                && (f.getName().endsWith("ass") || f.getName().endsWith("srt")
                        || f.getName().endsWith("ssa") || f.getName().endsWith(
                        "cnpy"));
    }
}
