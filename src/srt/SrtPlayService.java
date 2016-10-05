package srt;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import srt.ex.SrtErrCode;
import srt.ex.SrtException;

import com.wnc.srtlearn.modules.srt.Favoritable;
import com.wnc.srtlearn.modules.srt.FavoriteMgr;
import com.wnc.srtlearn.modules.srt.SrtVoiceHelper;
import com.wnc.srtlearn.setting.SrtSetting;
import com.wnc.srtlearn.ui.SBaseLearnActivity;
import common.app.ToastUtil;

public class SrtPlayService implements Favoritable
{
	private PlayThread playThread;
	private boolean replayCtrl = false;// 复读模式
	public boolean autoPlayNextCtrl = true;// 如果播放过程出异常,就不能单靠系统设置的值控制自动播放下一个了,
	public SBaseLearnActivity sBaseLearnActivity;
	private int beginReplayIndex = -1;
	private int endReplayIndex = -1;

	// 两个音频间的播放延迟
	final int VOICE_PLAY_DELAY = 200;

	public SrtPlayService(SBaseLearnActivity sBaseLearnActivity)
	{
		this.sBaseLearnActivity = sBaseLearnActivity;
	}

	public void favorite() throws SrtException
	{
		List<SrtInfo> currentPlaySrtInfos = getCurrentPlaySrtInfos();
		FavoriteMgr favoriteMgr = new FavoriteMgr(this, sBaseLearnActivity);
		if (favoriteMgr.save(currentPlaySrtInfos))
		{
			ToastUtil.showLongToast(sBaseLearnActivity, "收藏成功!");
		}
		else
		{
			ToastUtil.showLongToast(sBaseLearnActivity, "收藏失败!");
		}
	}

	public SrtInfo getSrtInfo(SRT_VIEW_TYPE view_type) throws SrtException
	{
		SrtInfo srt = null;
		switch (view_type)
		{
		case VIEW_FIRST:
			srt = DataHolder.getFirst();
			break;
		case VIEW_LAST:
			srt = DataHolder.getLast();
			break;
		case VIEW_LEFT:
			srt = DataHolder.getPre();
			break;
		case VIEW_RIGHT:
			srt = DataHolder.getNext();
			break;
		case VIEW_CURRENT:
			srt = DataHolder.getCurrent();
			break;
		}
		return srt;
	}

	public String getPleyProgress()
	{
		final List<SrtInfo> list = DataHolder.srtInfoMap.get(getCurFile());
		if (list == null)
		{
			return "";
		}
		return "(" + (getCurIndex() + 1) + "/" + list.size() + ")";
	}

	public int getCurIndex()
	{
		return DataHolder.getCurrentSrtIndex();
	}

	public String getCurFile()
	{
		return DataHolder.getFileKey();
	}

	public void showNewSrtFile(String srtFile) throws SrtException
	{
		this.setReplayCtrl(false);
		this.setReplayIndex(-1, -1);
		System.out.println("srtFile:" + srtFile);
		sBaseLearnActivity.stopSrtPlay();
		DataHolder.switchFile(srtFile);
		if (!DataHolder.srtInfoMap.containsKey(srtFile))
		{
			new DataParseThread(getCurFile()).start();
			while (DataHolder.getAllSrtInfos() == null || DataHolder.getAllSrtInfos().size() == 0)
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
			sBaseLearnActivity.play(getSrtInfo(SRT_VIEW_TYPE.VIEW_CURRENT));
		}
		else
		{
			sBaseLearnActivity.play(getSrtInfo(SRT_VIEW_TYPE.VIEW_CURRENT));
		}
	}

