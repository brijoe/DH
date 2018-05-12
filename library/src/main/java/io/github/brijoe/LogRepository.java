package io.github.brijoe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * private Long id;
 * private String requestType;
 * private String url;
 * private Long date;
 * private String headers;
 * private String responseCode;
 * private String responseData;
 * private Double duration;
 * private String errorClientDesc;
 * private String postData;
 */
 public class LogRepository extends BaseRepository<NetworkLog> {


    public LogRepository(Context context) {
        super(context);
    }

    /**
     * 插入数据
     *
     * @param networkLog
     * @return
     */
    public long insert(NetworkLog networkLog) {
        ContentValues values = getContentValues(networkLog);
        values.remove("id");
        return insert(DBConstant.TABLE_LOG, null, values);
    }

    /**
     * 更新数据
     *
     * @param networkLog
     */
    public void update(NetworkLog networkLog) {
        ContentValues cv = getContentValues(networkLog);
        cv.remove("id");
        update(DBConstant.TABLE_LOG, cv, "id = ?", new String[]{networkLog.getId() + ""});
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public void delete(int id) {
        delete(DBConstant.TABLE_LOG, "id = ?", new String[]{id + ""});
    }

    public void deleteAll() {
        delete(DBConstant.TABLE_LOG,"",null);
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<NetworkLog> readAllLogs() {
        List<NetworkLog> list = query(DBConstant.TABLE_LOG, null, null, null, null, null, "ID DESC", null);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return null;
    }

    @Override
    public List<NetworkLog> queryResult(Cursor cursor) {
        List<NetworkLog> logList = new ArrayList<>();
        NetworkLog log;
        while (cursor.moveToNext()) {
            log = new NetworkLog();
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

    private ContentValues getContentValues(NetworkLog log) {
        ContentValues cv = new ContentValues();
        cv.put("ID", log.getId());
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