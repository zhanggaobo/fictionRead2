package com.zhanggb.fictionread.sqlit;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午9:16
 * To change this template use File | Settings | File Templates.
 */
public interface Schema {

    static String TABLE_NAME = "book";
    static String _ID = "_id";
    static String FILE = "file";
    static String NAME = "name";
    static String AUTHOR = "author";
    static String LAST_TIME = "lasttime";
    static String PERCENT = "percent";
    static String BEGIN_READ = "beginread";

    static String SECTION = "section";

    interface Environment {
        String DATABASE_NAME = "read.db";
        int DATABASE_VERSION = 20140122;
    }

    interface IBook {
        interface Sql {
            String CREATE_BOOK =
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                            "(" + _ID + " INTEGER PRIMARY KEY," +
                            FILE + " TEXT," +
                            NAME + " TEXT," +
                            AUTHOR + " TEXT," +
                            LAST_TIME + " INTEGER," +
                            BEGIN_READ + " INTEGER," +
                            PERCENT + " TEXT" +
                            ")";

            String DROP_BOOK = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

    interface ISection {
        interface Sql {
            String CREATE_SECTION =
                    "CREATE TABLE IF NOT EXISTS " + SECTION +
                            "(" + _ID + " INTEGER PRIMARY KEY," +
                            FILE + " TEXT," +
                            SECTION + " TEXT" +
                            ")";

            String DROP_SECYION = "DROP TABLE IF EXISTS " + SECTION;
        }
    }
}
