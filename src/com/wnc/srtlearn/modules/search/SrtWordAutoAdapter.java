package com.wnc.srtlearn.modules.search;

import java.util.List;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.ViewHolder;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wnc.srtlearn.R;

public class SrtWordAutoAdapter extends net.widget.act.abs.MyActAdapter
{
    public SrtWordAutoAdapter(Context context, List<AutoCompletable> items,
            int maxMatch)
    {
        super(context, items, maxMatch);
    }

    class BookViewHolder extends ViewHolder
    {
        TextView engTv;
    }

    @Override
    protected View getView2(int position, View convertView, ViewGroup parent)
    {
        BookViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new BookViewHolder();
            convertView = View
                    .inflate(context, R.layout.act_srtinfo_item, null);
            viewHolder.engTv = (TextView) convertView
                    .findViewById(R.id.tvSearchEng);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (BookViewHolder) convertView.getTag();
        }
        BookViewHolder bookviewHolder = viewHolder;
        ActSrtWord srtWord = (ActSrtWord) autoItems.get(position);
        bookviewHolder.engTv.setText(srtWord.getWord());
        System.out.println("srtWord.getWord():" + srtWord.getWord());
        return convertView;
    }
}
