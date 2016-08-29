package com.wnc.srtlearn.bihua;

import android.content.Context;
import android.os.Environment;

import com.wnc.basic.BasicFileUtil;
import com.wnc.srtlearn.srt.DownPicTask;
import common.app.BasicPhoneUtil;
import common.app.ToastUtil;

public class SwfPlayMgr
{
    public final static String SWF_FOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/swf/";
    public final static String SWF_HTML = SWF_FOLDER + "swfplayer.htm";
    private final static String swfAPI = "http://zd.diyifanwen.com/Files/WordSwf/%s.swf";

    public static void reCreateHtml(String hanzi, Context context)
    {
        final String localSwfFile = SWF_FOLDER + hanzi + ".swf";
        if (BasicFileUtil.isExistFile(localSwfFile)
                && BasicFileUtil.getFileSize(localSwfFile) > 0)
        {
            writeSwfData(hanzi, true);
        }
        else
        {
            if (BasicPhoneUtil.isNetworkAvailable(context))
            {
                writeSwfData(hanzi, false);
                downLoadSwf(hanzi);
            }
            else
            {
                ToastUtil.showShortToast(context, "网络连接不可用!");
            }
        }
    }

    private static void downLoadSwf(String hanzi)
    {
        String destSave = SWF_FOLDER + hanzi + ".swf";
        String swfUrl = getSwfUrl(java.net.URLEncoder.encode(hanzi));
        new Thread(new DownPicTask(destSave, swfUrl)).start();
    }

    private static void writeSwfData(String hanzi, boolean isLocal)
    {
        StringBuilder accum = new StringBuilder(1024);
        String movie = isLocal ? hanzi + ".swf" : getSwfUrl(hanzi);
        accum.append("<object id=\"forfun\" classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"600\" height=\"600\" "
                + "codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\">\n");
        accum.append(" <param name=\"movie\" value=\"" + movie + "\">\n");
        accum.append("<param name=\"quality\" value=\"high\">\n");
        accum.append("<param name=\"bgcolor\" value=\"#F0F0F0\">\n");
        accum.append("<param name=\"menu\" value=\"false\">\n");
        accum.append("<param name=\"wmode\" value=\"opaque\">\n");
        accum.append("<param name=\"FlashVars\" value=\"\">\n");
        accum.append("<param name=\"allowScriptAccess\" value=\"sameDomain\">\n");
        accum.append("<embed id=\"forfunex\" src=\""
                + movie
                + "\" width=\"600\" height=\"600\" align=\"middle\" allowScriptAccess=\"sameDomain\" menu=\"false\""
                + " type=\"application/x-shockwave-flash\" pluginspage=\"http://www.adobe.com/go/getflashplayer\">\n");
        accum.append("</object>");
        BasicFileUtil.writeFileString(SWF_HTML, accum.toString(), "UTF-8",
                false);
    }

    private static String getSwfUrl(String hanzi)
    {
        return String.format(swfAPI, hanzi);
    }
}
