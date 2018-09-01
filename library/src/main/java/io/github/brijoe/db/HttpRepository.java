package io.github.brijoe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import io.github.brijoe.bean.NetworkInfo;

public class HttpRepository extends BaseRepository<NetworkInfo> {


    public HttpRepository(Context context) {
        super(context);
    }


    public long insert(NetworkInfo networkLog) {
        ContentValues values = getContentValues(networkLog);
        return insert(DBConstant.TABLE_HTTP, null, values);
    }


    public void update(NetworkInfo networkLog) {
        ContentValues cv = getContentValues(networkLog);
        update(DBConstant.TABLE_HTTP, cv, "id = ?", new String[]{networkLog.getId() + ""});
    }


    public void delete(int id) {
        delete(DBConstant.TABLE_HTTP, "id = ?", new String[]{id + ""});
    }

    public void deleteAll() {
        delete(DBConstant.TABLE_HTTP,"",null);
    }


    public List<NetworkInfo> readAllLogs() {
        List<NetworkInfo> list = query(DBConstant.TABLE_HTTP, null, null, null, null, null, "ID DESC", null);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return null;
    }

    @Override
    public List<NetworkInfo> queryResult(Cursor cursor) {
        List<NetworkInfo> logList = new ArrayList<>();
        NetworkInfo log;
        while (cursor.moveToNext()) {
            log = new NetworkInfo();
            log.setId(cursor.getLong(cursor.getColumnIndex("ID")));
            log.setRequestType(cursor.getString(cursor.getColumnIndex("REQUEST_TYPE")));
            log.setUrl(cursor.getString(cursor.getColumnIndex("URL")));
            log.setDate(cursor.getLong(cursor.getColumnIndex("DATE")));
            log.setRequestHeaders(cursor.getString(cursor.getColumnIndex("REQUEST_HEADERS")));
            log.setResponseHeaders(cursor.getString(cursor.getColumnIndex("RESPONSE_HEADERS")));
            log.setResponseCode(cursor.getString(cursor.getColumnIndex("RESPONSE_CODE")));
            log.setResponseData(cursor.getString(cursor.getColumnIndex("RESPONSE_DATA")));
            log.setDuration(cursor.getDouble(cursor.getColumnIndex("DURATION")));
            log.setErrorClientDesc(cursor.getString(cursor.getColumnIndex("ERROR_CLIENT_DESC")));
            log.setPostData(cursor.getString(cursor.getColumnIndex("POST_DATA")));
            logList.add(log);
        }
        return logList;
    }

    private ContentValues getContentValues(NetworkInfo log) {
        ContentValues cv = new ContentValues();
//        cv.put("ID", log.getId());
        cv.put("REQUEST_TYPE", log.getRequestType());
        cv.put("URL", log.getUrl());
        cv.put("DATE", log.getDate());
        cv.put("REQUEST_HEADERS",log.getRequestHeaders());
        cv.put("RESPONSE_HEADERS", log.getResponseHeaders());
        cv.put("RESPONSE_CODE", log.getResponseCode());
        cv.put("RESPONSE_DATA", log.getResponseData());
        cv.put("DURATION", log.getDuration());
        cv.put("ERROR_CLIENT_DESC", log.getErrorClientDesc());
        cv.put("POST_DATA", log.getPostData());
        return cv;
    }


}