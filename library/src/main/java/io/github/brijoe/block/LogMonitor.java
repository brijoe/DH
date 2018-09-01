package io.github.brijoe.block;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Date;
import java.util.List;

import io.github.brijoe.DH;
import io.github.brijoe.bean.BlockInfo;
import io.github.brijoe.db.BlockRepository;

class LogMonitor {

    private static BlockRepository logRepository = new BlockRepository(DH.getContext());
    private static LogMonitor sInstance = new LogMonitor();
    private HandlerThread mLogThread = new HandlerThread("Block-log");
    private Handler mIoHandler;
    private static final long TIME_BLOCK = 256;

    private int MSG_BLOCK_WHAT = 0x01;

    private LogMonitor() {
        mLogThread.start();
        mIoHandler = new Handler(mLogThread.getLooper());
    }

    private Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            //block occur,print main thread stack trace
            List<StackTraceElement[]> result = ThreadSampler.getInstance().getTraceInfo();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.size(); i++) {
                sb.append(">>>>>>>>>第[" + i + "]条堆栈<<<<<<<<<\n");
                for (StackTraceElement s : result.get(i)) {
                    sb.append(s.toString() + "\n");
                }
            }
            Log.e("BlockWatcher",
                    String.format("------发生卡顿[%d]条堆栈信息-----\n", result.size()));
            //insert log
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setTime(new Date().getTime());
            blockInfo.setTraceCount(result.size());
            blockInfo.setTraces(sb.toString());
            logRepository.insert(blockInfo);

        }
    };

    public static LogMonitor getInstance() {
        return sInstance;
    }

    public boolean isMonitor() {
        return mIoHandler.hasMessages(MSG_BLOCK_WHAT);
    }

    public void startMonitor() {
        ThreadSampler.getInstance().start();
        Message message = Message.obtain(mIoHandler, mLogRunnable);
        message.what = MSG_BLOCK_WHAT;
        mIoHandler.sendMessageDelayed(message, TIME_BLOCK);
    }

    public void removeMonitor() {
        mIoHandler.removeMessages(MSG_BLOCK_WHAT);
        ThreadSampler.getInstance().stop();
    }
}