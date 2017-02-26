package com.zhanggb.fictionread.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhanggb.fictionread.R;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryItemView extends RelativeLayout {

    private Context context;
    private ImageView leftView;
    private TextView nameView;

    public DirectoryItemView(Context context) {
        super(context);
        this.context = context;
        setupView();
    }

    private void setupView() {
        View view = LayoutInflater.from(context).inflate(R.layout.directory_item_view, null);
        leftView = (ImageView) view.findViewById(R.id.directory_item_left_image);
        nameView = (TextView) view.findViewById(R.id.directory_item_name);
        addView(view);
    }

    public ImageView getLeftView() {
        return leftView;
    }

    public TextView getNameView() {
        return nameView;
    }

    public View getView() {
        return this;
    }

}
