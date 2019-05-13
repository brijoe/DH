package io.github.brijoe.block;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 线程堆栈信息采样
 */


class ThreadSampler extends EventAdapterCallback {

    private final int STACK_SIZE = 5;
    private final long SAMPLE_RATE = 52;
    private final int MSG_SAMPLE_ONCE = 0x01;
    private final int MSG_SAMPLE_PERIOD = 0x02;
    private List<StackTraceElement[]> traceList = Collections.synchronizedList(
            new ArrayList<StackTraceElement[]>());

    private HandlerThread mSampleThread = new HandlerThread(
            "Thread-Sampler", Process.THREAD_PRIORITY_BACKGROUND);
    private Handler mSampleHandler;


    private void recordThreadTrace() {
        //limit traceList's size to reduce log size
        if (traceList.size() < STACK_SIZE) {
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            traceList.add(stackTrace);
        }
    }

    private ThreadSampler() {
        mSampleThread.start();
        mSampleHandler = new Handler(mSampleThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SAMPLE_PERIOD:
                        Message message = Message.obtain();
                        message.what = MSG_SAMPLE_PERIOD;
                        mSampleHandler.sendMessageDelayed(message, SAMPLE_RATE);
                        recordThreadTrace();
                        break;
                    case MSG_SAMPLE_ONCE:
                        recordThreadTrace();
                        break;
                }
            }
        };
    }

    private boolean isSampleStart = false;

    @Override
    public void onFrameStart(long frameTimeNanos) {
        //start main thread stacktrace
        if (!isSampleStart) {
            //have not started yet,start immediately
            Message.obtain(mSampleHandler, MSG_SAMPLE_PERIOD).sendToTarget();
            isSampleStart = true;
        } else {
            //if started, sample immediately
            Message.obtain(mSampleHandler, MSG_SAMPLE_ONCE).sendToTarget();

        }
    }

    @Override
    public void onFrameSmooth() {
        traceList.clear();

    }

    private static class ThreadSamplerHolder {
        public static ThreadSampler mInstance = new ThreadSampler();
    }

    public static ThreadSampler getInstance() {
        return ThreadSamplerHolder.mInstance;
    }

    //获取内存中的采样结果
    public List<StackTraceElement[]> getTraceInfo() {
        return new ArrayList<>(traceList);
    }
}
