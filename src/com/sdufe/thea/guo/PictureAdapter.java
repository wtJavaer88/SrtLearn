package com.sdufe.thea.guo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wnc.srtlearn.R;

public class PictureAdapter extends BaseAdapter
{

    private Context context;
    private int selectItem;
    private List<GalleryModel> list;

    public PictureAdapter(Context context, List<GalleryModel> list)
    {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        if (list != null)
        {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position)
    {
        if (list != null)
        {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void setSelectItem(int selectItem)
    {

        if (this.selectItem != selectItem)
        {
            this.selectItem = selectItem;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHold hold;

        if (convertView == null)
        {
            hold = new ViewHold();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.gallery_item, null);
            convertView.setTag(hold);
        }
        else
        {
            hold = (ViewHold) convertView.getTag();
        }
        hold.mImageView = (ImageView) convertView.findViewById(R.id.imageview);
        hold.mTextView = (TextView) convertView.findViewById(R.id.text);

        hold.mImageView.setImageResource(list.get(position).getImageView());
        hold.mTextView.setText(list.get(position).getText());
        if (selectItem == position)
        {
            hold.mImageView.setLayoutParams(new LinearLayout.LayoutParams(150,
                    180));
            hold.mTextView.setTextSize(20);
            hold.mTextView.setFocusable(true);
        }
        else
        {
            hold.mImageView.setLayoutParams(new LinearLayout.LayoutParams(120,
                    150));
            hold.mTextView.setTextSize(20);
            hold.mTextView.setFocusable(false);
        }
        return convertView;
    }

    static class ViewHold
    {
        public TextView mTextView;
        private ImageView mImageView;
    }

}
