package io.github.brijoe.block;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

public class BlockWatcher {

    private static final String TAG = "BlockWatcher";

    private static long lastFrameTimeNanos = 0;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return;
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {

            @Override
            public void doFrame(long frameTimeNanos) {
                if(lastFrameTimeNanos!=0) {
                    //计算掉帧数
                    long diffMs = TimeUnit.MILLISECONDS.convert(
                            frameTimeNanos - lastFrameTimeNanos, TimeUnit.NANOSECONDS);
                    if (diffMs > 16.6f) {
                        //掉帧数统计
                        int droppedCount = (int) (diffMs / 16.6);
                        Log.e(TAG, "掉帧数" + droppedCount);
                    }
                }
                lastFrameTimeNanos = frameTimeNanos;
                //监控中移除
                if (LogMonitor.getInstance().isMonitor()) {
                    LogMonitor.getInstance().removeMonitor();
                }
                LogMonitor.getInstance().startMonitor();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }


}