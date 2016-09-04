package common.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import common.uihelper.MyAppParams;

public class SysInit
{
	static Activity context;

	public static void init(Activity context2)
	{
		context = context2;

		MyAppParams.getInstance().setPackageName(context2.getPackageName());
		MyAppParams.getInstance().setResources(context2.getResources());
		MyAppParams.getInstance().setAppPath(context2.getFilesDir().getParent());
		MyAppParams.setScreenWidth(BasicPhoneUtil.getScreenWidth(context2));
		MyAppParams.setScreenHeight(BasicPhoneUtil.getScreenHeight(context2));

		if (isFirstRun())
		{
			createDbAndFullData();
		}
	}

	private static void createDbAndFullData()
	{
		boolean moveAssertDb = MoveDbUtil.moveAssertDb("srtlearn.db", "srtlearn.db", context);
		System.out.println("移动成功标志: " + moveAssertDb);
	}

	static String FIRST_RUN = "isSrtlearnFirstRun";

	private static boolean isFirstRun()
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("share", context.MODE_PRIVATE);
		boolean isFirstRun = sharedPreferences.getBoolean(FIRST_RUN, true);
		Editor editor = sharedPreferences.edit();
		if (isFirstRun)
		{
			Log.d("Sysinit", "第一次运行");
			editor.putBoolean(FIRST_RUN, false);
			editor.commit();
			return true;
		}
		else
		{
			Log.d("Sysinit", "不是第一次运行");
		}
		return false;
	}

}
