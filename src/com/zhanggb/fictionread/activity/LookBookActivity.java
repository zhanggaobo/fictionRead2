package com.zhanggb.fictionread.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.zhanggb.fictionread.R;
import com.zhanggb.fictionread.manager.BookManager;
import com.zhanggb.fictionread.manager.BookPageFactory;
import com.zhanggb.fictionread.manager.PreferencesManager;
import com.zhanggb.fictionread.manager.impl.BookManagerImpl;
import com.zhanggb.fictionread.model.Book;
import com.zhanggb.fictionread.model.Section;
import com.zhanggb.fictionread.util.DialogFactory;
import com.zhanggb.fictionread.util.Resource;
import com.zhanggb.fictionread.view.BookPageView;
import com.zhanggb.fictionread.view.BookSeekBarView;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-23
 * Time: 下午12:23
 * To change this template use File | Settings | File Templates.
 */
public class LookBookActivity extends Activity {

    private Context context;
    private BookPageView bookPageView;
    Bitmap mCurPageBitmap, mNextPageBitmap;
    Canvas mCurPageCanvas, mNextPageCanvas;
    BookPageFactory pageFactory;
    private int mWidth;
    private int mHeight;
    private Book book;
    private int bookId;
    private BookManager bookManager;
    private PreferencesManager preferencesManager;

