package io.github.brijoe.anr;

import android.app.ActivityManager;
import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import io.github.brijoe.DH;
import io.github.brijoe.db.BlockRepository;

/**
 * 监控 ANR
 *
 * @date: 2019/5/8
 * @author:bridgeliang
 */
public class AnrFileWatcher extends FileObserver {


    private static final String TAG = "AnrFileWatcher";

    private AnrFileWatcher() {
        super("/data/anr");
    }


    public static void init() {
        new AnrFileWatcher().startWatching();
    }

    @Override
    public void onEvent(int event, String path) {
        Log.d(TAG, "onEvent: " + path);
//        if (path != null) {
//            String filepath = "/data/anr/" + path;
//            if (filepath.contains("trace")) {
//                filterANR();
//            }
//        }
    }

    @Override
    public void startWatching() {
        super.startWatching();
    }

    @Override
    public void stopWatching() {
        super.stopWatching();
    }

    private long lastTimes = 0;


    private void filterANR() {
        try {
            long nowTime = System.currentTimeMillis();
            if (nowTime - lastTimes < 10000L) {
                Log.d(TAG, "should not process ANR too Fre in 10000");
            } else {
                lastTimes = nowTime;
                ActivityManager.ProcessErrorStateInfo errorStateInfo = findError(10000L);
                if (errorStateInfo == null) {
                    Log.d(TAG, "proc state is unvisiable!");
                } else if (errorStateInfo.pid == android.os.Process.myPid()) {
                    Log.d(TAG, "not mind proc!" + errorStateInfo.processName);
                    String msg = "Found ANR in !" + errorStateInfo.processName + ":\r\n " + errorStateInfo.longMsg + "\n\n";
//                    String crashFileName = "";
                    Log.d(TAG, msg);

                } else {
                    Log.d(TAG, "found visiable anr , start to process!");
                }
            }
        } catch (Throwable throwable) {
            Log.d(TAG, "handle anr error  " + throwable.getMessage());
        }
    }

    protected ActivityManager.ProcessErrorStateInfo findError(long time) {
        time = time < 0L ? 0L : time;
        ActivityManager activityManager = (ActivityManager) DH.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        long var5 = time;
        int index = 0;
        do {
            Log.d(TAG, "waiting!");
            List errorStateInfoList = activityManager.getProcessesInErrorState();
            if (errorStateInfoList != null) {
                Iterator iterator = errorStateInfoList.iterator();
                while (iterator.hasNext()) {
                    ActivityManager.ProcessErrorStateInfo errorStateInfo = (ActivityManager.ProcessErrorStateInfo) iterator.next();
                    if (errorStateInfo.condition ==
                            ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
                        return errorStateInfo;
                    }
                }
            }
        } while ((index++) < var5);
        Log.d(TAG, "end!");
        return null;
    }
}
