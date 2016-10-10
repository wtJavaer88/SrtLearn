package srt;

import java.util.List;

import srt.ex.SrtParseErrorException;
import srt.picker.DBPicker;
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
    private int state = 0;// 0是初始状态, 不进行判断
    String curFile;
    String seekTime;
    int curPage = 0;
    final int COUNTS_PER_PAGE = 100;
    Picker picker;

    public int getRunState()
    {
        return state;
    }

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
        try
        {
            if (BasicStringUtil.isNullString(seekTime))
            {
                if (picker instanceof DBPicker)
                {
                    cachedSrtInfos = picker.getSrtInfos();
                }
                else
                {
                    cachedSrtInfos = picker.get10CacheSrtInfos(null);
                }
            }
            else
            {
                if (picker instanceof DBPicker)
                {
                    cachedSrtInfos = picker.getSrtInfos();
                }
                else
                {
                    cachedSrtInfos = picker.getCacheSrtInfosInRange(
                            TimeHelper.getTimeBeforeOneMinute(seekTime),
                            TimeHelper.getTimeAfterOneMinute(seekTime));
                }
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
            if (cachedSrtInfos != null && cachedSrtInfos.size() > 0)
            {
                DataHolder.appendData(curFile, cachedSrtInfos);
                if (picker instanceof DBPicker)
                {
                    state = 2;// 数据全部提取完成
                }
                else
                {
                    state = 1;// 已经有数据可供操作
                    DataHolder.product(picker.getSrtInfos());
                    state = 2;// 数据全部提取完成
                }

                initTimeLineArr(DataHolder.getAllSrtInfos());
            }
            else
            {
                state = -1;// 缓存获取失败
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            state = -2;// 出现异常
        }

        System.out.println(BasicFileUtil.getFileName(curFile) + "字幕结果数:"
                + DataHolder.srtInfoMap.get(curFile).size());
        System.out
                .println("总耗时:" + basicRunTimeUtil.getCurrentRunMilliSecond());
    }

    public void initTimeLineArr(List<SrtInfo> currentSrtInfos)
    {
        int size = currentSrtInfos.size();
        String[] leftTimelineArr = new String[size];
        String[] rightTimelineArr = new String[size];
        for (int i = 0; i < size; i++)
        {
            SrtInfo srtInfo = currentSrtInfos.get(i);
            leftTimelineArr[i] = srtInfo.getFromTime().toString();
            rightTimelineArr[i] = srtInfo.getToTime().toString();
        }
        SrtTimeArr srtTimeArr = new SrtTimeArr();
        srtTimeArr.setSrtFile(curFile);
        srtTimeArr.setLeftTimelineArr(leftTimelineArr);
        srtTimeArr.setRightTimelineArr(rightTimelineArr);
        DataHolder.srtTimesMap.put(curFile, srtTimeArr);
    }

}
