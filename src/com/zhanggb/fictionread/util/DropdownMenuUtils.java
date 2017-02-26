package com.zhanggb.fictionread.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.zhanggb.fictionread.manager.BookManager;
import com.zhanggb.fictionread.manager.impl.BookManagerImpl;
import com.zhanggb.fictionread.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-23
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class DropdownMenuUtils {

    public enum MenuType {
        BOOK_LIST //联系人头部
    }

    private View view;
    private Context context;
    private Book book;
    private BookManager bookManager;

    public DropdownMenuUtils(Activity activity, View view, Book book) {
        this.context = activity;
        this.view = view;
        this.book = book;
        bookManager = new BookManagerImpl(context);
    }

    public Items findDropdownMenuByType(MenuType menuType) {
        if (menuType == MenuType.BOOK_LIST) {
            return createBookListMenu();
        }
        return null;
    }

    private Items createBookListMenu() {
        final Items dropdownMenu = new Items();
        dropdownMenu.add(new Items.Item("从书架上移除", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookManager.deleteBookById(book.getId());
                dropdownMenu.getCallBackListener().execute(null);
            }
        }));
        dropdownMenu.add(new Items.Item("删除该文件", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookManager.deleteBookById(book.getId());
                bookManager.deleteBookFileByFile(book.getFile());
                dropdownMenu.getCallBackListener().execute(null);
            }
        }));
        return dropdownMenu;
    }


    public static class Items {
        private Closure callBackListener;
        private List<Item> itemList;

        public void setCallBackListener(Closure callBackListener) {
            this.callBackListener = callBackListener;
        }

        public Closure getCallBackListener() {
            return callBackListener;
        }

        public Items() {
            itemList = new ArrayList<Item>();
        }

        public Item get(int i) {
            return itemList.get(i);
        }

        public void add(Item item) {
            itemList.add(item);
        }

        public void remove(int i) {
            itemList.remove(i);
        }

        public void removeLastItem() {
            if (itemList.size() > 0) {
                itemList.remove(getLength() - 1);
            }
        }

        public List<Item> getItemList() {
            return this.itemList;
        }

        public int getLength() {
            return this.itemList.size();
        }

        public String[] getItemNames() {
            List<String> array = new ArrayList<String>();
            for (Item quickTag : getItemList()) {
                array.add(quickTag.name);
            }
            return ArrayUtils.toArray(array, String.class);
        }

        public static class Item {
            public int drawable;
            public String name;
            public View.OnClickListener onClickListener;

            public Item(String name, View.OnClickListener onClickListener) {
                this.name = name;
                this.onClickListener = onClickListener;
            }

            public Item(int drawable, String name, View.OnClickListener clickListener) {
                this.drawable = drawable;
                this.name = name;
                this.onClickListener = clickListener;
            }


            public void onClick(View view) {
                if (this.onClickListener != null) {
                    this.onClickListener.onClick(view);
                }
            }
        }
    }
}
