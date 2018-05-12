package io.github.brijoe;

import android.content.Context;

 class DBInterface {
    private static DBInterface instance;
    private static DBHelper dbHelper;
    private DBInterface() {
    }

    public static DBInterface getInstance(Context context) {
        if (instance == null) {
            instance = new DBInterface();
            initDBHelper(context);
        }
        return instance;
    }

    private static void initDBHelper(Context context) {
        dbHelper = new DBHelper(context, DBConstant.DB_NAME,DBConstant.VERSION);
        dbHelper.onCreate(dbHelper.getWritableDatabase());
    }


    public DBHelper getDbHelper(){
        return dbHelper;
    }
}