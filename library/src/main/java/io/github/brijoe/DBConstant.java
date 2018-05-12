package io.github.brijoe;

class DBConstant {
    public static final String DB_NAME = "DH.db";
    public static final String TABLE_LOG = "LOG";
    public static final int VERSION = 1;

    public static final String SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOG + " ( ID INT PRIMARY KEY NOT NULL," +
                    "REQUEST_TYPE          TEXT NOT NULL," +
                    "URL INT NOT NULL," +
                    "DATE TEXT NOT NULL," +
                    "REQUEST_HEADERS TEXT NOT NULL," +
                    "RESPONSE_HEADERS TEXT NOT NULL," +
                    "RESPONSE_CODE TEXT," +
                    "RESPONSE_DATA TEXT," +
                    "DURATION INT," +
                    "ERROR_CLIENT_DESC TEXT," +
                    "POST_DATA TEXT)";
}
