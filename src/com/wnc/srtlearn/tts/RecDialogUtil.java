package com.wnc.srtlearn.tts;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.wnc.srtlearn.RecActivity;

public class RecDialogUtil
{
    // �Ի������
    static DialogRecognitionListener mRecognitionListener;

    public static BaiduASRDigitalDialog getDialog(RecActivity recActivity,
            final CallBack callback)
    {
        Bundle params = new Bundle();
        // ����ע��ٶȿ���ƽ̨�õ���ֵ API_KEY,SECRET_KEY
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
                BdTextToSpeech.API_KEY);
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
                BdTextToSpeech.SECRET_KEY);
        // ���öԻ���ģʽ
        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
                Config.DIALOG_THEME);
        // ���������½��Ի���
        BaiduASRDigitalDialog mDialog = new BaiduASRDigitalDialog(recActivity,
                params);

        if (mRecognitionListener == null)
        {
            mRecognitionListener = new DialogRecognitionListener()
            {
                @Override
                public void onResults(Bundle results)
                {
                    ArrayList<String> rs = results != null ? results
                            .getStringArrayList(RESULTS_RECOGNITION) : null;
                    if (rs != null && rs.size() > 0)
                    {
                        Log.i("RecResult", rs.get(0));
                        callback.listenComplete(rs.get(0));
                    }
                }
            };
        }
        // ���öԻ���ļ���
        mDialog.setDialogRecognitionListener(mRecognitionListener);
        // �Ի�������
        mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP,
                Config.CURRENT_PROP);
        mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                Config.getCurrentLanguage());
        mDialog.getParams().putBoolean(
                BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE,
                Config.PLAY_START_SOUND);
        mDialog.getParams().putBoolean(
                BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE,
                Config.PLAY_END_SOUND);
        mDialog.getParams().putBoolean(
                BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE,
                Config.DIALOG_TIPS_SOUND);

        return mDialog;
    }
}
