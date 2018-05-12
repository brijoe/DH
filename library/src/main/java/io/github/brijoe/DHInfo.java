package io.github.brijoe;


class DHInfo {

    private static PageInfo sPageInfo;
    private static AppInfo sAppInfo;
    private static DeviceInfo sDeviceInfo;
    private static ProcessInfo sProcessInfo;


    public static PageInfo getsPageInfo() {
        return sPageInfo;
    }

    public static void setsPageInfo(PageInfo sPageInfo) {
        DHInfo.sPageInfo = sPageInfo;
    }

    public static AppInfo getsAppInfo() {
        return sAppInfo;
    }

    public static void setsAppInfo(AppInfo sAppInfo) {
        DHInfo.sAppInfo = sAppInfo;
    }

    public static ProcessInfo getsProcessInfo() {
        return sProcessInfo;
    }

    public static void setsProcessInfo(ProcessInfo sProcessInfo) {
        DHInfo.sProcessInfo = sProcessInfo;
    }

    public static DeviceInfo getsDeviceInfo() {
        return sDeviceInfo;
    }


    public static void setsDeviceInfo(DeviceInfo sDeviceInfo) {
        DHInfo.sDeviceInfo = sDeviceInfo;
    }
}
