package io.github.brijoe;

import android.view.View;

public final class Debugger {
    private String mItemName;
    private View.OnClickListener mListener;

    public Debugger( String itemName, View.OnClickListener listener) {
        this.mItemName = itemName;
        this.mListener = listener;
    }


    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public View.OnClickListener getListener() {
        return mListener;
    }

    public void setListener(View.OnClickListener mListener) {
        this.mListener = mListener;
    }
}