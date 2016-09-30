package srt;

import java.util.List;

import srt.ex.SrtException;
import srt.ex.SrtParseErrorException;
import srt.picker.Picker;
import srt.picker.PickerFactory;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicRunTimeUtil;
import com.wnc.basic.BasicStringUtil;

/**
 * 在正常模式下,从0时间轴开始取10条
 * <p>
 * 在指定浏览模式下,从指定时间轴附近取两分钟的内容
 * 
 * @author cpr216
 * 
 */
public class DataParseThread extends Thread
{
    String curFile;
    String seekTime;
    int curPage = 0;
    final int COUNTS_PER_PAGE = 100;
    Picker picker;
    private String[] leftTimelineArr;
    private String[] rightTimelineArr;

    public DataParseThread(String curFile) throws SrtParseErrorException
    {
        this.curFile = curFile;
        picker = PickerFactory.getPicker(curFile);
        if (picker == null)
        {
            throw new SrtParseErrorException();
        }
    }

    public DataParseThread(String curFile, String seekTime)
            throws SrtParseErrorException
    {
        this.curFile = curFile;
        this.seekTime = seekTime;
        picker = PickerFactory.getPicker(curFile);
        if (picker == null)
        {
            throw new SrtParseErrorException();
        }
    }

    @Override
    public void run()
    {
        final BasicRunTimeUtil basicRunTimeUtil = new BasicRunTimeUtil(
                "DataParseThread");
        basicRunTimeUtil.beginRun();

        // seekTime = "00:02:30,440";
        // seekTime = "00:00:00,000";

        List<SrtInfo> cachedSrtInfos;
        if (BasicStringUtil.isNullString(seekTime))
        {
            cachedSrtInfos = picker.get10CacheSrtInfos(null);
        }
        else
        {
            cachedSrtInfos = picker.getCacheSrtInfosInRange(
                    TimeHelper.getTimeBeforeOneMinute(seekTime),
                    TimeHelper.getTimeAfterOneMinute(seekTime));

            for (int i = 0; i < cachedSrtInfos.size(); i++)
            {
                if (cachedSrtInfos.get(i).getFromTime().toString()
                        .compareTo(seekTime) >= 0)
                {
                    DataHolder.setCurrentSrtIndex(i);
                    break;
                }
            }
        }
        // System.out.println("cachedSrtInfos:" + cachedSrtInfos);
        DataHolder.appendData(curFile, cachedSrtInfos);
        try
        {
            DataHolder.product(picker.getSrtInfos());
        }
        catch (SrtException e)
        {
            e.printStackTrace();
        }
        initTimeLineArr(DataHolder.getAllSrtInfos());

        System.out.println(BasicFileUtil.getFileName(curFile) + "字幕结果数:"
                + DataHolder.srtInfoMap.get(curFile).size());
        System.out
                .println("总耗时:" + basicRunTimeUtil.getCurrentRunMilliSecond());
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
        SrtTimeArr srtTimeArr = new SrtTimeArr();
        srtTimeArr.setSrtFile(curFile);
        srtTimeArr.setLeftTimelineArr(leftTimelineArr);
        srtTimeArr.setRightTimelineArr(rightTimelineArr);
        DataHolder.srtTimesMap.put(curFile, srtTimeArr);
    }

}
