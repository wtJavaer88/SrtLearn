package uihelper;

import android.content.res.Resources;
import android.os.Environment;

import com.wnc.basic.BasicFileUtil;

public class MyAppParams
{
    private String packageName;
    private Resources resources;
    private String appPath;
    private String workPath = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/money/";
    private String localLogPath;
    private String backupDbPath;
    private String tmpPicPath;
    private String tmpVoicePath;
    private String tmpVideoPath;
    private String zipPath;

    private static int screenWidth;
    private static int screenHeight;

    private static MyAppParams singletonMyAppParams = new MyAppParams();

    private MyAppParams()
    {
        this.localLogPath = this.workPath + "log/";
        this.backupDbPath = this.workPath + "backupdb/";
        this.tmpPicPath = this.workPath + "tempimg/";

        this.tmpVoicePath = this.workPath + "tempamr/";
        this.tmpVideoPath = this.workPath + "tempMP4/";
        this.zipPath = this.workPath + "zip/";

        BasicFileUtil.makeDirectory(this.localLogPath);
        BasicFileUtil.makeDirectory(this.backupDbPath);

        BasicFileUtil.makeDirectory(this.tmpPicPath);
        BasicFileUtil.makeDirectory(this.tmpVoicePath);
        BasicFileUtil.makeDirectory(this.tmpVideoPath);

        BasicFileUtil.makeDirectory(this.zipPath);
    }

    public static MyAppParams getInstance()
    {
        return singletonMyAppParams;
    }

    public String getZipPath()
    {
        return this.zipPath;
    }

    public String getBackupDbPath()
    {
        return this.backupDbPath;
    }

    public static int getScreenWidth()
    {
        return screenWidth;
    }

    public static void setScreenWidth(int screenWidth)
    {
        MyAppParams.screenWidth = screenWidth;
    }

    public static int getScreenHeight()
    {
        return screenHeight;
    }

    public static void setScreenHeight(int screenHeight)
    {
        MyAppParams.screenHeight = screenHeight;
    }

    public void setPackageName(String name)
    {
        if (name == null)
        {
            return;
        }
        if (this.packageName == null)
        {
            this.packageName = name;
        }
    }

    public String getPackageName()
    {
        return this.packageName;
    }

    public void setAppPath(String path)
    {
        if (path == null)
        {
            return;
        }
        if (this.appPath == null)
        {
            this.appPath = path;
        }
    }

    public void setResources(Resources res)
    {
        if (res == null)
        {
            return;
        }
        if (this.resources == null)
        {
            this.resources = res;
        }
    }

    public Resources getResources()
    {
        return this.resources;
    }

    public String getWorkPath()
    {
        return this.workPath;
    }

    public String getLocalLogPath()
    {
        return this.localLogPath;
    }

    public String getTmpPicPath()
    {
        return this.tmpPicPath;
    }

    public String getTmpVoicePath()
    {
        return this.tmpVoicePath;
    }

    public String getAppPath()
    {
        return this.appPath;
    }

    public String getTmpVideoPath()
    {
        return this.tmpVideoPath;
    }
}
