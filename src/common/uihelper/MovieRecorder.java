package common.uihelper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.view.SurfaceView;

public class MovieRecorder extends Thread
{
    private MediaRecorder mediarecorder;
    private boolean isRecording;
    SurfaceView surfaceView;
    final int VIDEO_WIDTH = 640;
    final int VIDEO_HEIGHT = 480;
    final String tmpVideoPath = MyAppParams.getInstance().getTmpVideoPath();

    public MovieRecorder(SurfaceView surfaceView)
    {
        this.surfaceView = surfaceView;
    }

    public boolean isRecording()
    {
        return this.isRecording;
    }

    public String getRecordedFile()
    {
        return lastFileName;
    }

    @Override
    public void run()
    {
        try
        {
            // 暂停100ms再自动运行
            Thread.sleep(100);
        }
        catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        // mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        // 设置视频文件输出的路径
        lastFileName = newFileName();
        mediarecorder.setOutputFile(lastFileName);
        try
        {

            // 准备录制
            mediarecorder.prepare();
            mediarecorder.setOnInfoListener(new OnInfoListener()
            {
                @Override
                public void onInfo(MediaRecorder arg0, int arg1, int arg2)
                {
                }
            });
            mediarecorder.setOnErrorListener(new OnErrorListener()
            {
                @Override
                public void onError(MediaRecorder arg0, int arg1, int arg2)
                {
                    System.out.println("Err function");
                }
            });
            // 开始录制
            mediarecorder.start();
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        isRecording = true;
        timeSize = 0;
        timer = new Timer();
        timer.schedule(new TimerTask()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                timeSize++;
            }
        }, 0, 1000);
    }

    Timer timer;
    int timeSize = 0;

    private String lastFileName;

    public void stopRecording()
    {
        if (mediarecorder != null)
        {
            // 停止
            mediarecorder.stop();
            mediarecorder.release();
            mediarecorder = null;
            timer.cancel();
        }
    }

    public String newFileName()
    {
        return tmpVideoPath + System.currentTimeMillis() + ".3gp";
    }

    public void release()
    {
        if (mediarecorder != null)
        {
            // 停止
            mediarecorder.stop();
            mediarecorder.release();
            mediarecorder = null;
        }
    }
}
