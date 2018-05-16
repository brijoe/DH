package io.github.brijoe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * DH tools,a development tools library for Android.
 *
 * @author Brijoe
 */

public final class DH {

    private static final String TAG = "DH";
    private static Context mContext;
    private static List<Debugger> mAppendList = new ArrayList<>();

    private static int mActivityCount = 0;

    private static boolean mEnabled =true;

    private DH() { }


    /**
     * Start watching activity references (on ICS+), the enable flag is set to true by default.
     *
     * @param context Application context
     */

    public static void  install(Context context) {
       install(context,true);
    }

    /**
     * Start watching activity references (on ICS+),the enable flag is controlled by caller.
     * @param context Application context
     * @param enabled whether the DH library is enabled or not.
     */
    public static void install(Context context,boolean enabled){
        if (context == null || !(context instanceof Application))
            throw new IllegalArgumentException("context must be application context");
        mContext = context;
        mEnabled =enabled;
       if(enabled)
            ((Application) context).registerActivityLifecycleCallbacks(lifecycleCallbacks);

    }

    protected static Context getContext() {

        return mContext;
    }

    protected static boolean getEnabled(){
        return mEnabled;
    }

    /**
     * Add single item {@link Debugger} to the dialog.
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
     * Add multiple items {@link Debugger} to the dialog.
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

    protected static List<Debugger> getAppendList() {
        return mAppendList;
    }

    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {


        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            Log.d(TAG, "onActivityCreated: " + activity.toString());


        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted: " + activity.toString());
            mActivityCount++;
            if (mActivityCount == 1) {
                SensorHelper.getInstance().register();
                Log.d(TAG, "App is in the foreground:register Sensor ");
            }

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed: " + activity.toString());
            if (isInnerActivity(activity)) {
                return;
            }
            SensorHelper.getInstance().inject(activity);
            dump(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused: " + activity.toString());

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped: " + activity.toString());
            mActivityCount--;
            if (mActivityCount == 0) {
                SensorHelper.getInstance().unregister();
                Log.d(TAG, "App is in the background:unregister Sensor ");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            Log.d(TAG, "onActivitySaveInstanceState: " + activity.toString());
            if(isInnerActivity(activity))
                return;
            SensorHelper.getInstance().destroy();

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "onActivityDestroyed: " + activity.toString());
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
        //pageInfo
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageName(activity.getComponentName().toString());
        pageInfo.setIntentStr(activity.getIntent().toString());
        if (activity.getCallingActivity() != null)
            pageInfo.setCaller(activity.getCallingActivity().toString());
        //processInfo
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProName(DHTool.getAppProcessName(activity, DHTool.getAppProcessId()) + "(" + DHTool.getAppProcessId() + ")");
        processInfo.setFreeMemory(DHTool.getMemory());

        DHInfo.setsPageInfo(pageInfo);
        DHInfo.setsProcessInfo(processInfo);

        //appInfo
        if (DHInfo.getsAppInfo() == null) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPkgName(DHTool.getAppPackageName(activity));
            appInfo.setVersion(DHTool.getAppVersionName(activity) + "(build " + DHTool.getAppVersionCode(activity) + ")");
            appInfo.setSignMd5(DHTool.getSignMD5(activity));
            appInfo.setSignSha1(DHTool.getSignSHA1(activity));
            appInfo.setSignSha256(DHTool.getSignSHA256(activity));
            DHInfo.setsAppInfo(appInfo);
        }
        //deviceInfo
        if (DHInfo.getsDeviceInfo() == null) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setModel(DHTool.getPhoneModel());
            deviceInfo.setOsVersion(DHTool.getBuildVersion());
            deviceInfo.setResolution(DHTool.getScreenWidth(activity) + "x" + DHTool.getScreenHeight(activity));
            deviceInfo.setDensity(DHTool.getDensity(activity));
            DHInfo.setsDeviceInfo(deviceInfo);
        }


    }

}
