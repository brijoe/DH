package io.github.brijoe.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.brijoe.bean.RealWatchInfo;

public class StateSampler implements Runnable {

    private String TAG = "StateSampler";

    private ScheduledExecutorService scheduler;
    private ActivityManager activityManager;
    private long freq;
    private Long lastCpuTime;
    private Long lastAppCpuTime;
    private RandomAccessFile procStatFile;
    private RandomAccessFile appStatFile;

    private DecimalFormat format;

    private FpsCalculator fpsCalculator;

    private StateSampler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        format = new DecimalFormat("#.##");
        fpsCalculator = FpsCalculator.instance();
    }

    private static class SingleHolder {

        static StateSampler sInstance = new StateSampler();
    }

    public static StateSampler getInstance() {
        return SingleHolder.sInstance;
    }

    // freq为采样周期
    public void init(Context context, long freq) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.freq = freq;
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this, 0L, freq, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        //process and transfer data
        //frame data
        String fps = "-/-";
        if (fpsCalculator.isCalculatingFPS()) {
            fps = fpsCalculator.stopGetAvgFPS() + "fps";
            fpsCalculator.startCalculate();
        } else {
            fpsCalculator.startCalculate();
        }
        //cpu data
        String cpu;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cpu = "n/a";
        } else {
            cpu = format.format(sampleCPU()) + "%";
        }
        //memory data
        double[] memData = sampleMemory();
        String[] mem = new String[memData.length];
        for (int i = 0; i < memData.length; i++)
            mem[i] = format.format(memData[i]) + "MB";
        //thread info
        List<String> threadInfo = sampleThread();

        MonitorManager.getInstance().updateData(new RealWatchInfo(fps, cpu, mem, threadInfo));
    }

    //采集CPU 使用率，8.0以上无法读取 /proc/stat 数据
    private double sampleCPU() {
        long cpuTime;
        long appTime;
        double sampleValue = 0.0D;
        try {
            if (procStatFile == null || appStatFile == null) {
                procStatFile = new RandomAccessFile("/proc/stat", "r");
                appStatFile = new RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r");
            } else {
                procStatFile.seek(0L);
                appStatFile.seek(0L);
            }
            String procStatString = procStatFile.readLine();
            String appStatString = appStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (lastCpuTime == null && lastAppCpuTime == null) {
                lastCpuTime = cpuTime;
                lastAppCpuTime = appTime;
                return sampleValue;
            }
            sampleValue = ((double) (appTime - lastAppCpuTime) / (double) (cpuTime - lastCpuTime)) * 100D;
            lastCpuTime = cpuTime;
            lastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sampleValue;
    }

    private double[] sampleMemory() {
        double[] mem = {0.0D, 0.0D, 0.0D, 0.0D};
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
            if (memInfo.length > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                final int totalPss = memInfo[0].getTotalPss();
                final int dalvikPss = memInfo[0].dalvikPss;
                final int nativePss = memInfo[0].nativePss;
                final int otherPss = totalPss - dalvikPss - nativePss;
                // Mem in MB
                mem[0] = totalPss / 1024.0D;
                mem[1] = dalvikPss / 1024.0D;
                mem[2] = nativePss / 1024.0D;
                mem[3] = otherPss / 1024.0D;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    private List<String> sampleThread() {
        List<String> result = new ArrayList<>();
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);
        for (Thread thread : threads) {
            result.add("（" + thread.getId() + "）-[" + thread.getName() + "]-" + thread.getState());
        }
        return result;
    }

    public void stop() {
        if (scheduler != null)
            scheduler.shutdownNow();
        scheduler = null;
    }
}
