package com.wnc.srtlearn.ui.handler;

import srt.DataHolder;
import srt.SrtPlayService;
import android.os.Handler;

import com.wnc.srtlearn.ui.SBaseLearnActivity;

public class AutoPlayHandler extends Handler
{
	SBaseLearnActivity activity;
	SrtPlayService srtPlayService;

	public AutoPlayHandler(SBaseLearnActivity activity)
	{
		this.activity = activity;
		srtPlayService = activity.getSrtPlayService();
	}

	@Override
	public void handleMessage(android.os.Message msg)
	{
		if (msg.what == 1)
		{

			if (srtPlayService.isReplayInvalid())
			{
				srtPlayService.stopReplayModel();
			}
			if (srtPlayService.isReplayCtrl())
			{
				// 复读结束时,回到复读开始的地方继续复读
				if (srtPlayService.getCurIndex() == srtPlayService.getEndReplayIndex())
				{
					DataHolder.setCurrentSrtIndex(srtPlayService.getBeginReplayIndex());
					activity.playCurrent();
				}
				else
				{
					// 复读模式下,也会自动播放下一条,但是临时性的
					activity.playNext();
				}
			}
			else if (srtPlayService.isAutoPlayModel())
			{
				// 在自动播放模式下,播放下一条
				activity.playNext();
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
