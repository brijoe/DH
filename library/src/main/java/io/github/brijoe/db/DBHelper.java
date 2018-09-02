package io.github.brijoe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private int dbVersion;

    public DBHelper(Context context, String dbName, int initVersion) {
        super(context, dbName, null, initVersion);
        this.context = context;
        this.dbVersion = initVersion;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstant.BLOCK_SQL);
        db.execSQL(DBConstant.HTTP_SQL);
    }

    private void clearData(SQLiteDatabase db) {
        db.execSQL(DBConstant.HTTP_CLEAR_SQL);
        db.execSQL(DBConstant.BLOCK_CLEAR_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearData(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearData(db);

    }
}