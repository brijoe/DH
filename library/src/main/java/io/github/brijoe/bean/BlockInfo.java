package io.github.brijoe.bean;

import java.io.Serializable;

public class BlockInfo implements Serializable {

    private long id;
    private long timeRecord;
    private long timeCost;
    private int traceCount;
    private String traces;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(long time) {
        this.timeRecord = time;
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }

    public int getTraceCount() {
        return traceCount;
    }

    public void setTraceCount(int traceCount) {
        this.traceCount = traceCount;
    }

    public String getTraces() {
        return traces;
    }

    public void setTraces(String traces) {
        this.traces = traces;
    }
}
