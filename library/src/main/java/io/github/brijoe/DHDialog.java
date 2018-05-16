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

    private final int INDEX=0x01;

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
        mTvTitle.setText(String.format(mContext.getString(R.string.dialog_title),BuildConfig.VERSION_NAME));
        initItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mRootView);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initItems() {

        LinearLayout parent= ((LinearLayout) mRootView);
        //inner items
        mList.add(new Debugger(INDEX,mContext.getString(R.string.net_log), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, LogActivity.class));
            }
        }));

        mList.add(new Debugger(INDEX+1,mContext.getString(R.string.app_info), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHTool.gotoAppSetting(mContext);
            }
        }));

        mList.add(new Debugger(INDEX+2,mContext.getString(R.string.language_pref), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHTool.gotoLanguageSetting(mContext);
            }
        }));

        mList.add(new Debugger(INDEX+3,mContext.getString(R.string.developer_options), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DHTool.gotoDevModeSetting(mContext);
            }
        }));
        mList.add(new Debugger(INDEX+4,mContext.getString(R.string.tool_box), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ToolBoxActivity.class));
            }
        }));
        //render inner items
        for (int i = 0; i < mList.size(); i++) {
            Debugger debugger = mList.get(i);
            DHItem item = new DHItem(mContext);
            item.setText(debugger.getItemName());
            item.setOnClickListener(debugger.getListener());
            parent.addView(item,debugger.getIndex());
        }


        // remove duplicate items
        List<Debugger> appendList=DH.getAppendList();
        List<Debugger> finalList=new ArrayList<>();
        for(int i=0;i<appendList.size();i++){
            if(!finalList.contains(appendList.get(i))) {
                finalList.add(appendList.get(i));
            }
        }
        // render outer items
        for(int i=0;i<finalList.size();i++){
            Debugger debugger = finalList.get(i);
            DHItem item = new DHItem(mContext);
            item.setText(debugger.getItemName());
            item.setBackground(mContext.getResources().getDrawable(R.drawable.dh_item_outer_selector));
            item.setOnClickListener(debugger.getListener());
            //debugger have value set
            if(debugger.getIndex()==Integer.MIN_VALUE){
               int index=parent.getChildCount();
                parent.addView(item, index+1);
            }
            else {
                parent.addView(item, debugger.getIndex() + INDEX + 5);
            }
        }



    }


}