    private TextView percentText;
    private SeekBar seekBar;
    private WindowManager windowManager;
    private int seekBarProgress = 0;
    private float density;
    private BookSeekBarView bookSeekBarView;
    private List<Section> sections;
    private float deviceDs = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        density = dm.density;
        mWidth = display.getWidth();
        mHeight = display.getHeight();
        setupView();
    }

    private void setupView() {
        bookPageView = new BookPageView(this, mWidth, mHeight);
        setContentView(bookPageView);
        this.context = LookBookActivity.this;
        bookManager = new BookManagerImpl(context);
        preferencesManager = new PreferencesManager(context);

        mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        pageFactory = new BookPageFactory(context, mWidth, mHeight);
        Bitmap bitmap = null;
        int textBack = preferencesManager.getTextBack();
        switch (textBack) {
            case 0:
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.readbg1);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.readbg2);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.readbg3);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.readbg4);
                break;
            case 4:
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.readbg5);
                break;
            case 5:
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.readbg6);
                break;
        }

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) mWidth / bitmap.getWidth());
        float scaleHeight = ((float) mHeight / bitmap.getHeight());
        matrix.postScale(scaleWidth, scaleHeight);
        pageFactory.setBgBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW") && intent.getType().equals("text/plain")) {
                String file = intent.getData().getPath();
                if (file.startsWith("file://")) {
                    file = file.substring(7);
                }
                book = new Book();
                book.setFile(file);
                book.setName(file.substring(file.lastIndexOf("/") + 1));
                book.setBeginRead(0);
                book.setPercent("0%");
                book.setLastTime(System.currentTimeMillis());
                bookId = (int) bookManager.insertBook(book);
            } else {
                bookId = intent.getIntExtra(Resource.BOOK_ID, 0);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookId != 0) {
            book = bookManager.findBookById(bookId);
        }
        if (book == null) {
            finish();
        }
        openBook();
    }

    @Override
    protected void onPause() {
        super.onPause();
        book.setBeginRead(pageFactory.getM_mbBufBegin());
        book.setPercent(pageFactory.getPercent());
        book.setLastTime(System.currentTimeMillis());
        book.setId(bookId);
        bookManager.updateBook(book.getId(), book);
    }

    private void createSeekBarDialog() {
        bookSeekBarView = new BookSeekBarView(context, new BookSeekBarView.BackCodeListener() {
            @Override
            public void execute() {
                windowManager.removeView(bookSeekBarView);
                windowManager = null;
            }
        });
        percentText = bookSeekBarView.getPercentText();
        seekBar = bookSeekBarView.getSeekBar();
        seekBar.setMax(100);
        Button btnCancel = bookSeekBarView.getBtnCancel();
        Button btnOk = bookSeekBarView.getBtnOk();
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        seekBar.setProgress(pageFactory.getIntPercent());
        seekBarProgress = pageFactory.getIntPercent();
        percentText.setText(pageFactory.getPercent());
        windowManager = getWindowManager();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        layoutParams.height = (int) density * 120;
        if (density == 1.5) {
            layoutParams.height = (int) density * 200;
        }
        layoutParams.width = mWidth;
        layoutParams.x = 0;
        layoutParams.y = mHeight - layoutParams.height;
        windowManager.addView(bookSeekBarView, layoutParams);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(bookSeekBarView);
                windowManager = null;
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(bookSeekBarView);
                windowManager = null;
                book.setBeginRead(pageFactory.getM_mbBufBegin(seekBarProgress));
                book.setPercent(pageFactory.getPercent());
                book.setLastTime(System.currentTimeMillis());
                book.setId(bookId);
                bookManager.updateBook(book.getId(), book);
                setupView();
                onResume();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && windowManager != null) {
            windowManager.removeView(bookSeekBarView);
            windowManager = null;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "目录");
        menu.add(0, 2, 1, "跳转");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            DialogFactory.createProgressDialog(context, "正在查看目录", new DialogFactory.ProgressDialogCallback() {
                @Override
                public void onShow(ProgressDialog dialog) {
                    sections = bookManager.findSections(book.getFile());
                    if (sections == null || sections.size() == 0) {
                        sections = pageFactory.findSectionDirectory(book.getFile());
                        bookManager.insertSections(sections, book.getFile());
                    }
                }

                @Override
                public void onDismiss(ProgressDialog dialog) {
                    createDirectoryDialog(sections);
                }

                @Override
                public void onCancel(ProgressDialog dialog) {
                }

                @Override
                public void onException(ProgressDialog dialog, Exception exception) {
                }
            }).show();
        } else if (item.getItemId() == 2) {
            createSeekBarDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDirectoryDialog(List<Section> sections) {

        View view = LayoutInflater.from(context).inflate(R.layout.book_section_directory_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.section_list);
        ListSectionAdapter adapter = new ListSectionAdapter(sections);
        listView.setAdapter(adapter);

        final Dialog loginDialog = new Dialog(this, R.style.dialog);
        loginDialog.setContentView(view);
        Window window = loginDialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.shape_round_corner);
        WindowManager.LayoutParams layoutParams = loginDialog.getWindow().getAttributes();
        if (density == 3) {
            layoutParams.width = getPx(320);
            layoutParams.height = getPx(300);
        } else if (density == 2) {
            layoutParams.width = getPx(325);
            layoutParams.height = getPx(300);
        } else {
            layoutParams.width = getPx(300);
            layoutParams.height = getPx(300);
        }
        window.setAttributes(layoutParams);
        loginDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Section section = (Section) view.getTag();
                if (section != null) {
                    book.setBeginRead(section.getCurrent());
                    book.setPercent(pageFactory.getPercent());
                    book.setLastTime(System.currentTimeMillis());
                    book.setId(bookId);
                    bookManager.updateBook(book.getId(), book);
                    setupView();
                    onResume();
                    try {
                        loginDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ListSectionAdapter extends BaseAdapter {

        List<Section> sections;

        ListSectionAdapter(List<Section> sections) {
            this.sections = sections;
        }

        @Override
        public int getCount() {
            return sections.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View parentView;
            if (view != null) {
                parentView = view;
            } else {
                parentView = LayoutInflater.from(context).inflate(R.layout.list_item_text, null);
            }
            TextView textView = (TextView) parentView.findViewById(R.id.text);
            textView.setText(sections.get(i).getName());
            parentView.setTag(sections.get(i));
            return parentView;
        }
    }

    //SeekBar的监听器
    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                seekBarProgress = i;
            }
            percentText.setText(seekBarProgress + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void openBook() {
        try {
            pageFactory.openBook(book.getBeginRead(), book.getFile());
            pageFactory.onDraw(mCurPageCanvas);
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(this, "电子书不存在!", Toast.LENGTH_SHORT).show();
        }

        bookPageView.setBitmaps(mCurPageBitmap, mCurPageBitmap);

        bookPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                boolean ret = false;
                if (v == bookPageView) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        bookPageView.abortAnimation();
                        bookPageView.calcCornerXY(e.getX(), e.getY());

                        pageFactory.onDraw(mCurPageCanvas);
                        if (bookPageView.DragToRight()) {
                            try {
                                pageFactory.prePage();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            if (pageFactory.isFirstPage()) {
                                return false;
                            }
                            pageFactory.onDraw(mNextPageCanvas);
                        } else {
                            try {
                                pageFactory.nextPage();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            if (pageFactory.isLastPage()) {
                                return false;
                            }
                            pageFactory.onDraw(mNextPageCanvas);
                        }
                        bookPageView.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                    }
                    ret = bookPageView.doTouchEvent(e);
                    return ret;
                }
                return false;
            }
        });
    }

    protected int getPx(int dp) {
        if (-1 == deviceDs) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            deviceDs = dm.density;
        }
        return (int) (dp * deviceDs);
    }

}