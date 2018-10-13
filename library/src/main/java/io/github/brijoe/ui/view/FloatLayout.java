package io.github.brijoe.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.github.brijoe.R;
import io.github.brijoe.bean.RealWatchInfo;
import io.github.brijoe.monitor.MonitorManager;

public class FloatLayout extends FrameLayout {


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWmParams;
    private float mTouchStartX;
    private float mTouchStartY;
    private View mContent;
    private Context mContext;

    private TextView mTvClose, mTvFps, mTvCpu, mTvMemTotal, mTvMemDalvik, mTvMemNative, mTvMemOther, mTvThreadCount;

    private ListView mLvThreads;

    private ArrayAdapter<String> mThreadsAdapter;

    public FloatLayout(Context context) {
        this(context, null);
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mContent = LayoutInflater.from(mContext).inflate(R.layout.window_monitor, this);
        mTvClose = mContent.findViewById(R.id.tv_close);

        mTvFps = mContent.findViewById(R.id.tv_fps);
        mTvCpu = mContent.findViewById(R.id.tv_cpu);
        mTvMemTotal = mContent.findViewById(R.id.tv_mem_total);
        mTvMemDalvik = mContent.findViewById(R.id.tv_mem_dalvik);
        mTvMemNative = mContent.findViewById(R.id.tv_mem_native);
        mTvMemOther = mContent.findViewById(R.id.tv_mem_other);
        mTvThreadCount = mContent.findViewById(R.id.tv_thread_count);


        mLvThreads = mContent.findViewById(R.id.lv_threads);
        mThreadsAdapter = new ArrayAdapter<String>(mContext, R.layout.item_thread);
        mLvThreads.setAdapter(mThreadsAdapter);


        mTvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MonitorManager.getInstance().removeWindow();
            }
        });

    }

    public void showData(RealWatchInfo info) {
        if (info == null)
            return;
        mTvFps.setText(info.getFps());
        mTvCpu.setText(info.getCpu());
        mTvMemTotal.setText(info.getMem()[0]);
        mTvMemDalvik.setText(info.getMem()[1]);
        mTvMemNative.setText(info.getMem()[2]);
        mTvMemOther.setText(info.getMem()[3]);

        List<String> threadInfo = info.getThreadInfo();
        if (threadInfo != null) {
            mTvThreadCount.setText(String.format("(%d)", info.getThreadInfo().size()));
            mThreadsAdapter.clear();
            mThreadsAdapter.addAll(info.getThreadInfo());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if ((Math.abs(mTouchStartX - mMoveStartX) > 3)
                        && (Math.abs(mTouchStartY - mMoveStartY) > 3)) {
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }
                break;
        }
        return true;
    }

    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }
}