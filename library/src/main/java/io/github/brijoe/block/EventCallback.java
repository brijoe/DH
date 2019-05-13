package io.github.brijoe.block;

interface EventCallback {
    void onFrameStart(long frameTimeNanos);

    void onFrameBlock(long frameDiff);

    void onFrameSmooth();
}