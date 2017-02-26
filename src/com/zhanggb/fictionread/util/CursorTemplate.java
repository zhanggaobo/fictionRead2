package com.zhanggb.fictionread.util;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-22
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
public class CursorTemplate {


    /**
     * 遍历cursor
     *
     * @param cursor      Android Cursor
     * @param closure     Closure
     * @param closeCursor close cursor?
     */
    public static void each(Cursor cursor, Closure<Cursor> closure, boolean closeCursor) {
        if (cursor == null) {
            return;
        }
        try {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        closure.execute(cursor);
                    } catch (Closure.VetoException ex) {
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (closeCursor && null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }


    public static void one(Cursor cursor, Closure<Cursor> closure, boolean closeCursor) {
        if (cursor == null) {
            return;
        }
        try {
            if (cursor.moveToNext()) {
                closure.execute(cursor);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (closeCursor && null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public static void one(Cursor cursor, Closure<Cursor> closure) {
        one(cursor, closure, true);
    }


    public static void each(Cursor cursor, Closure<Cursor> closure) {
        each(cursor, closure, true);
    }

    /**
     * 游标是否存在数据
     *
     * @param cursor Cursor
     * @return exists
     */
    public static boolean exist(Cursor cursor) {
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }
}
