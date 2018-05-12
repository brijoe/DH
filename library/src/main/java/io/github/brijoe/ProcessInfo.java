package io.github.brijoe;

import java.io.Serializable;

/**
 * 进程信息
 *
 * @author Brijoe
 */

class ProcessInfo implements Serializable {

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
