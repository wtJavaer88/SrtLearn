package com.wnc.srtlearn.ui.handler;

import srt.DataHolder;
import srt.SRT_VIEW_TYPE;
import android.os.Handler;

import com.wnc.srtlearn.ui.SrtActivity;
import common.uihelper.gesture.EmptyFlingPoint;

public class AutoPlayHandler extends Handler
{
	SrtActivity activity;

	public AutoPlayHandler(SrtActivity activity)
	{
		this.activity = activity;
	}

	@Override
	public void handleMessage(android.os.Message msg)
	{
		if (msg.what == 1)
		{
			if (activity.getSrtPlayService().isReplayInvalid())
			{
				activity.getSrtPlayService().stopReplayModel();
			}
			if (activity.getSrtPlayService().isReplayCtrl())
			{
				// 复读结束时,回到复读开始的地方继续复读
				if (activity.getSrtPlayService().getCurIndex() == activity.getSrtPlayService().getEndReplayIndex())
				{
					DataHolder.setCurrentSrtIndex(activity.getSrtPlayService().getBeginReplayIndex());
					activity.getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_CURRENT);
				}
				else
				{
					// 复读模式下,也会自动播放下一条,但是临时性的
					activity.doRight(new EmptyFlingPoint(), new EmptyFlingPoint());
				}
			}
			else if (activity.getSrtPlayService().isAutoPlayModel())
			{
				// 在自动播放模式下,播放下一条
				activity.doRight(new EmptyFlingPoint(), new EmptyFlingPoint());
			}
			else
			{
				activity.stopSrtPlay();
			}
		}
		else
		{
			activity.stopSrtPlay();
		}
	}
}
