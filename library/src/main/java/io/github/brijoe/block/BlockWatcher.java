package io.github.brijoe.block;

import java.util.ArrayList;
import java.util.List;


public class BlockWatcher {

    private static final String TAG = "BlockWatcher";

    protected final long TIME_BLOCK = 256;

    protected final int TIME_ANR = 5000;

    protected final int EVENT_START = 0x01;
    protected final int EVENT_BLOCK = 0x02;
    protected final int EVENT_SMOOTH = 0x03;


    protected static List<EventCallback> mBlockEventCallbacks = new ArrayList<>();

    static {
        mBlockEventCallbacks.add(ThreadSampler.getInstance());
        mBlockEventCallbacks.add(LogSampler.getInstance());
        mBlockEventCallbacks.add(AnrSampler.getInstance());
    }

    private static BlockWatcher DEFAULT_WATCHER = new HandlerBlockWatcher();

    public static void init() {
        DEFAULT_WATCHER.startWatch();
    }


    protected void startWatch() {
        throw new UnsupportedOperationException();
    }


    protected final void dispatchEventCallback(int event, long... time) {
        for (EventCallback callback : mBlockEventCallbacks) {
            switch (event) {
                case EVENT_START:
                    if (callback != null) {
                        callback.onFrameStart(time[0]);
                    }
                    break;

                case EVENT_SMOOTH:
                    if (callback != null) {
                        callback.onFrameSmooth();
                    }
                    break;

                case EVENT_BLOCK:
                    if (callback != null) {
                        callback.onFrameBlock(time[0]);
                    }
                    break;
            }
        }
    }

}

