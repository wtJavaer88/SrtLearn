package com.wnc.srtlearn.modules.srt;

import com.wnc.basic.BasicFileUtil;
import common.utils.UrlPicDownloader;

public class DownPicTask implements Runnable
{
    String saveFilePath;
    String resUrl;

    public DownPicTask(String saveFilePath, String swfUrl)
    {
        this.saveFilePath = saveFilePath;
        this.resUrl = swfUrl;
    }

    @Override
    public void run()
    {
        int download = Integer.MIN_VALUE;
        try
        {
            download = UrlPicDownloader.download(resUrl, saveFilePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (download != BasicFileUtil.getFileSize(saveFilePath))
        {
            BasicFileUtil.deleteFile(saveFilePath);
        }
        else
        {

        }
    }
}