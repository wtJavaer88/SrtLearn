package common.app;

import android.app.Activity;
import android.util.Log;

import common.uihelper.MyAppParams;

public class SysInit
{
    static Activity context;

    public static void init(Activity context2)
    {
        context = context2;
        SharedPreferenceUtil.init(context);

        MyAppParams.getInstance().setPackageName(context2.getPackageName());
        MyAppParams.getInstance().setResources(context2.getResources());
        MyAppParams.getInstance()
                .setAppPath(context2.getFilesDir().getParent());
        MyAppParams.setScreenWidth(BasicPhoneUtil.getScreenWidth(context2));
        MyAppParams.setScreenHeight(BasicPhoneUtil.getScreenHeight(context2));

        if (isFirstRun())
        {
            createDbAndFullData();
        }
    }

    private static void createDbAndFullData()
    {
        boolean moveAssertDb = MoveDbUtil.moveAssertDb("srtlearn.db",
                "srtlearn.db", context);
        System.out.println("移动成功标志: " + moveAssertDb);
    }

    static String FIRST_RUN = "isSrtlearnFirstRun";

    private static boolean isFirstRun()
    {
        boolean isFirstRun = SharedPreferenceUtil.getShareDataByKey(FIRST_RUN,
                true);
        if (isFirstRun)
        {
            Log.d("Sysinit", "第一次运行");
            SharedPreferenceUtil.changeValue(FIRST_RUN, false);
            return true;
        }
        else
        {
            Log.d("Sysinit", "不是第一次运行");
        }
        return false;
    }

}
