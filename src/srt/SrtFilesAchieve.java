package srt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.ex.SrtException;
import common.uihelper.MyAppParams;
import common.utils.MyFileUtil;
import common.utils.PinYinUtil;
import common.utils.TextFormatUtil;

/**
 * 字幕文件获取专用类,同时兼顾图片路径获取
 * 
 * @author cpr216
 * 
 */
public class SrtFilesAchieve
{

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
                // 对文件按文件名排序
                List<File> fileList = MyFileUtil.getSortFiles(listFiles);
                List<String> srtList = new ArrayList<String>();
                int j = 0;
                for (File f2 : fileList)
                {
                    if (SrtTextHelper.isSrtfile(f2))
                    {
                        srtFilePathes.put(String.format(DELTA_UNIQUE, i, j),
                                f2.getAbsolutePath());
                        srtList.add(getFileName(TextFormatUtil
                                .getFileNameNoExtend(f2.getName())));
                        j++;
                    }
                }
                srightArr[i++] = srtList.toArray(new String[srtList.size()]);
            }

        }
        return srightArr;
    }

    private static String getFileName(String fileName)
    {
        float accumLen = 0;
        for (int i = 0; i < fileName.length(); i++)
        {
            if (PinYinUtil.isChinese(fileName.charAt(i)))
            {
                accumLen += 1;
            }
            else
            {
                accumLen += 0.5;
            }
            if (accumLen >= FILE_NAME_MAXLEN)
            {
                return BasicStringUtil.subString(fileName, 0, i + 1);
            }
        }
        return fileName;
    }

    private static List<File> getFolderFiles()
    {
        tvFolders = new ArrayList<File>();
        File srtFolderFile = new File(MyAppParams.SRT_FOLDER);
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
        if (srtFilePathes.size() == 0)
        {
            getDirsFiles();
        }
        String target = srtFilePathes.get(String.format(DELTA_UNIQUE, dIndex,
                fIndex));
        return target == null ? "" : target;
    }

    /**
     * 获取图片文件路径
     * 
     * @param srtFile
     * @return
     * @throws SrtException
     */
    public static String getThumbPicPath(String srtFile) throws SrtException
    {
        String filePath = MyAppParams.THUMB_PICFOLDER
                + srtFile.replace(MyAppParams.SRT_FOLDER, "");
        int i = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, i);

        File picFolder = new File(filePath);
        if (picFolder.exists())
        {
            if (srtFile.endsWith(".lrc"))
            {
                // 如果有单词图片,则打开单词图片,否则打开剧集图片
                final String wordPic = getWordPic(filePath);
                if (BasicStringUtil.isNotNullString(wordPic))
                {
                    return wordPic;
                }
            }
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

    private static String getWordPic(String filePath) throws SrtException
    {
        SrtInfo current = DataHolder.getCurrent();
        String pic1 = filePath + File.separator
                + current.getChs().replace(" ", "") + ".pic";
        System.out.println("getWordPic: " + pic1);
        if (BasicFileUtil.isExistFile(pic1))
        {
            return pic1;
        }
        String pic2 = filePath + File.separator
                + current.getChs().replace(" ", "") + ".gif";
        System.out.println("getWordPic: " + pic2);
        if (BasicFileUtil.isExistFile(pic2))
        {
            return pic2;
        }
        return "";
    }
}
