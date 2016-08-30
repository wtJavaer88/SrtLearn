package srt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Environment;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import common.utils.MyFileUtil;
import common.utils.TextFormatUtil;

/**
 * 字幕文件获取专用类,同时兼顾图片路径获取
 * 
 * @author cpr216
 * 
 */
public class SrtFilesAchieve
{
    private static final String SRT_FOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/srt/";
    final static String THUMB_PICFOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/srtpic/";
    private static Map<String, String> srtFilePathes = new HashMap<String, String>();
    private static List<File> tvFolders = null;
    static String[] sleftArr;
    static String[][] srightArr;
    // 文件夹名称最大位数
    private final static int FOLDER_NAME_MAXLEN = 16;
    // 文件名最大位数
    private final static int FILE_NAME_MAXLEN = 8;
    // 用来生成srtFilePathes里的key
    private final static String DELTA_UNIQUE = "D%dF%d";

    public static String[] getDirs()
    {
        if (sleftArr == null)
        {
            tvFolders = getFolderFiles();
            int tvs = tvFolders.size();
            sleftArr = new String[tvs];
            int i = 0;
            for (File folder : tvFolders)
            {
                sleftArr[i++] = BasicStringUtil.subString(folder.getName(), 0,
                        FOLDER_NAME_MAXLEN);
            }
        }

        return sleftArr;
    }

    public static String[][] getDirsFiles()
    {
        if (srightArr == null)
        {
            tvFolders = getFolderFiles();
            int tvs = tvFolders.size();
            srightArr = new String[tvs][];
            int i = 0;
            for (File folder : tvFolders)
            {
                File[] listFiles = folder.listFiles();
                List<File> fileList = MyFileUtil.getSortFiles(listFiles);
                List<String> srtList = new ArrayList<String>();
                int j = 0;
                for (File f2 : fileList)
                {
                    if (SrtTextHelper.isSrtfile(f2))
                    {
                        srtFilePathes.put(String.format(DELTA_UNIQUE, i, j),
                                f2.getAbsolutePath());
                        srtList.add(BasicStringUtil.subString(TextFormatUtil
                                .getFileNameNoExtend(f2.getName()), 0,
                                FILE_NAME_MAXLEN));
                        j++;
                    }
                }
                srightArr[i++] = srtList.toArray(new String[srtList.size()]);
            }

        }
        return srightArr;
    }

    private static List<File> getFolderFiles()
    {
        tvFolders = new ArrayList<File>();
        File srtFolderFile = new File(SRT_FOLDER);
        for (File f : MyFileUtil.getSortFiles(srtFolderFile.listFiles()))
        {
            if (f.isDirectory())
            {
                tvFolders.add(f);
            }
        }
        return tvFolders;
    }

    public static String getSrtFileByArrIndex(int dIndex, int fIndex)
    {
        return srtFilePathes.get(String.format(DELTA_UNIQUE, dIndex, fIndex));
    }

    /**
     * 获取图片文件路径
     * 
     * @param srtFile
     * @return
     */
    public static String getThumbPicPath(String srtFile)
    {
        String filePath = THUMB_PICFOLDER + srtFile.replace(SRT_FOLDER, "");
        int i = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, i);
        File picFolder = new File(filePath);
        if (picFolder.exists())
        {
            filePath = filePath + File.separator
                    + TextFormatUtil.getFileNameNoExtend(srtFile) + "_p1.pic";
            if (!BasicFileUtil.isExistFile(filePath))
            {
                if (picFolder.listFiles().length > 0)
                {
                    filePath = picFolder.listFiles()[0].getAbsolutePath();
                }
            }
        }
        else
        {
            filePath = "";
        }
        return filePath;
    }
}
