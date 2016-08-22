package uihelper;

import app.SharedPreferenceUtil;

public class Setting
{

    public static boolean budgetChanged = false;
    public static boolean restored = false;
    final static String DEFAULT_MEMBER = "王";
    final static String DEFAULT_EMAIL = "1@qq.com";
    final static String DEFAULT_BUDGET = "500";

    private final static String LAST_MEMBER = "M001";
    private final static String EMAIL = "M002";

    private final static String BACKUP_TIME_MODEL = "M0031";
    private final static String BACKUP_AUTO = "M0032";
    private final static String BACKUP_WAY = "M0033";

    private final static String WEEK_BUDGET = "M004";

    public static void setBACKUP_TIME_MODEL(String backup_time_model)
    {
        SharedPreferenceUtil.changeValue(BACKUP_TIME_MODEL, backup_time_model);
    }

    public static void setBACKUP_AUTO(String backup_auto)
    {
        SharedPreferenceUtil.changeValue(BACKUP_AUTO, backup_auto);
    }

    public static void setBACKUP_WAY(String backup_way)
    {
        SharedPreferenceUtil.changeValue(BACKUP_WAY, backup_way);
    }

    public static void setMember(String member)
    {
        SharedPreferenceUtil.changeValue(LAST_MEMBER, member);
    }

    public static void setEmail(String email)
    {
        SharedPreferenceUtil.changeValue(EMAIL, email);
    }

    public static void setBudget(String budget)
    {
        budgetChanged = true;
        SharedPreferenceUtil.changeValue(WEEK_BUDGET, budget);
    }

    public static String getLastMember()
    {
        return SharedPreferenceUtil.getShareDataByKey(LAST_MEMBER,
                DEFAULT_MEMBER);
    }

    public static String getEmail()
    {
        return SharedPreferenceUtil.getShareDataByKey(EMAIL, DEFAULT_EMAIL);
    }

    public static int getBudget()
    {
        return Integer.parseInt(SharedPreferenceUtil.getShareDataByKey(
                WEEK_BUDGET, DEFAULT_BUDGET));
    }

    public static String getBackupTimeModel()
    {
        return SharedPreferenceUtil.getShareDataByKey(BACKUP_TIME_MODEL, "每次");
    }

    public static boolean isBackupAuto()
    {
        return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(
                BACKUP_AUTO, "false"));
    }

    public static String getBackupWay()
    {
        return SharedPreferenceUtil.getShareDataByKey(BACKUP_WAY, "邮箱");
    }

}
