package common;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyFileUtil
{
    public static List<File> getSortFiles(File[] listFiles)
    {
        List<File> fileList = Arrays.asList(listFiles);
        Collections.sort(fileList, new Comparator<File>()
        {
            @Override
            public int compare(File o1, File o2)
            {
                if (o1.isDirectory() && o2.isFile())
                {
                    return 1;
                }
                if (o1.isFile() && o2.isDirectory())
                {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return fileList;
    }

    public static String convertFileSize(long size)
    {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb)
        {
            return String.format("%.1f GB", (float) size / gb);
        }
        else if (size >= mb)
        {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        }
        else if (size >= kb)
        {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        }
        else
        {
            return String.format("%d B", size);
        }
    }
}
