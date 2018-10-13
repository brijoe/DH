package io.github.brijoe.monitor;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import io.github.brijoe.bean.RealWatchInfo;
import io.github.brijoe.ui.view.FloatLayout;


/**
 * MonitorManager
 */
public class MonitorManager {

    private FloatLayout mFloatLayout;

    private WindowManager mWindowManger;

    private boolean isAdd;

    private StateSampler mSampler = StateSampler.getInstance();


    private Handler handler = new Handler(Looper.getMainLooper());


    private MonitorManager() {
    }

    public static MonitorManager getInstance() {
        return Holder.mInstance;
    }

    private static class Holder {
        static MonitorManager mInstance = new MonitorManager();

    }


    public void updateData(final RealWatchInfo info) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mFloatLayout != null) {
                    mFloatLayout.showData(info);
                }
            }
        });

    }


    public void addWindow(Context context) {
        if (isAdd)
            return;
        mWindowManger = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        mFloatLayout = new FloatLayout(context);
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManger.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = 100;

        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        mFloatLayout.setParams(wmParams);
        mWindowManger.addView(mFloatLayout, wmParams);
        isAdd = true;

        mSampler.init(context, 1000);
        mSampler.start();
        FpsCalculator.instance().start();


    }

    public void removeWindow() {
        if (isAdd) {
            if (mFloatLayout != null && mWindowManger != null)
                mWindowManger.removeView(mFloatLayout);
            isAdd = false;
            if (mSampler != null)
                mSampler.stop();
        }

    }
}
