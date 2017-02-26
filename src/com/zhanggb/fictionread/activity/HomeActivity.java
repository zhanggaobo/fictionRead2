package com.zhanggb.fictionread.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.feiwo.view.FwBannerManager;
import com.feiwo.view.FwRecevieAdListener;
import com.zhanggb.fictionread.R;
import com.zhanggb.fictionread.manager.BookManager;
import com.zhanggb.fictionread.manager.PreferencesManager;
import com.zhanggb.fictionread.manager.impl.BookManagerImpl;
import com.zhanggb.fictionread.model.Book;
import com.zhanggb.fictionread.util.*;
import com.zhanggb.fictionread.view.BookListItemView;
import com.zhanggb.fictionread.view.HeadView;

import java.util.Date;
import java.util.List;

public class HomeActivity extends Activity {

    private Context context;
    private HeadView headView;
    private ListView listView;
    private BookManager bookManager;
    private PreferencesManager preferencesManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_activity);
        this.context = HomeActivity.this;
        FwBannerManager.init(context, Resource.APPKEY_FEIWO);
        bookManager = new BookManagerImpl(context);
        listView = (ListView) findViewById(R.id.home_book_list);
        preferencesManager = new PreferencesManager(context);
        setupHeadView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupListView();
        initBanner();
    }

    private void initBanner() {
        //启动方式2  能够获取banner的宽度 高度
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.feiwo_banner_ll);
        FwBannerManager.setParentView(layout,
                new FwRecevieAdListener() {

                    @Override
                    public void onSucessedRecevieAd(int arg0, int arg1) {
                        // TODO Auto-generated method stub
//                        Toast.makeText(context, "width = " + arg0 + " height" + arg1, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailedToRecevieAd() {
                        // TODO Auto-generated method stub
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "字体大小");
        menu.add(0, 2, 2, "阅读背景");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int whichSize = preferencesManager.getTextSize();
        if (whichSize == 15) {
            whichSize = 0;
        } else if (whichSize == 25) {
            whichSize = 2;
        } else {
            whichSize = 1;
        }
        int whichBack = preferencesManager.getTextBack();
        if (item.getItemId() == 1) {
            Dialog mDialog = new AlertDialog.Builder(context)
                    .setTitle("字体大小")
                    .setSingleChoiceItems(new String[]{"小", "中", "大"}, whichSize, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    preferencesManager.setTextSize(15);
                                    break;
                                case 1:
                                    preferencesManager.setTextSize(20);
                                    break;
                                case 2:
                                    preferencesManager.setTextSize(25);
                                    break;
                            }
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            mDialog.show();

        } else if (item.getItemId() == 2) {
            Dialog mDialog = new AlertDialog.Builder(context)
                    .setTitle("阅读背景")
                    .setSingleChoiceItems(new String[]{"羊皮卷", "羽毛", "黑色水滴", "纸质", "木纹", "西方花纹"}, whichBack, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    preferencesManager.setTextBack(0);
                                    break;
                                case 1:
                                    preferencesManager.setTextBack(1);
                                    break;
                                case 2:
                                    preferencesManager.setTextBack(2);
                                    break;
                                case 3:
                                    preferencesManager.setTextBack(3);
                                    break;
                                case 4:
                                    preferencesManager.setTextBack(4);
                                    break;
                                case 5:
                                    preferencesManager.setTextBack(5);
                                    break;
                            }
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            mDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupHeadView() {
        headView = (HeadView) findViewById(R.id.head_view);
        headView.getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ChooseBooksActivity.class));
            }
        });
    }

    private void setupListView() {
        List<Book> books = bookManager.findBooks();
        BookListAdapter adapter = new BookListAdapter(books);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book book = (Book) view.getTag();
                Intent intent = new Intent(context, LookBookActivity.class);
                intent.putExtra(Resource.BOOK_ID, book.getId());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                Book book = (Book) view.getTag();
                final DropdownMenuUtils.Items dropdownMenu = new DropdownMenuUtils(HomeActivity.this, view, book).
                        findDropdownMenuByType(DropdownMenuUtils.MenuType.BOOK_LIST);
                DialogFactory.createChooseDialog(context, null, dropdownMenu.getItemNames(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DropdownMenuUtils.Items.Item quickTag = dropdownMenu.get(i);
                                quickTag.onClick(view);
                            }
                        }).show();
                dropdownMenu.setCallBackListener(new Closure<String>() {
                    @Override
                    public void execute(String input) {
                        setupListView();
                    }
                });
                return true;
            }
        });
    }

    class BookListAdapter extends BaseAdapter {

        List<Book> books;

        BookListAdapter(List<Book> books) {
            this.books = books;
        }

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int i) {
            return books.get(i);
        }

        @Override
        public long getItemId(int i) {
            return books.get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BookListItemView itemView;
            Book book = books.get(i);
            if (view != null) {
                itemView = (BookListItemView) view;
            } else {
                itemView = new BookListItemView(context);
            }
            itemView.getBookName().setText(book.getName());
            itemView.getBookAuthor().setText(book.getAuthor());
            itemView.getBookPercent().setText(book.getPercent());
            itemView.getBookLastTime().setText(DateUtils.format(context, new Date(book.getLastTime())));
            itemView.setTag(book);
            return itemView;
        }
    }
}
