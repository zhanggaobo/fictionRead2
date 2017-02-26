package com.zhanggb.fictionread.view;

import android.content.Context;
import android.util.AttributeSet;
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
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
public class HeadView extends RelativeLayout {

    private Context context;
    private TextView textView;
    private ImageView leftImage, rightImage;

    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setupView();
    }

    private void setupView() {
        View view = LayoutInflater.from(context).inflate(R.layout.head_view, null);
        textView = (TextView) view.findViewById(R.id.head_view_text);
        leftImage = (ImageView) view.findViewById(R.id.head_view_left);
        rightImage = (ImageView) view.findViewById(R.id.head_view_right);
        addView(view);
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getLeftImage() {
        return leftImage;
    }

    public ImageView getRightImage() {
        return rightImage;
    }
}
