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
        for(int i = 1; i <= this.dbVersion; ++i) {
            this.upgradeSqlData(db, i, false);
        }
    }

    private void upgradeSqlData(SQLiteDatabase db, int version, boolean isDowngrade) {
        db.execSQL(DBConstant.BLOCK_SQL);
        db.execSQL(DBConstant.HTTP_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion + 1; i <= newVersion; ++i) {
            this.upgradeSqlData(db, i, false);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion - 1; i >= newVersion; --i) {
            this.upgradeSqlData(db, i, false);
        }

    }
}