package io.github.brijoe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * DH 入口
 */

public final class DH {

    private static final String TAG = "DH";
    private static Context mContext;
    private static List<Debugger> mAppendList = new ArrayList<>();

    private static int mActivityCount=0;

    private DH() {
    }

    /**
     * 初始化出口，在Application onCreate 中调用
     *
     * @param context
     */
    public static void install(Context context) {
        if (context == null || !(context instanceof Application))
            throw new IllegalArgumentException("context must be application context");
        mContext = context;
        ((Application) context).registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }


    /**
     * 获取全局Context
     *
     * @return
     */
    protected static Context getContext() {

        return mContext;
    }

    /**
     * 添加单个调试菜单 DebugHelper.addDebugger()
     *
     * @param debugger
     */
    public static void addDebugger(Debugger debugger) {
        if (mContext == null)
            throw new NullPointerException("have you called install(context) method?");
        if (debugger == null)
            return;
        mAppendList.add(debugger);
    }

    /**
     * 添加多个调试菜单
     *
     * @param list
     */
    public static void addDebugger(List<Debugger> list) {
        if (mContext == null)
            throw new NullPointerException("have you called install(context) method?");
        if (list == null || list.size() == 0)
            return;
        mAppendList.addAll(list);

    }

    //获取自定义的菜单列表
    protected static List<Debugger> getAppendList() {
        return mAppendList;
    }

    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {



        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            Log.d(TAG, "onActivityCreated: "+activity.toString());


        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted: "+activity.toString());
            mActivityCount++;
            if(mActivityCount==1) {
                SensorHelper.getInstance().register();
                Log.d(TAG, "App is in the foreground:unregister Sensor ");
            }

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed: "+activity.toString());
            if (isInnerActivity(activity)) {
                return;
            }
            SensorHelper.getInstance().inject(activity);
            dump(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused: "+activity.toString());

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped: "+activity.toString());
            mActivityCount--;
            if (mActivityCount == 0) {
                SensorHelper.getInstance().unregister();
                Log.d(TAG, "App is in the background:unregister Sensor ");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            Log.d(TAG, "onActivitySaveInstanceState: "+activity.toString());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "onActivityDestroyed: "+activity.toString());
        }
    };

    private static boolean isInnerActivity(Activity activity) {
        if (activity instanceof LogActivity
                || activity instanceof LogDetailActivity
                || activity instanceof ToolBoxActivity)
            return true;
        return false;
    }

    private static void dump(Activity activity) {
        if (activity == null)
            return;
        //页面信息
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageName(activity.getComponentName().toString());
        pageInfo.setIntentStr(activity.getIntent().toString());
//        pageInfo.setIntentStr(activity.getIntent().);
        if (activity.getCallingActivity() != null)
            pageInfo.setCaller(activity.getCallingActivity().toString());
        //进程信息
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProName(DHUtil.getAppProcessName(activity, DHUtil.getAppProcessId()) + "(" + DHUtil.getAppProcessId() + ")");
        processInfo.setFreeMemory(DHUtil.getMemory());

        //设置
        DHInfo.setsPageInfo(pageInfo);
        DHInfo.setsProcessInfo(processInfo);

        //应用信息
        if(DHInfo.getsAppInfo()==null) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPkgName(DHUtil.getAppPackageName(activity));
            appInfo.setVersion(DHUtil.getAppVersionName(activity) + "(build " + DHUtil.getAppVersionCode(activity) + ")");
            appInfo.setSignMd5(DHUtil.getSignMD5(activity));
            appInfo.setSignSha1(DHUtil.getSignSHA1(activity));
            appInfo.setSignSha256(DHUtil.getSignSHA256(activity));
            DHInfo.setsAppInfo(appInfo);
        }
        //设备信息
        if(DHInfo.getsDeviceInfo()==null) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setModel(DHUtil.getPhoneModel());
            deviceInfo.setOsVersion(DHUtil.getBuildVersion());
            deviceInfo.setResolution(DHUtil.getScreenWidth(activity) + "x" + DHUtil.getScreenHeight(activity));
            deviceInfo.setDensity(DHUtil.getDensity(activity));
            DHInfo.setsDeviceInfo(deviceInfo);
        }


    }

}
