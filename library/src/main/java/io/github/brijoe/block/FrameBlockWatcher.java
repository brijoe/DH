package io.github.brijoe.block;

import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

/**
 * 此种方案 post 频率太高，CPU 消耗有点大
 *
 * @date: 2019/5/8
 * @author:bridgeliang
 */
class FrameBlockWatcher extends BlockWatcher {


    @Override
    protected void startWatch() {
        Choreographer.getInstance().postFrameCallback(new BlockFrameCallback());
    }

    class BlockFrameCallback implements Choreographer.FrameCallback {

        private long lastFrameTimeNanos = 0;

        @Override
        public void doFrame(long frameTimeNanos) {
            //todo 能否做优化？
            Choreographer.getInstance().postFrameCallback(this);
            dispatchEventCallback(EVENT_START, frameTimeNanos);
            if (lastFrameTimeNanos != 0) {
                long diffMs = TimeUnit.MILLISECONDS.convert(
                        frameTimeNanos - lastFrameTimeNanos, TimeUnit.NANOSECONDS);
                //frame callback time more than threshold
                if (diffMs > TIME_BLOCK) {
                    dispatchEventCallback(EVENT_BLOCK, diffMs);
                }
                //frame smooth
                else {
                    dispatchEventCallback(EVENT_SMOOTH);
                }
            }
            lastFrameTimeNanos = frameTimeNanos;
        }
    }
}
