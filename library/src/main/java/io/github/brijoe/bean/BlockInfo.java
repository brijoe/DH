package io.github.brijoe.bean;

import java.io.Serializable;

public class BlockInfo implements Serializable {

    private long id;
    private long time;
    private int traceCount;
    private String traces;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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
