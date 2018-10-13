package io.github.brijoe.example;

public class GhostThread  {
    private static final String TAG = "GhostThread";
    
    private static boolean isStart = false;
    
    private static Thread[] threads = new Thread[3];
    
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (; ;) {
                if (!isStart) {
                    break;
                }
                double c = Math.PI * Math.PI * Math.PI; 
            }
        }
    };
    
    public static void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(runnable);
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
            threads[i] = thread;
        }
    }
    
    public static void stop() {
        isStart = false;
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}