	/**
	 * 指定一个时间点,开始搜索字幕
	 * 
	 * @param srtFile
	 * @param seekTimeStr
	 * @throws SrtException
	 */
	public void seekSrtFile(String srtFile, String seekTimeStr) throws SrtException
	{
		this.setReplayCtrl(false);
		this.setReplayIndex(-1, -1);
		System.out.println("srtFile:" + srtFile);
		sBaseLearnActivity.stopSrtPlay();
		DataHolder.switchFile(srtFile);
		if (!DataHolder.srtInfoMap.containsKey(srtFile))
		{
			new DataParseThread(getCurFile(), seekTimeStr).start();
			while (DataHolder.getAllSrtInfos() == null || DataHolder.getAllSrtInfos().size() == 0)
			{
				System.out.println("wait");
				try
				{
					TimeUnit.MILLISECONDS.sleep(10);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			sBaseLearnActivity.play(getSrtInfo(SRT_VIEW_TYPE.VIEW_CURRENT));
		}
		else
		{
			SrtInfo curPlaySrtInfo = null;
			for (int i = 0; i < DataHolder.getAllSrtInfos().size(); i++)
			{
				SrtInfo srtInfo = DataHolder.getAllSrtInfos().get(i);
				if (srtInfo.getFromTime().toString().compareTo(seekTimeStr) >= 0)
				{
					curPlaySrtInfo = srtInfo;
					DataHolder.setCurrentSrtIndex(i);
					break;
				}
			}
			if (curPlaySrtInfo == null)
			{
				throw new SrtException(SrtErrCode.SRT_OUTOF_RANGE);
			}
			else
			{
				sBaseLearnActivity.play(curPlaySrtInfo);
			}
		}
	}

	/**
	 * 控制切换是否复读,快捷设置仅复读本句
	 */
	public void switchReplayModel()
	{
		this.setReplayCtrl(isReplayCtrl() ? false : true);
		if (isReplayCtrl())
		{
			setReplayIndex(getCurIndex(), getCurIndex());
		}
		ToastUtil.showShortToast(sBaseLearnActivity, isReplayCtrl() ? "复读" : "不复读");
	}

	public void stopReplayModel()
	{
		this.setReplayCtrl(false);
		setReplayIndex(-1, -1);
	}

	public void setReplayIndex(int bIndex, int eIndex)
	{
		if (bIndex > eIndex)
		{
			ToastUtil.showLongToast(sBaseLearnActivity, "结束时间不能小于开始时间!");
		}
		else
		{
			setBeginReplayIndex(bIndex);
			setEndReplayIndex(eIndex);
		}
	}

	/**
	 * 检查复读模式是否失效:在复读的时候,如果翻页的范围超出了复读范围
	 * <p>
	 * 注意不能通过取反来表示复读有效
	 * 
	 * @return
	 */
	public boolean isReplayInvalid()
	{
		return isReplayCtrl() && (getCurIndex() < getBeginReplayIndex() || getCurIndex() > getEndReplayIndex());
	};

	/**
	 * 复读模式是否在进行中
	 * 
	 * @return
	 */
	public boolean isReplayRunning()
	{
		return isReplayCtrl() && (getCurIndex() >= getBeginReplayIndex() && getCurIndex() <= getEndReplayIndex());
	};

	public void playSrt()
	{
		// 停止原有的播放线程,播放新字幕
		stopSrt();
		// 每次播放,先设置自动播放控制为true
		autoPlayNextCtrl = true;
		playThread = new PlayThread(this);
		playThread.start();
	}

	public void stopSrt()
	{
		SrtVoiceHelper.stop();
		if (playThread != null)
		{
			playThread.threadRunning = false;
			playThread = null;
		}
		autoPlayNextCtrl = false;
	}

	public boolean isAutoPlayModel()
	{
		return autoPlayNextCtrl && SrtSetting.isAutoPlayNext();
	}

	public boolean isRunning()
	{
		return playThread != null;
	}

	public boolean isReplayCtrl()
	{
		return replayCtrl;
	}

	public void setReplayCtrl(boolean replayCtrl)
	{
		this.replayCtrl = replayCtrl;
	}

	public int getEndReplayIndex()
	{
		return endReplayIndex;
	}

	public void setEndReplayIndex(int endReplayIndex)
	{
		this.endReplayIndex = endReplayIndex;
	}

	public int getBeginReplayIndex()
	{
		return beginReplayIndex;
	}

	public void setBeginReplayIndex(int beginReplayIndex)
	{
		this.beginReplayIndex = beginReplayIndex;
	}

	public List<SrtInfo> getCurrentPlaySrtInfos() throws SrtException
	{
		if (isReplayRunning())
		{
			return DataHolder.getSrtInfos(beginReplayIndex, endReplayIndex);
		}
		else
		{
			return Arrays.asList(DataHolder.getCurrent());
		}
	}

	public String getFavTag()
	{
		String tag = "tag<";
		if (isReplayRunning())
		{
			tag += "replay";
		}
		else
		{
			tag += "normal";
		}

		tag += ">";
		return tag;
	}
}
