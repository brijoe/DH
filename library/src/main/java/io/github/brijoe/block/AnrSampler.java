package io.github.brijoe.block;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import io.github.brijoe.DH;
import io.github.brijoe.bean.BlockInfo;
import io.github.brijoe.db.BlockRepository;

/**
 * @date: 2019/5/11
 * @author:bridgeliang
 */
public class AnrSampler extends EventAdapterCallback {

    private static final String TAG = "AnrWatcher";
    private BlockRepository logRepository = new BlockRepository(DH.getContext());

    private final int ANR_TIME = 5000;

    private HandlerThread mAnrThread = new HandlerThread(
            "Anr-watcher",
            Process.THREAD_PRIORITY_BACKGROUND);

    private static class AnrSamplerHolder {
        public static AnrSampler mInstance = new AnrSampler();

    }

    public static AnrSampler getInstance() {
        return AnrSamplerHolder.mInstance;
    }

    private Handler mAnrHandler;


    private AnrSampler() {
        mAnrThread.start();
        mAnrHandler = new Handler(mAnrThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                dumpAllThreads();
            }
        };
    }

    @Override
    public void onFrameStart(long frameTimeNanos) {
        mAnrHandler.sendEmptyMessageDelayed(0, ANR_TIME);
    }

    @Override
    public void onFrameBlock(long frameDiff) {
        mAnrHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onFrameSmooth() {
        mAnrHandler.removeCallbacksAndMessages(null);
    }

    private void dumpAllThreads() {
        final Thread mainThread = Looper.getMainLooper().getThread();
        Map<Thread, StackTraceElement[]> stackTraces = new TreeMap<Thread, StackTraceElement[]>(new Comparator<Thread>() {
            @Override
            public int compare(Thread lhs, Thread rhs) {
                if (lhs == rhs)
                    return 0;
                if (lhs == mainThread)
                    return -1;
                if (rhs == mainThread)
                    return 1;
                return rhs.getName().compareTo(lhs.getName());
            }
        });

        for (Map.Entry<Thread, StackTraceElement[]> entry :
                Thread.getAllStackTraces().entrySet())
            stackTraces.put(entry.getKey(), entry.getValue());

        // Sometimes main is not returned in getAllStackTraces() - ensure that we list it
        if (!stackTraces.containsKey(mainThread)) {
            stackTraces.put(mainThread, mainThread.getStackTrace());
        }
        Log.e(TAG, "dumpAllThreads: " + stackTraces);
        Iterator iter = stackTraces.entrySet().iterator();
        StringBuffer sb = new StringBuffer();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            // 获取key
            Thread key = (Thread) entry.getKey();
            sb.append("[").append(key.getName()).append("]").append("\n");
            StackTraceElement[] stackTraceElements = (StackTraceElement[]) entry.getValue();
            for (StackTraceElement s : stackTraceElements)
                sb.append(s.toString() + "\n");
            sb.append("\n\n");
        }
        //insert log
        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setTimeRecord(System.currentTimeMillis());
        blockInfo.setTimeCost(ANR_TIME);
        blockInfo.setTraceCount(stackTraces.size());
        blockInfo.setTraces(sb.toString());
        logRepository.insert(blockInfo);


    }
}
