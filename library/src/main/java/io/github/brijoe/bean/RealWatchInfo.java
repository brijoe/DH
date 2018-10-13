package io.github.brijoe.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class RealWatchInfo implements Serializable {

    private String fps;
    private String cpu;

    private String[] mem;

    private List<String> threadInfo;


    public RealWatchInfo(String fps, String cpu, String[] mem, List<String> threadInfo) {
        this.fps = fps;
        this.cpu = cpu;
        this.mem = mem;
        this.threadInfo = threadInfo;
    }

    public String getFps() {
        return this.fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getCpu() {
        return this.cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String[] getMem() {
        return this.mem;
    }

    public void setMem(String[] mem) {
        this.mem = mem;
    }


    public List<String> getThreadInfo() {
        return this.threadInfo;
    }

    public void setThreadInfo(List<String> threadInfo) {
        this.threadInfo = threadInfo;
    }

    @Override
    public String toString() {
        return "RealWatchInfo{" +
                "fps='" + fps + '\'' +
                ", cpu='" + cpu + '\'' +
                ", mem=" + Arrays.toString(mem) +
                ", threadInfo=" + threadInfo +
                '}';
    }

}
