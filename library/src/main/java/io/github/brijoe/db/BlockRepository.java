package io.github.brijoe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import io.github.brijoe.bean.BlockInfo;

public class BlockRepository extends BaseRepository<BlockInfo> {


    public BlockRepository(Context context) {
        super(context);
    }


    public long insert(BlockInfo blockInfo) {
        ContentValues values = getContentValues(blockInfo);
        return insert(DBConstant.TABLE_BLOCK, null, values);
    }


    public void deleteAll() {
        delete(DBConstant.TABLE_BLOCK, "", null);
    }


    public List<BlockInfo> readAllLogs() {
        List<BlockInfo> list = query(DBConstant.TABLE_BLOCK, null, null, null, null, null, "ID DESC", null);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return null;
    }

    @Override
    public List<BlockInfo> queryResult(Cursor cursor) {
        List<BlockInfo> logList = new ArrayList<>();
        BlockInfo log;
        while (cursor.moveToNext()) {
            log = new BlockInfo();
            log.setId(cursor.getLong(cursor.getColumnIndex("ID")));
            log.setTimeRecord(cursor.getLong(cursor.getColumnIndex("DATE")));
            log.setTimeCost(cursor.getLong(cursor.getColumnIndex("TIME_COST")));
            log.setTraceCount(cursor.getInt(cursor.getColumnIndex("TRACE_COUNT")));
            log.setTraces(cursor.getString(cursor.getColumnIndex("TRACE_CONTENT")));
            logList.add(log);
        }
        return logList;
    }

    private ContentValues getContentValues(BlockInfo log) {
        ContentValues cv = new ContentValues();
        cv.put("DATE", log.getTimeRecord());
        cv.put("TIME_COST", log.getTimeCost());
        cv.put("TRACE_COUNT", log.getTraceCount());
        cv.put("TRACE_CONTENT", log.getTraces());
        return cv;
    }


}