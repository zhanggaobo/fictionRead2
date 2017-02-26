package com.zhanggb.fictionread.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.zhanggb.fictionread.R;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-1-13
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
public class BookSeekBarView extends LinearLayout {
    private Context context;
    private View view;
    private TextView percentText;
    private SeekBar seekBar;
    private Button btnCancel;
    private Button btnOk;
    private BackCodeListener backCodeListener;

    public BookSeekBarView(Context context, BackCodeListener backCodeListener) {
        super(context);
        this.context = context;
        this.backCodeListener = backCodeListener;
        setupView();
        addView(view, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }

    private void setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.book_seek_bar, null);
        percentText = (TextView) view.findViewById(R.id.book_seek_bar_percent);
        seekBar = (SeekBar) view.findViewById(R.id.book_seek_bar);
        btnCancel = (Button) view.findViewById(R.id.cancel);
        btnOk = (Button) view.findViewById(R.id.ok);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            backCodeListener.execute();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnOk() {
        return btnOk;
    }

    public View getView() {
        return view;
    }

    public TextView getPercentText() {
        return percentText;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public interface BackCodeListener {
        void execute();
    }
}
