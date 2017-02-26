package com.zhanggb.fictionread.sqlit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-15
 * Time: 下午9:15
 * To change this template use File | Settings | File Templates.
 */
public class ReadSqliteOpenHelper extends SQLiteOpenHelper {

    public static ReadSqliteOpenHelper sqliteOpenHelper;
    private Context context;

    public ReadSqliteOpenHelper(Context context) {
        super(context, Schema.Environment.DATABASE_NAME, null, Schema.Environment.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(Schema.IBook.Sql.CREATE_BOOK);
        database.execSQL(Schema.ISection.Sql.CREATE_SECTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            database.execSQL(Schema.IBook.Sql.DROP_BOOK);
            database.execSQL(Schema.ISection.Sql.DROP_SECYION);
            onCreate(database);
        }
    }

    public ReadSqliteOpenHelper getSqliteOpenHelper() {
        if (sqliteOpenHelper != null) {
            return sqliteOpenHelper;
        } else {
            sqliteOpenHelper = new ReadSqliteOpenHelper(context);
        }
        return sqliteOpenHelper;
    }
}
