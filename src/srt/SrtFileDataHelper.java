package srt;

import java.util.List;

import srt.picker.Picker;

import com.wnc.basic.BasicFileUtil;

public class SrtFileDataHelper
{
    final static int countsPerPage = 100;
    static Picker picker;

    public static void dataEntity(final String curFile)
    {
        leftTimelineArr = null;
        rightTimelineArr = null;
        picker = srt.picker.PickerFactory.getPicker(curFile);
        DataHolder.appendData(curFile, picker.getSrtInfos(0, countsPerPage));
        // 新进程去跑分页数据
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                getDataByPage(curFile);
            }
        }).start();
    }

    public static String[] leftTimelineArr;
    public static String[] rightTimelineArr;

    private static void getDataByPage(String curFile)
    {
        // 这儿的两个变量必须用局部的,防止切换字幕
        int curPage = 1;
        while (DataHolder.completeMap.containsKey(curFile)
                && !DataHolder.completeMap.get(curFile))
        {
            DataHolder.appendData(
                    curFile,
                    picker.getSrtInfos(countsPerPage * curPage, countsPerPage
                            * (curPage + 1)));
            curPage++;
        }
        if (DataHolder.srtInfoMap.containsKey(curFile))
        {
            initTimeLineArr(DataHolder.getAllSrtInfos());
            System.out.println(BasicFileUtil.getFileName(curFile) + "字幕结果数:"
                    + DataHolder.srtInfoMap.get(curFile).size());
        }
    }

    private static void initTimeLineArr(List<SrtInfo> currentSrtInfos)
    {
        if (leftTimelineArr == null || rightTimelineArr == null)
        {
            int size = currentSrtInfos.size();
            leftTimelineArr = new String[size];
            rightTimelineArr = new String[size];
            for (int i = 0; i < size; i++)
            {
                SrtInfo srtInfo = currentSrtInfos.get(i);
                leftTimelineArr[i] = srtInfo.getFromTime().toString();
                rightTimelineArr[i] = srtInfo.getToTime().toString();
            }
        }
    }

}
