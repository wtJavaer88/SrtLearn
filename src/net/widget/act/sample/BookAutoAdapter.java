package net.widget.act.sample;

import java.util.List;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.ViewHolder;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wnc.srtlearn.R;

public class BookAutoAdapter extends net.widget.act.abs.MyActAdapter
{
    public BookAutoAdapter(Context context, List<AutoCompletable> items,
            int maxMatch)
    {
        super(context, items, maxMatch);
    }

    class BookViewHolder extends ViewHolder
    {
        TextView id, name, author, price;
    }

    @Override
    protected View getView2(int position, View convertView, ViewGroup parent)
    {
        BookViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new BookViewHolder();
            convertView = View.inflate(context, R.layout.act_item, null);
            viewHolder.id = (TextView) convertView.findViewById(R.id.id_book);
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.name_book);
            viewHolder.author = (TextView) convertView
                    .findViewById(R.id.author_book);
            viewHolder.price = (TextView) convertView
                    .findViewById(R.id.price_book);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (BookViewHolder) convertView.getTag();
        }
        BookViewHolder bookviewHolder = viewHolder;
        Book book = (Book) autoItems.get(position);
        bookviewHolder.id.setText(book.getId() + "");
        bookviewHolder.name.setText(book.getName());
        bookviewHolder.author.setText(book.getAuthor());
        bookviewHolder.price.setText(book.getPrice() + "");
        return convertView;
    }
}
