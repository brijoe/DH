package io.github.brijoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.brijoe.R;

/**
 * 调试窗口
 *
 * @author Bridge
 */

class DHDialog extends Dialog {

    private Context mContext;
    private View mRootView;

    private List<Debugger> mList = new ArrayList<>();

    private final String  TAG="DH";

    public DHDialog( Context context) {
        super(context, R.style.debugDialog);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_dh, null);
        initItems();
        renderItems();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mRootView);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initItems() {

        Log.d(TAG, "initItems: ");

        //内部预定义item
        mList.add(new Debugger("查看网络日志", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, LogActivity.class));
            }
        }));

        mList.add(new Debugger("应用设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHUtil.gotoAppSetting(mContext);
            }
        }));

        mList.add(new Debugger("系统语言设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHUtil.gotoLanguageSetting(mContext);
            }
        }));

        mList.add(new Debugger("开发者选项", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHUtil.gotoDevModeSetting(mContext);
            }
        }));
        mList.add(new Debugger("ToolBox", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ToolBoxActivity.class));
            }
        }));
        //获取附加的List
        mList.addAll(DH.getAppendList());

    }

    private void renderItems() {

        for (int i = 0; i < mList.size(); i++) {
            Debugger debugger = mList.get(i);
            DHItem item = new DHItem(mContext);
            item.setText(debugger.getItemName());
            item.setOnClickListener(debugger.getListener());
            ((LinearLayout) mRootView).addView(item);
        }

    }

}
