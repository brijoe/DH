package io.github.brijoe;

import java.io.Serializable;

/**
 * 设备信息
 *
 * @Brijoe
 */

class DeviceInfo implements Serializable {
    private String model;
    private String osVersion;
    private String resolution;
    private String density;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

}
