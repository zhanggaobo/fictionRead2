package com.zhanggb.fictionread.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.zhanggb.fictionread.R;
import com.zhanggb.fictionread.manager.BookManager;
import com.zhanggb.fictionread.manager.FileManager;
import com.zhanggb.fictionread.manager.impl.BookManagerImpl;
import com.zhanggb.fictionread.manager.impl.FileManagerImpl;
import com.zhanggb.fictionread.model.Book;
import com.zhanggb.fictionread.model.Directory;
import com.zhanggb.fictionread.view.DirectoryItemView;
import com.zhanggb.fictionread.view.HeadView;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public class ChooseBooksActivity extends Activity {

    private Context context;

    private FileManager fileManager = new FileManagerImpl();

    private HeadView headView;
    private TextView directorView;
    private ListView listView;

    private File file;
    private BookManager bookManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_books_activity);
        this.context = ChooseBooksActivity.this;
        bookManager = new BookManagerImpl(context);
        directorView = (TextView) findViewById(R.id.choose_books_directory);
        listView = (ListView) findViewById(R.id.choose_item_list);
        file = Environment.getExternalStorageDirectory();
        setupHeadView();
        setupDirectoryView();
        setupListView();
    }

    private void setupDirectoryView() {
        directorView.setText("路径:" + file.toString());
    }

    private void setupHeadView() {
        headView = (HeadView) findViewById(R.id.head_view);
        headView.getTextView().setText("打开书籍");
        headView.getLeftImage().setVisibility(View.VISIBLE);
        headView.getRightImage().setVisibility(View.GONE);
        headView.getLeftImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupListView() {
        DirectoryItemView directoryItemView = new DirectoryItemView(context);
        directoryItemView.getLeftView().setImageResource(R.drawable.icon_folder);
        directoryItemView.getNameView().setText("上一层");
        listView.addHeaderView(directoryItemView);
        refreshListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    File f = preDirectory();
                    if (f != null) {
                        file = f;
                        setupDirectoryView();
                        refreshListView();
                    } else {
                        return;
                    }
                } else {
                    Directory directory = (Directory) view.getTag();
                    if (directory.getType() == Directory.Type.DIRECTORY) {
                        file = directory.getFile();
                        setupDirectoryView();
                        refreshListView();
                    } else if (directory.getName().endsWith(".txt")) {
                        Book book = new Book();
                        book.setName(directory.getName());
                        book.setFile(directory.getFile().toString());
                        book.setPercent("0%");
                        book.setLastTime(System.currentTimeMillis());
                        bookManager.insertBook(book);
                        Toast.makeText(context, "添加书籍成功!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(context, "不支持的格式！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void refreshListView() {
        List<Directory> directories = fileManager.findDirectoryByPath(file);
        ListViewAdapter listViewAdapter = new ListViewAdapter(directories);
        listView.setAdapter(listViewAdapter);
    }

    private File preDirectory() {
        return file.getParentFile();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            File f = preDirectory();
            if (f != null) {
                file = f;
                setupDirectoryView();
                refreshListView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class ListViewAdapter extends BaseAdapter {

        List<Directory> directories;

        ListViewAdapter(List<Directory> list) {
            directories = list;
        }

        @Override
        public int getCount() {
            return directories.size();
        }

        @Override
        public Object getItem(int i) {
            return directories.get(i);
        }

        @Override
        public long getItemId(int i) {
            return directories.get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return createChooseItemView(i, view);
        }

        private View createChooseItemView(int i, View view) {
            DirectoryItemView itemView;
            if (view == null) {
                itemView = new DirectoryItemView(context);
            } else {
                itemView = (DirectoryItemView) view;
            }
            Directory d = directories.get(i);
            if (d.getType() == Directory.Type.DIRECTORY) {
                itemView.getLeftView().setImageResource(R.drawable.icon_folder);
            } else if (d.getType() == Directory.Type.FILE) {
                itemView.getLeftView().setImageResource(R.drawable.icon_umd);
            }
            itemView.getNameView().setText(d.getName());
            itemView.setTag(d);
            return itemView.getView();

        }
    }
}