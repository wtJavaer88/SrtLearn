package srt;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import com.wnc.basic.BasicFileUtil;
import common.utils.TextFormatUtil;

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
                + common.utils.TextFormatUtil.getFileNameNoExtend(srtFilePath);
    }

    public static String getSrtVoiceLocation(String srtFile, String voiceTimeStr)
    {
        return getSrtVoiceFolder(srtFile) + File.separator
                + voiceTimeStr.replace(":", "") + ".mp3";
    }

    public static Queue<String> getSrtVoicesInRange(String srtFile,
            String voiceTimeStr1, String voiceTimeStr2)
    {
        Queue<String> queue = new LinkedList<String>();
        final String folder = getSrtVoiceFolder(srtFile);
        String m1 = folder + File.separator + voiceTimeStr1.replace(":", "")
                + ".mp3";
        String m2 = folder + File.separator + voiceTimeStr2.replace(":", "")
                + ".mp3";
        if (BasicFileUtil.isExistFile(m1))
        {
            queue.offer(m1);
        }
        if (BasicFileUtil.isExistFile(m2))
        {
            queue.offer(m2);
        }

        return queue;
    }

    public static boolean isSrtfile(File f)
    {
        return f.isFile()
                && (f.getName().endsWith(".ass")
                        || f.getName().endsWith(".srt")
                        || f.getName().endsWith(".ssa")
                        || f.getName().endsWith(".cnpy") || f.getName()
                        .endsWith(".lrc"));
    }

    public static String concatTimeline(TimeInfo fTimeinfo, TimeInfo tTimeinfo)
    {
        return fTimeinfo.toString() + " ---> " + tTimeinfo.toString();
    }

    public static String getSxFile(String srtFilePath)
    {
        if (BasicFileUtil.isExistFile(srtFilePath))
        {
            File f = new File(srtFilePath);
            String folder = f.getParent();
            int i = folder.lastIndexOf("/");
            if (i != -1)
            {
                folder = folder.substring(i + 1);
                String name = TextFormatUtil.getFileNameNoExtend(f.getName());
                return folder + " / " + name;
            }
        }
        return "";
    }
}
