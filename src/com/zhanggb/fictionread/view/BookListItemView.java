package com.zhanggb.fictionread.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhanggb.fictionread.R;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-23
 * Time: 上午9:40
 * To change this template use File | Settings | File Templates.
 */
public class BookListItemView extends LinearLayout {

    private View parent;
    private ImageView leftImage;
    private TextView bookName;
    private TextView bookPercent;
    private TextView bookLastTime;
    private TextView bookAuthor;


    public BookListItemView(Context context) {
        super(context);
        parent = LayoutInflater.from(context).inflate(R.layout.book_list_item_view, null);
        setupView();
        addView(parent);
    }

    private void setupView() {
        leftImage = (ImageView) parent.findViewById(R.id.book_item_image);
        bookName = (TextView) parent.findViewById(R.id.book_item_name);
        bookPercent = (TextView) parent.findViewById(R.id.book_item_percent);
        bookAuthor = (TextView) parent.findViewById(R.id.book_item_author);
        bookLastTime = (TextView) parent.findViewById(R.id.book_item_last_time);
    }

    public ImageView getLeftImage() {
        return leftImage;
    }

    public TextView getBookName() {
        return bookName;
    }

    public TextView getBookPercent() {
        return bookPercent;
    }

    public TextView getBookLastTime() {
        return bookLastTime;
    }

    public TextView getBookAuthor() {
        return bookAuthor;
    }
}
