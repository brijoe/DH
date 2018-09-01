package io.github.brijoe.bean;

import java.io.Serializable;

public class ProcessInfo implements Serializable {

    private String proName;

    private String freeMemory;


    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }
}
