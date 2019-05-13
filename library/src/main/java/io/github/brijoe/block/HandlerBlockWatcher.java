package io.github.brijoe.block;

import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Printer;

/**
 * @date: 2019/5/8
 * @author:bridgeliang
 */
class HandlerBlockWatcher extends BlockWatcher implements Printer {

    private static final String TAG = "HandlerBlockWatcher";
    private long mLastPrintTime = 0;

    @Override
    protected void startWatch() {
        Looper.getMainLooper().setMessageLogging(this);
    }

    @Override
    public void println(String logStr) {
        if (TextUtils.isEmpty(logStr))
            return;
        // >>>>> Dispatching to Handler
        if (logStr.startsWith(">>>>>")) {
            mLastPrintTime = SystemClock.uptimeMillis();
            dispatchEventCallback(EVENT_START, mLastPrintTime);
        }
        // <<<<< Finished to Handler
        else if (logStr.startsWith("<<<<<")) {
            long blockTime = SystemClock.uptimeMillis() - mLastPrintTime;
            if (blockTime <= TIME_BLOCK) {
                dispatchEventCallback(EVENT_SMOOTH);
            } else if (blockTime <= TIME_ANR) {
                dispatchEventCallback(EVENT_BLOCK, blockTime);
            }
        }
    }
}
