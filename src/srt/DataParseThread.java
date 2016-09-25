package srt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import srt.ex.SrtException;
import srt.ex.SrtParseErrorException;
import srt.picker.Picker;
import srt.picker.PickerFactory;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicRunTimeUtil;

public class DataParseThread extends Thread
{
	String curFile;
	int curPage = 0;
	final int COUNTS_PER_PAGE = 100;
	Picker picker;
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
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

	@Override
	public void run()
	{
		final BasicRunTimeUtil basicRunTimeUtil = new BasicRunTimeUtil("DataParseThread");
		basicRunTimeUtil.beginRun();

		DataHolder.appendData(curFile, picker.get10CacheSrtInfos("00:21:24.030"));

		while (isNotComplete())
		{
			executor.execute(new Task(curPage));
			curPage++;
		}
		executor.shutdown();
		while (executor.getActiveCount() > 0)
		{
			try
			{
				TimeUnit.MILLISECONDS.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			DataHolder.product(allInfos);
			initTimeLineArr(DataHolder.getAllSrtInfos());
		}
		catch (SrtException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(BasicFileUtil.getFileName(curFile) + "字幕结果数:" + DataHolder.srtInfoMap.get(curFile).size());
		System.out.println("总耗时:" + basicRunTimeUtil.getCurrentRunMilliSecond());
	}

	private boolean isNotComplete()
	{
		return picker.getSrtLineCounts() - curPage * COUNTS_PER_PAGE > 0;
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

	List<SrtInfo> allInfos = new ArrayList<SrtInfo>();

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
			allInfos.addAll(picker.getSrtInfos(COUNTS_PER_PAGE * curPage, COUNTS_PER_PAGE * (curPage + 1)));
		}

	}
}
