package io.github.brijoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class DHDialog extends Dialog {

    private Context mContext;
    private View mRootView;
    private TextView mTvTitle;

    private List<Debugger> mList = new ArrayList<>();

    private final String  TAG="DH";

    public DHDialog( Context context) {
        super(context, R.style.debugDialog);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_dh, null);
        mTvTitle=(TextView) mRootView.findViewById(R.id.tvTitle);
        mTvTitle.setText(mContext.getString(R.string.dialog_title));
        initItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mRootView);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initItems() {
        //inner items
        mList.add(new Debugger(mContext.getString(R.string.net_log), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, LogActivity.class));
            }
        }));

        mList.add(new Debugger(mContext.getString(R.string.app_info), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHTool.gotoAppSetting(mContext);
            }
        }));

        mList.add(new Debugger(mContext.getString(R.string.language_pref), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHTool.gotoLanguageSetting(mContext);
            }
        }));

        mList.add(new Debugger(mContext.getString(R.string.developer_options), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHTool.gotoDevModeSetting(mContext);
            }
        }));
        mList.add(new Debugger(mContext.getString(R.string.tool_box), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ToolBoxActivity.class));
            }
        }));
        //outer items
        mList.addAll(DH.getAppendList());

        //render items
        for (int i = 0; i < mList.size(); i++) {
            Debugger debugger = mList.get(i);
            DHItem item = new DHItem(mContext);
            item.setText(debugger.getItemName());
            item.setOnClickListener(debugger.getListener());
            ((LinearLayout) mRootView).addView(item);
        }

    }


}
