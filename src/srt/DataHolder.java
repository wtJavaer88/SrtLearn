package srt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wnc.basic.BasicStringUtil;

public class DataHolder
{
    private static String fileKey = "";
    private static int srtIndex = -1;// 正常的浏览从0开始

    public static Map<String, List<SrtInfo>> map = new HashMap<String, List<SrtInfo>>();
    public static Map<String, Integer> indexMap = new HashMap<String, Integer>();

    public static Map<String, Boolean> completeMap = new HashMap<String, Boolean>();

    public static List<SrtInfo> getCurrentSrtInfos()
    {
        if (BasicStringUtil.isNullString(fileKey))
        {
            return null;
        }
        return map.get(fileKey);
    }

    public static int getCurrentSrtIndex()
    {
        return srtIndex;
    }

    /**
     * 设置播放位置,注意现在只能在复读模式下起作用
     * 
     * @param replayBeginIndex
     */
    public static void setCurrentSrtIndex(int replayBeginIndex)
    {
        srtIndex = replayBeginIndex;
    }

    public static String getFileKey()
    {
        return fileKey;
    }

    public static SrtInfo getNext()
    {
        srtIndex++;
        // System.out.println("next:srtIndex.." + srtIndex);
        return getSrtByIndex();
    }

    public static SrtInfo getPre()
    {
        srtIndex--;
        return getSrtByIndex();
    }

    public static SrtInfo getFirst()
    {
        srtIndex = 0;
        return getSrtByIndex();
    }

    public static SrtInfo getCurrent()
    {
        return getSrtByIndex();
    }

    public static SrtInfo getLast()
    {
        checkExist();
        List<SrtInfo> list = map.get(fileKey);
        srtIndex = list.size() - 1;
        return getSrtByIndex();
    }

    private static SrtInfo getSrtByIndex()
    {
        checkExist();
        indexMap.put(fileKey, srtIndex);
        List<SrtInfo> list = map.get(fileKey);
        if (srtIndex == -1)
        {
            srtIndex = 0;
            indexMap.put(fileKey, srtIndex);
            throw new RuntimeException("已经是第一条了!");
        }
        if (srtIndex == list.size())
        {
            srtIndex = list.size() - 1;
            indexMap.put(fileKey, srtIndex);
            throw new RuntimeException("已经读完了!");
        }
        return list.get(srtIndex);
    }

    public static void switchFile(String file)
    {
        fileKey = file;
        srtIndex = indexMap.get(fileKey) == null ? -1 : indexMap.get(fileKey);
        if (!completeMap.containsKey(file))
        {
            completeMap.put(file, false);
        }
    }

    private static void checkExist()
    {
        if (!map.containsKey(fileKey))
        {
            throw new RuntimeException("找不到该文件的字幕!");
        }
    }

    public static SrtInfo getClosestSrt(int hour, int minute, int second)
    {
        checkExist();
        long l = TimeHelper.getTime(hour, minute, second, 0);
        List<SrtInfo> list = map.get(fileKey);
        SrtInfo srtInfo = null;
        for (int i = 0; i < list.size(); i++)
        {
            SrtInfo info = list.get(i);
            if (formatSeconds(info.getFromTime()) >= l
                    || formatSeconds(info.getToTime()) >= l)
            {
                srtInfo = info;
                srtIndex = i;
                break;
            }
        }
        // 返回最后一个
        if (srtInfo == null)
        {
            srtInfo = list.get(list.size() - 1);
            srtIndex = list.size() - 1;
        }
        return srtInfo;
    }

    private static long formatSeconds(TimeInfo timeInfo)
    {
        return TimeHelper.getTime(timeInfo.getHour(), timeInfo.getMinute(),
                timeInfo.getSecond(), timeInfo.getMillSecond());
    }

    public static void appendData(String srtFile, List<SrtInfo> srtInfos)
    {
        if (!map.containsKey(srtFile))
        {
            map.put(srtFile, srtInfos);
        }
        else if (!srtInfos.isEmpty())
        {
            if (!completeMap.get(srtFile))
            {
                map.get(srtFile).addAll(srtInfos);
            }
        }
        else
        {
            completeMap.put(srtFile, true);
            resortList(srtFile);
        }
    }

    /**
     * 使用这个参数,而不是fileKey,因为fileKey只是代表最新的那个
     * 
     * @param srtFile
     */
    private static void resortList(String srtFile)
    {
        List<SrtInfo> list = map.get(srtFile);
        Collections.sort(list, new java.util.Comparator<SrtInfo>()
        {
            @Override
            public int compare(SrtInfo lhs, SrtInfo rhs)
            {
                return lhs.getFromTime().toString()
                        .compareTo(rhs.getFromTime().toString());
            }
        });
        srtIndex = 0;
        // System.out.println("list:srtIndex.." + srtIndex);
    }

    public static void setFileKey(String srtFile)
    {
        fileKey = srtFile;
    }
}
