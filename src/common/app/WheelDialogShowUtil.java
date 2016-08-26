package common.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import srt.DataHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cn.org.octopus.wheelview.widget.ArrayWheelAdapter;
import cn.org.octopus.wheelview.widget.OnWheelChangedListener;
import cn.org.octopus.wheelview.widget.WheelView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;
import common.uihelper.AfterWheelChooseListener;
import common.utils.DateTimeSelectArrUtil;
import common.utils.MyWheelBean;
import common.utils.TextFormatUtil;

public class WheelDialogShowUtil
{
    public static void showCurrDateTimeDialog(final Context context,
            String datetime, final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        final List<String[]> arrList = new ArrayList<String[]>();
        arrList.add(DateTimeSelectArrUtil.getYears());
        arrList.add(DateTimeSelectArrUtil.getMonths());
        arrList.add(DateTimeSelectArrUtil.getDays());
        arrList.add(DateTimeSelectArrUtil.getHours());
        arrList.add(DateTimeSelectArrUtil.getMinutes());
        arrList.add(DateTimeSelectArrUtil.getSeconds());

        LinearLayout llContent = new LinearLayout(context);
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        final List<WheelView> wheelviews = new ArrayList<WheelView>();

        for (int i = 0; i < 6; i++)
        {
            WheelView wheelview = new WheelView(context);
            wheelview.setVisibleItems(7);
            if (i > 0)
            {
                wheelview.setCyclic(true);
            }
            else
            {
                wheelview.setCyclic(false);
            }
            String[] data = arrList.get(i);
            wheelview.setAdapter(new ArrayWheelAdapter<String>(data));

            wheelview.setTextSize(40);
            llContent.addView(wheelview, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
            wheelviews.add(wheelview);
        }
        // 为日期的改变设置监听器
        setDateChangeListener(context, arrList, wheelviews);

        setDefaultValue(wheelviews, arrList, datetime);

        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(getFormatDateTimeStr(
                                wheelviews, arrList));
                        dialog.dismiss();
                    }

                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

    public static void showCurrDateDialog(Context context,
            final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        final List<String[]> arrList = new ArrayList<String[]>();
        arrList.add(DateTimeSelectArrUtil.getYears());
        arrList.add(DateTimeSelectArrUtil.getMonths());
        arrList.add(DateTimeSelectArrUtil.getDays());

        LinearLayout llContent = new LinearLayout(context);
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        final List<WheelView> wheelviews = new ArrayList<WheelView>();

        for (int i = 0; i < 3; i++)
        {
            WheelView wheelview = new WheelView(context);
            wheelview.setVisibleItems(7);
            if (i > 0)
            {
                wheelview.setCyclic(true);
            }
            else
            {
                wheelview.setCyclic(false);
            }
            String[] data = arrList.get(i);
            wheelview.setAdapter(new ArrayWheelAdapter<String>(data));

            wheelview.setTextSize(40);
            llContent.addView(wheelview, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
            wheelviews.add(wheelview);
        }
        setDefaultValue(wheelviews, arrList,
                BasicDateUtil.getCurrentDateTimeString());

        // 为日期的改变设置监听器
        setDateChangeListener(context, arrList, wheelviews);
        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(getFormatDateStr(wheelviews,
                                arrList));
                        dialog.dismiss();
                    }

                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

    public static void showTimeSelectDialog(Context context,
            int[] defaultIndex, final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();

        final List<String[]> arrList = new ArrayList<String[]>();
        arrList.add(DateTimeSelectArrUtil.getHours());
        arrList.add(DateTimeSelectArrUtil.getMinutes());
        arrList.add(DateTimeSelectArrUtil.getSeconds());

        LinearLayout llContent = new LinearLayout(context);
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        final List<WheelView> wheelviews = new ArrayList<WheelView>();

        for (int i = 0; i < 3; i++)
        {
            WheelView wheelview = new WheelView(context);
            wheelview.setVisibleItems(7);
            if (i > 0)
            {
                wheelview.setCyclic(true);
            }
            else
            {
                wheelview.setCyclic(false);
            }
            String[] data = arrList.get(i);
            wheelview.setAdapter(new ArrayWheelAdapter<String>(data));
            wheelview.setCurrentItem(defaultIndex[i]);
            wheelview.setTextSize(40);
            llContent.addView(wheelview, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
            wheelviews.add(wheelview);
        }

        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(wheelviews.get(0)
                                .getCurrentItem(), wheelviews.get(1)
                                .getCurrentItem(), wheelviews.get(2)
                                .getCurrentItem());
                        dialog.dismiss();
                    }

                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

    public static void showSrtDialog(final Context context, String[] leftArr,
            String[] rightArr, int beginIndex, int endIndex,
            final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        final List<String[]> arrList = new ArrayList<String[]>();
        arrList.add(DateTimeSelectArrUtil.getYears());
        arrList.add(DateTimeSelectArrUtil.getMonths());
        arrList.add(DateTimeSelectArrUtil.getDays());

        LinearLayout llContent = new LinearLayout(context);
        llContent.setOrientation(LinearLayout.HORIZONTAL);
        final WheelView wheelview1 = new WheelView(context);
        wheelview1.setVisibleItems(7);
        wheelview1.setCyclic(true);
        wheelview1.setAdapter(new ArrayWheelAdapter<String>(leftArr));
        wheelview1.setTextSize(40);
        wheelview1.setCurrentItem(beginIndex);
        llContent.addView(wheelview1, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

        final WheelView wheelview2 = new WheelView(context);
        wheelview2.setVisibleItems(7);
        wheelview2.setCyclic(true);
        wheelview2.setAdapter(new ArrayWheelAdapter<String>(rightArr));
        wheelview2.setTextSize(40);
        wheelview2.setCurrentItem(endIndex);
        llContent.addView(wheelview2, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        // 为字幕的改变设置监听器
        OnWheelChangedListener onWheelChangedListener = new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                ToastUtil.showShortToast(context, DataHolder
                        .getCurrentSrtInfos().get(newValue).getChs());
            }
        };
        wheelview1.addChangingListener(onWheelChangedListener);
        wheelview2.addChangingListener(onWheelChangedListener);
        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(wheelview1.getCurrentItem(),
                                wheelview2.getCurrentItem());
                        dialog.dismiss();
                    }

                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

    public static void showHanziDialog(final Context context, String dialogStr,
            int beginIndex, int endIndex,
            final AfterWheelChooseListener listener)
    {
        String[] hanziArr = new String[dialogStr.length()];
        for (int i = 0; i < dialogStr.length(); i++)
        {
            hanziArr[i] = dialogStr.substring(i, i + 1);
        }
        System.out.println(Arrays.toString(hanziArr));
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        final List<String[]> arrList = new ArrayList<String[]>();
        arrList.add(DateTimeSelectArrUtil.getYears());
        arrList.add(DateTimeSelectArrUtil.getMonths());
        arrList.add(DateTimeSelectArrUtil.getDays());

        LinearLayout llContent = new LinearLayout(context);
        llContent.setOrientation(LinearLayout.HORIZONTAL);
        final WheelView wheelview1 = new WheelView(context);
        wheelview1.setVisibleItems(7);
        wheelview1.setCyclic(false);
        wheelview1.setAdapter(new ArrayWheelAdapter<String>(hanziArr));
        wheelview1.setTextSize(40);
        wheelview1.setCurrentItem(beginIndex);
        llContent.addView(wheelview1, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

        final WheelView wheelview2 = new WheelView(context);
        wheelview2.setVisibleItems(7);
        wheelview2.setCyclic(false);
        wheelview2.setAdapter(new ArrayWheelAdapter<String>(hanziArr));
        wheelview2.setTextSize(40);
        wheelview2.setCurrentItem(endIndex);
        llContent.addView(wheelview2, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(wheelview1.getCurrentItem(),
                                wheelview2.getCurrentItem());
                        dialog.dismiss();
                    }

                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

    private static void setDateChangeListener(final Context context,
            final List<String[]> arrList, final List<WheelView> wheelviews)
    {
        OnWheelChangedListener onWheelChangedListener = new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                String formatDateStr = getFormatDateStr(wheelviews, arrList);
                if (BasicDateUtil.isDateFormatTimeString(formatDateStr,
                        "yyyy-MM-dd"))
                {
                    ToastUtil.showShortToast(
                            context,
                            BasicDateUtil.getGBDateWeekString(
                                    formatDateStr.replace("-", "")).replace(
                                    "七", "天"));
                }
                else
                {
                    ToastUtil.showLongToast(context, "不存在这一天:" + formatDateStr);
                }
            }
        };
        wheelviews.get(0).addChangingListener(onWheelChangedListener);
        wheelviews.get(1).addChangingListener(onWheelChangedListener);
        wheelviews.get(2).addChangingListener(onWheelChangedListener);
    }

    private static String getFormatDateStr(List<WheelView> wheelviews,
            List<String[]> arrList)
    {

        String[] values = new String[arrList.size()];
        computeDateTimeValues(wheelviews, arrList, values);
        return TextFormatUtil.addSeparatorToDay(values[0] + values[1]
                + values[2]);
    }

    private static String getFormatDateTimeStr(List<WheelView> wheelviews,
            List<String[]> arrList)
    {

        String[] values = new String[arrList.size()];
        computeDateTimeValues(wheelviews, arrList, values);
        return TextFormatUtil.addSeparatorToDay(values[0] + values[1]
                + values[2])
                + " "
                + TextFormatUtil.addSeparatorToTime(values[3] + values[4]
                        + values[5]);
    }

    private static void computeDateTimeValues(List<WheelView> wheelviews,
            List<String[]> arrList, String[] values)
    {
        for (int i = 0; i < arrList.size(); i++)
        {
            String value = PatternUtil.getFirstPattern(
                    arrList.get(i)[wheelviews.get(i).getCurrentItem()], "\\d+");
            value = BasicStringUtil.fillLeftStringNotruncate(value, 2, "0");
            values[i] = value;
        }
    }

    private static void setDefaultValue(List<WheelView> wheelviews,
            List<String[]> arrList, String datetime)
    {
        Date date = TextFormatUtil.getFormatedDate(datetime);
        int month = date.getMonth();
        int day = date.getDate();
        int hour = date.getHours();
        int minute = date.getMinutes();
        int second = date.getSeconds();

        for (int i = 0; i < arrList.size(); i++)
        {
            switch (i)
            {
            case 0:
                wheelviews.get(i).setCurrentItem(1);
                break;
            case 1:
                wheelviews.get(i).setCurrentItem(month);
                break;
            case 2:
                wheelviews.get(i).setCurrentItem(day - 1);
                break;
            case 3:
                wheelviews.get(i).setCurrentItem(hour);
                break;
            case 4:
                wheelviews.get(i).setCurrentItem(minute);
                break;
            case 5:
                wheelviews.get(i).setCurrentItem(second);
                break;
            }
        }
    }

    /**
     * 
     * @param context
     * @param title
     * @param leftList
     *            理论上可以去掉, 但是要靠这个对元素排序
     * @param rightMap
     * 
     * @param listener
     */
    public static void showRelativeDialog(Context context, String title,
            final List<MyWheelBean> leftList,
            final Map<MyWheelBean, List<? extends MyWheelBean>> rightMap,
            final AfterWheelChooseListener listener)
    {
        showRelativeDialog(context, title, leftList, rightMap, 0, 0, listener);
    }

    /**
     * 
     * @param context
     * @param title
     * @param leftList
     *            理论上可以去掉, 但是要靠这个对元素排序
     * @param rightMap
     * 
     * @param listener
     */
    public static void showRelativeDialog(Context context, String title,
            final List<MyWheelBean> leftList,
            final Map<MyWheelBean, List<? extends MyWheelBean>> rightMap,
            int leftId, int rightId, final AfterWheelChooseListener listener)
    {

        final String[] leftArr = new String[leftList.size()];
        final String[][] rightArr = new String[leftList.size()][];

        int i = 0;
        for (MyWheelBean mybean : leftList)
        {
            leftArr[i] = mybean.getName();
            if (rightMap.containsKey(mybean))
            {
                int j = 0;
                String[] arr = new String[rightMap.get(mybean).size()];
                for (MyWheelBean bean : rightMap.get(mybean))
                {
                    arr[j] = bean.getName();
                    j++;

                }
                rightArr[i] = arr;
            }
            i++;
        }

        showRelativeDialog(context, title, leftArr, rightArr, leftId, rightId,
                listener);
    }

    public static void showRelativeDialog(Context context, String title,
            final String[] left, final String[][] right, int defaultLeftId,
            int defaultRightId, final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        // 创建对话框内容, 创建一个 LinearLayout
        LinearLayout llContent = new LinearLayout(context);
        // 将创建的 LinearLayout 设置成横向的
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        // 创建 WheelView 组件
        final WheelView wheelLeft = new WheelView(context);
        // 设置 WheelView 组件最多显示元素
        wheelLeft.setVisibleItems(5);
        // 设置 WheelView 元素是否循环滚动
        wheelLeft.setCyclic(false);
        // 设置 WheelView 适配器
        wheelLeft.setAdapter(new ArrayWheelAdapter<String>(left));
        wheelLeft.setTextSize(60);

        // 设置右侧的 WheelView
        final WheelView wheelRight = new WheelView(context);
        // 设置右侧 WheelView 显示个数
        wheelRight.setVisibleItems(5);
        // 设置右侧 WheelView 元素是否循环滚动
        wheelRight.setCyclic(false);
        // 设置右侧 WheelView 的元素适配器
        wheelRight.setAdapter(new ArrayWheelAdapter<String>(right[0]));
        wheelRight.setTextSize(60);
        // 设置 LinearLayout 的布局参数
        LinearLayout.LayoutParams paramsLeft = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 4);
        paramsLeft.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 6);
        paramsRight.gravity = Gravity.RIGHT;
        // 将 WheelView 对象放到左侧 LinearLayout 中
        llContent.addView(wheelLeft, paramsLeft);
        // 将 WheelView 对象放到 右侧 LinearLayout 中
        llContent.addView(wheelRight, paramsRight);

        wheelLeft.setCurrentItem(defaultLeftId);
        wheelRight.setAdapter(new ArrayWheelAdapter<String>(
                right[defaultLeftId]));
        if (defaultLeftId == 0 && defaultRightId == -1)
        {
            System.out.println(left[0].length() / 2 + "  " + left[0].length());
            wheelRight.setCurrentItem(left[0].length() / 2);
        }
        else
        {
            wheelRight.setCurrentItem(defaultRightId);
        }
        System.out
                .println("选中项: l: " + defaultLeftId + " r: " + defaultRightId);

        // 为左侧的 WheelView 设置条目改变监听器
        wheelLeft.addChangingListener(new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
                // 设置右侧的 WheelView 的适配器
                wheelRight.setAdapter(new ArrayWheelAdapter<String>(
                        right[newValue]));
                wheelRight.setCurrentItem(right[newValue].length / 2);
            }
        });
        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(wheelLeft.getCurrentItem(),
                                wheelRight.getCurrentItem());
                        System.out.println("回调: l: "
                                + wheelLeft.getCurrentItem() + " r: "
                                + wheelRight.getCurrentItem());
                        dialog.dismiss();
                    }
                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

}
