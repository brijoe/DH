package io.github.brijoe.db;

public class DBConstant {
    public static final String DB_NAME = "DH.db";
    public static final String TABLE_HTTP = "HTTP";
    public static final String TABLE_BLOCK = "BLOCK";
    public static final int VERSION = 2;


    public static final String BLOCK_CLEAR_SQL = "DROP TABLE IF EXISTS " + TABLE_BLOCK;

    public static final String HTTP_CLEAR_SQL = "DROP TABLE IF EXISTS " + TABLE_HTTP;


    public static final String HTTP_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_HTTP + " ( ID INTEGER PRIMARY KEY  AUTOINCREMENT," +
                    "REQUEST_TYPE  TEXT NOT NULL," +
                    "URL INT NOT NULL," +
                    "DATE TEXT NOT NULL," +
                    "REQUEST_HEADERS TEXT NOT NULL," +
                    "RESPONSE_HEADERS TEXT NOT NULL," +
                    "RESPONSE_CODE TEXT," +
                    "RESPONSE_DATA TEXT," +
                    "DURATION INT," +
                    "ERROR_CLIENT_DESC TEXT," +
                    "POST_DATA TEXT)";

    public static final String BLOCK_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BLOCK + " ( ID INTEGER PRIMARY KEY  AUTOINCREMENT," +
                    "DATE TEXT NOT NULL," +
                    "TIME_COST TEXT," +
                    "TRACE_COUNT INT," +
                    "TRACE_CONTENT TEXT)";
}
