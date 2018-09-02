package io.github.brijoe.block;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class BlockWatcher {

    private static final String TAG = "BlockWatcher";

    private static final long TIME_BLOCK = 256;
    private static long lastFrameTimeNanos = 0;

    public interface BlockCallback {
        void onFrameStart(long frameTimeNanos);
        void onFrameBlock(long frameDiff);
        void onFrameSmooth();
    }
    private static List<BlockCallback> mBlockCallbacks =new ArrayList<>();

    public static void init(){
        mBlockCallbacks.add(ThreadSampler.getInstance());
        mBlockCallbacks.add(LogMonitor.getInstance());
        start();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return;
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {

            @Override
            public void doFrame(long frameTimeNanos) {
                Choreographer.getInstance().postFrameCallback(this);
                for(BlockCallback callback:mBlockCallbacks)
                    if(callback!=null) {
                        callback.onFrameStart(frameTimeNanos);
                    }
                if (lastFrameTimeNanos != 0) {
                    long diffMs = TimeUnit.MILLISECONDS.convert(
                            frameTimeNanos - lastFrameTimeNanos, TimeUnit.NANOSECONDS);
                    //frame callback time more than threshold
                    if (diffMs > TIME_BLOCK) {
                        for(BlockCallback callback:mBlockCallbacks)
                            if(callback!=null) {
                                callback.onFrameBlock(diffMs);
                            }
                    }
                    //frame smooth
                    else{
                        for(BlockCallback callback:mBlockCallbacks)
                            if(callback!=null) {
                                callback.onFrameSmooth();
                            }
                    }
                }
                else{

                }
                lastFrameTimeNanos = frameTimeNanos;
            }
        });
    }


}