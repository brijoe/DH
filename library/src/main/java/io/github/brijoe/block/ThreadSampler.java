package io.github.brijoe.block;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 线程堆栈信息采样
 */


 class ThreadSampler {

    //监控采样频率为52ms
    private long SAMPLE_RATE = 52;

    private List<StackTraceElement[]> traceList = Collections.synchronizedList(
            new ArrayList<StackTraceElement[]>());

    private HandlerThread mThread = new HandlerThread("sampler");

    private Handler mHandler;
    private Runnable mSampleRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mSampleRunnable, SAMPLE_RATE);
            recordThreadTrace();
        }
    };

    private void recordThreadTrace() {
        //采集并记录堆栈
        StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
        traceList.add(stackTrace);
    }

    private ThreadSampler() {
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }


    private static class ThreadSamplerHolder {
        public static ThreadSampler mInstance = new ThreadSampler();
    }


    public static ThreadSampler getInstance() {
        return ThreadSamplerHolder.mInstance;
    }

    //启动高频采样
    public void start() {
        traceList.clear();
        mHandler.post(mSampleRunnable);
    }

    public void stop() {
        mHandler.removeCallbacks(mSampleRunnable);
    }

    //获取内存中的采样结果
    public List<StackTraceElement[]> getTraceInfo() {
        return new ArrayList<>(traceList);
    }


}
