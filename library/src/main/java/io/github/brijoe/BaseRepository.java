package io.github.brijoe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

 class BaseRepository<T> {
    protected DBHelper dbHelper;

    public BaseRepository(Context context) {
        dbHelper = DBInterface.getInstance(context).getDbHelper();
    }


    public long insert(String table, String nullColumnHack, ContentValues values) {
        long ret = 0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ret = database.insert(table, nullColumnHack, values);
            database.setTransactionSuccessful();
        } catch (RuntimeException e) {
        } finally {
            database.endTransaction();
        }
        return ret;
    }


    public <T> List<T> query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, Integer limit) {
        List<T> results = new ArrayList<T>();
        Cursor cursor = null;
        try {
            if (limit != null) {
                cursor = dbHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit + "");
            } else {
                cursor = dbHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            }
            results = queryResult(cursor);
        } catch (RuntimeException e) {
            Log.e("query error. ", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return results;
    }


    public <T> List<T> queryResult(Cursor cursor) {
        throw new RuntimeException("Please overwrite method.");
    }


    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        int ret = 0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ret = database.update(table, values, whereClause, whereArgs);
            database.setTransactionSuccessful();
        } catch (RuntimeException e) {
            Log.e("update error. ", e.toString());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

    public int delete(String table, String whereClause, String[] whereArgs) {

        int ret = 0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ret = database.delete(table, whereClause, whereArgs);
            database.setTransactionSuccessful();
        } catch (RuntimeException e) {
            Log.e("delete error. ", e.toString());
        } finally {
            database.endTransaction();
        }
        return ret;
    }

}