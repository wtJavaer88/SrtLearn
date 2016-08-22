package app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtil
{
    static SharedPreferences sharedPreferences;

    final public static void init(Activity context2)
    {
        sharedPreferences = context2.getSharedPreferences("share",
                context2.MODE_PRIVATE);
    }

    public static void changeValue(String key, String value)
    {
        if (value.equals(getShareDataByKey(key, "")))
        {
            return;
        }
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getShareDataByKey(String key, String defaultValue)
    {
        return sharedPreferences.getString(key, defaultValue);
    }
}
