package com.zhanggb.fictionread.manager.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.gson.Gson;
import com.zhanggb.fictionread.manager.BookManager;
import com.zhanggb.fictionread.model.Book;
import com.zhanggb.fictionread.model.Section;
import com.zhanggb.fictionread.sqlit.ReadSqliteOpenHelper;
import com.zhanggb.fictionread.sqlit.Schema;
import com.zhanggb.fictionread.util.Closure;
import com.zhanggb.fictionread.util.CursorTemplate;
import com.zhanggb.fictionread.util.CursorUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-22
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public class BookManagerImpl implements BookManager {

    // 正则表达式   按自己需要来更改
    private static final String patternStr = "第[\\d一二三四五六七八九十百千]+[章节集回]\\s*.{0,20}";
    // 换行符占两个字节
    private static final int breakLineSize = 2;

    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase database;

    private Context context;

    public BookManagerImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<Book> findBooks() {
        final List<Book> books = new ArrayList<Book>();
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        Cursor cursor = database.query(Schema.TABLE_NAME,
                new String[]{Schema._ID, Schema.NAME, Schema.AUTHOR, Schema.FILE, Schema.LAST_TIME, Schema.BEGIN_READ, Schema.PERCENT},
                null, null, null, null, null);
        CursorTemplate.each(cursor, new Closure<Cursor>() {
            @Override
            public void execute(Cursor input) {
                Book book = new Book();
                book.setId(CursorUtils.getInt(input, Schema._ID));
                book.setName(CursorUtils.getString(input, Schema.NAME));
                book.setAuthor(CursorUtils.getString(input, Schema.AUTHOR));
                book.setLastTime(CursorUtils.getLong(input, Schema.LAST_TIME));
                book.setFile(CursorUtils.getString(input, Schema.FILE));
                book.setPercent(CursorUtils.getString(input, Schema.PERCENT));
                book.setBeginRead(CursorUtils.getInt(input, Schema.BEGIN_READ));
                books.add(book);
            }
        });
        return books;
    }

    @Override
    public Book findBookById(int id) {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        Cursor cursor = database.query(Schema.TABLE_NAME,
                new String[]{Schema._ID, Schema.NAME, Schema.AUTHOR, Schema.FILE, Schema.LAST_TIME, Schema.BEGIN_READ, Schema.PERCENT},
                Schema._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        final Book book = new Book();
        CursorTemplate.one(cursor, new Closure<Cursor>() {
            @Override
            public void execute(Cursor input) {
                book.setId(CursorUtils.getInt(input, Schema._ID));
                book.setName(CursorUtils.getString(input, Schema.NAME));
                book.setAuthor(CursorUtils.getString(input, Schema.AUTHOR));
                book.setLastTime(CursorUtils.getLong(input, Schema.LAST_TIME));
                book.setFile(CursorUtils.getString(input, Schema.FILE));
                book.setPercent(CursorUtils.getString(input, Schema.PERCENT));
                book.setBeginRead(CursorUtils.getInt(input, Schema.BEGIN_READ));
            }
        });
        return book;
    }

    @Override
    public void updateBook(int id, Book book) {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.NAME, book.getName());
        contentValues.put(Schema.FILE, book.getFile());
        contentValues.put(Schema.PERCENT, book.getPercent());
        contentValues.put(Schema.AUTHOR, book.getAuthor());
        contentValues.put(Schema.LAST_TIME, book.getLastTime());
        contentValues.put(Schema.BEGIN_READ, book.getBeginRead());
        database.update(Schema.TABLE_NAME, contentValues, Schema._ID + "=?", new String[]{String.valueOf(book.getId())});
    }

    @Override
    public long insertBook(Book book) {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.NAME, book.getName());
        contentValues.put(Schema.FILE, book.getFile());
        contentValues.put(Schema.PERCENT, book.getPercent());
        contentValues.put(Schema.AUTHOR, book.getAuthor());
        contentValues.put(Schema.LAST_TIME, book.getLastTime());
        contentValues.put(Schema.BEGIN_READ, book.getBeginRead());
        return database.insert(Schema.TABLE_NAME, null, contentValues);
    }

    @Override
    public void deleteBookById(int id) {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        database.delete(Schema.TABLE_NAME, Schema._ID + "=?", new String[]{String.valueOf(id)});
    }

    @Override
    public void deleteBookFileByFile(String file) {
        if (file.startsWith("/mnt")) {
            file = file.substring(file.indexOf("/sdcard"));
        }
        File f = new File(file);
        if (f.exists()) {
            boolean b = f.delete();
        }
    }

    @Override
    public List<Section> findSectionDirectory(String path) {
        List<Section> sections = new ArrayList<Section>();
        BufferedReader br = null;
        File bookFile = new File(path);
        if (!bookFile.exists()) {
            throw new RuntimeException(path + "小说文件不存在");
        }
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile), "GBK"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        long preByte = 0L;

        Pattern pattern = Pattern.compile(patternStr);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String title = line.substring(start, end);
                    int currentSize = line.substring(0, start).getBytes("utf-8").length;
                    Long current = preByte + currentSize + 1;
                    Section section = new Section();
                    section.setName(title);
                    section.setCurrent(current);
                    sections.add(section);
                }
                preByte += line.getBytes("utf-8").length + breakLineSize;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sections;
    }

    @Override
    public void insertSections(List<Section> sections, String file) {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        database.beginTransaction();
        Gson gson = new Gson();
        for (Section section : sections) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Schema.FILE, file);
            String sec = gson.toJson(section);
            contentValues.put(Schema.SECTION, sec);
            database.insert(Schema.SECTION, null, contentValues);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    public List<Section> findSections(String file) {
        final List<Section> sections = new ArrayList<Section>();
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new ReadSqliteOpenHelper(context).getSqliteOpenHelper();
        }
        if (database == null) {
            database = sqLiteOpenHelper.getWritableDatabase();
        }
        final Gson gson = new Gson();
        Cursor cursor = database.query(Schema.SECTION, null, Schema.FILE + " =?", new String[]{file}, null, null, null);
        CursorTemplate.each(cursor, new Closure<Cursor>() {
            @Override
            public void execute(Cursor input) {
                String sec = CursorUtils.getString(input, Schema.SECTION);
                Section section = gson.fromJson(sec, Section.class);
                sections.add(section);
            }
        });
        return sections;
    }

}
