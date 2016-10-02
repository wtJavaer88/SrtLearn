package common.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.widget.sdufe.thea.guo.GalleryModel;
import net.widget.sdufe.thea.guo.PictureAdapter;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;

import com.wnc.basic.BasicStringUtil;
import com.wnc.srtlearn.R;
import common.uihelper.AfterGalleryChooseListener;
import common.utils.PinYinUtil;
import common.utils.TextFormatUtil;

public class GalleryUtil
{
	public static Gallery getSrtSeasonGallery(Activity activity, Gallery gallery, final List<GalleryModel> list, final AfterGalleryChooseListener listener)
	{
		if (list == null || list.isEmpty())
		{
			throw new RuntimeException("给定的Gallery参数为空");
		}
		final PictureAdapter adapter = new PictureAdapter(activity, list);

		gallery.setAdapter(adapter);
		if (list.size() >= 2)
		{
			gallery.setSelection(2);
		}
		else
			gallery.setSelection(0);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				adapter.setSelectItem(position);
				if (listener != null)
				{
					listener.afterGalleryChoose(list.get(position).getId());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		return gallery;
	}

	public static Gallery getBihuaGallery(Activity activity, Gallery gallery, String dialog, final AfterGalleryChooseListener listener)
	{
		if (BasicStringUtil.isNullString(dialog))
		{
			throw new RuntimeException("请给一段合法的汉字!");
		}
		final List<GalleryModel> list = new ArrayList<GalleryModel>();
		for (int i = 0; i < dialog.length(); i++)
		{
			final char ch = dialog.charAt(i);
			if (TextFormatUtil.isChineseChar(ch))
			{
				list.add(new GalleryModel(R.drawable.bihua_select, "" + ch));
			}
		}
		final PictureAdapter adapter = new PictureAdapter(activity, list);

		gallery.setAdapter(adapter);
		gallery.setSelection(list.size() / 2);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				adapter.setSelectItem(position);
				if (listener != null)
				{
					listener.afterGalleryChoose(list.get(position).getText());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		return gallery;
	}

	public static Gallery getYuyinGallery(Activity activity, Gallery gallery, String dialog, final AfterGalleryChooseListener listener)
	{
		if (BasicStringUtil.isNullString(dialog))
		{
			throw new RuntimeException("请给一段合法的汉字!");
		}
		final List<GalleryModel> list = new ArrayList<GalleryModel>();
		for (int i = 0; i < dialog.length(); i++)
		{
			final char ch = dialog.charAt(i);
			if (TextFormatUtil.isChineseChar(ch))
			{
				list.add(new GalleryModel(R.drawable.yuyin_select, "" + ch));
			}
		}
		final PictureAdapter adapter = new PictureAdapter(activity, list);

		gallery.setAdapter(adapter);
		gallery.setSelection(list.size() / 2);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				adapter.setSelectItem(position);
				if (listener != null)
				{
					listener.afterGalleryChoose(list.get(position).getText());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		return gallery;
	}

	public static Gallery getPinyinGallery(Activity activity, Gallery gallery, String dialog, final AfterGalleryChooseListener listener)
	{
		if (BasicStringUtil.isNullString(dialog))
		{
			throw new RuntimeException("请给一段合法的汉字!");
		}
		final List<GalleryModel> list = new ArrayList<GalleryModel>();
		final List<String> pys = new ArrayList<String>();
		final Map<Integer, Integer> indexs = new HashMap<Integer, Integer>();
		int newIndex = 0;
		for (int i = 0; i < dialog.length(); i++)
		{
			final char ch = dialog.charAt(i);
			if (TextFormatUtil.isChineseChar(ch))
			{
				list.add(new GalleryModel(R.drawable.py_select, "" + ch + "(" + PinYinUtil.getSinglePinYin(ch) + ")"));
				pys.add(PinYinUtil.getSinglePinYin(ch));
				indexs.put(newIndex, i);
				newIndex++;
			}
		}
		final PictureAdapter adapter = new PictureAdapter(activity, list);

		gallery.setAdapter(adapter);
		gallery.setSelection(list.size() / 2);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				adapter.setSelectItem(position);
				if (listener != null)
				{
					if (position > -1 && position < indexs.size())
					{
						listener.afterGalleryChoose(indexs.get(position) + ":" + pys.get(position));
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		return gallery;
	}

	public static Gallery getPinyinGallery(Activity activity, Gallery gallery, String dialog, final String[] pinyin, final AfterGalleryChooseListener listener)
	{
		if (BasicStringUtil.isNullString(dialog) || pinyin == null)
		{
			throw new RuntimeException("请给一段合法的汉字和拼音!");
		}
		if (dialog.length() != pinyin.length)
		{
			throw new RuntimeException("汉字和拼音长度不匹配!");
		}
		final List<GalleryModel> list = new ArrayList<GalleryModel>();
		final Map<Integer, Integer> indexs = new HashMap<Integer, Integer>();
		int newIndex = 0;
		for (int i = 0; i < dialog.length(); i++)
		{
			final char ch = dialog.charAt(i);
			if (TextFormatUtil.isChineseChar(ch))
			{
				list.add(new GalleryModel(R.drawable.py_select, "" + ch + "(" + pinyin[i] + ")"));
				indexs.put(newIndex, i);
				newIndex++;
			}
		}
		final PictureAdapter adapter = new PictureAdapter(activity, list);

		gallery.setAdapter(adapter);
		gallery.setSelection(list.size() / 2);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				adapter.setSelectItem(position);
				if (listener != null)
				{
					if (position > -1 && position < indexs.size())
					{
						listener.afterGalleryChoose(indexs.get(position) + "");
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		return gallery;
	}
}
