package srt;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import srt.picker.Picker;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicRunTimeUtil;

public class DataParseThread extends Thread
{
    String curFile;
    final int COUNTS_PER_PAGE = 100;
    Picker picker;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(5);
    private String[] leftTimelineArr;
    private String[] rightTimelineArr;

    public DataParseThread(String curFile)
    {
        this.curFile = curFile;
        picker = srt.picker.PickerFactory.getPicker(curFile);
        if (picker == null)
        {
            throw new RuntimeException("字幕文件找不到!");
        }
    }

    @Override
    public void run()
    {
        final BasicRunTimeUtil basicRunTimeUtil = new BasicRunTimeUtil(
                "DataParseThread");
        basicRunTimeUtil.beginRun();
        new Task(0).run();
        int curPage = 1;
        while (picker.getSrtLineCounts() - curPage * COUNTS_PER_PAGE > 0)
        {
            executor.execute(new Task(curPage));
            curPage++;
        }
        executor.shutdown();
        while (executor.getActiveCount() > 0)
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        initTimeLineArr(DataHolder.getAllSrtInfos());
        DataHolder.resortList(curFile);
        System.out.println(BasicFileUtil.getFileName(curFile) + "字幕结果数:"
                + DataHolder.srtInfoMap.get(curFile).size());
        System.out
                .println("总耗时:" + basicRunTimeUtil.getCurrentRunMilliSecond());
        SrtTimeArr srtTimeArr = new SrtTimeArr();
        srtTimeArr.setSrtFile(curFile);
        srtTimeArr.setLeftTimelineArr(leftTimelineArr);
        srtTimeArr.setRightTimelineArr(rightTimelineArr);
        DataHolder.srtTimesMap.put(curFile, srtTimeArr);

    }

    public void initTimeLineArr(List<SrtInfo> currentSrtInfos)
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

    class Task implements Runnable
    {
        int curPage;

        public Task(int curPage)
        {
            this.curPage = curPage;
        }

        @Override
        public void run()
        {
            DataHolder.appendData(curFile, picker.getSrtInfos(COUNTS_PER_PAGE
                    * curPage, COUNTS_PER_PAGE * (curPage + 1)));
        }

    }
}
