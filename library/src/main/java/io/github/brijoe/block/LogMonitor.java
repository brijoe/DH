package io.github.brijoe.block;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.List;

import io.github.brijoe.DH;
import io.github.brijoe.bean.BlockInfo;
import io.github.brijoe.db.BlockRepository;

class LogMonitor implements BlockWatcher.BlockCallback {
    private BlockRepository logRepository = new BlockRepository(DH.getContext());
    private HandlerThread mLogThread = new HandlerThread("Block-log");
    private Handler mLogHandler;
    private final int MSG_BLOCK_WHAT = 0x01;

    private LogMonitor() {
        mLogThread.start();
        mLogHandler = new Handler(mLogThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_BLOCK_WHAT:
                        dumpWhenBlock((long) msg.obj);
                        break;
                }
            }
        };
    }
    private void dumpWhenBlock(long blockTime) {
        //block occur,print main thread stack trace
        List<StackTraceElement[]> result = ThreadSampler.getInstance().getTraceInfo();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.size(); i++) {
            sb.append(">>>>>>>>>>>>>>第[" + (i+1) + "]条堆栈<<<<<<<<<<<<<<\n");
            for (StackTraceElement s : result.get(i)) {
                sb.append(s.toString() + "\n");
            }
            sb.append("\n\n");
        }
        Log.e("DH",
                String.format("------发生卡顿[%d]条堆栈信息-----\n", result.size()));
        //insert log
        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setTimeRecord(System.currentTimeMillis());
        blockInfo.setTimeCost(blockTime);
        blockInfo.setTraceCount(result.size());
        blockInfo.setTraces(sb.toString());
        logRepository.insert(blockInfo);


    }

    private static class LogMonitorHolder {
        public static LogMonitor mInstance = new LogMonitor();
    }

    public static LogMonitor getInstance() {
        return LogMonitorHolder.mInstance;
    }


    @Override
    public void onFrameStart(long frameTimeNanos) {
        //do nothing
    }

    @Override
    public void onFrameBlock(long frameDiff) {
        Message.obtain(mLogHandler,MSG_BLOCK_WHAT,frameDiff).sendToTarget();
    }

    @Override
    public void onFrameSmooth() {
    //do nothing
    }
}