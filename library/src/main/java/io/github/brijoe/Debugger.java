package io.github.brijoe;

import android.view.View;

/**
 * The {@code Debugger} class wraps a item of the dialog.
 *
 * @author Brijoe
 */
public final class Debugger implements Comparable<Debugger> {

    private int index;
    private String mItemName;
    private View.OnClickListener mListener;

    public Debugger(){}


    public Debugger(int index, String mItemName, View.OnClickListener mListener) {
        if(index<0)
            throw new IllegalArgumentException("index must not be less than 0");
        this.index = index;
        this.mItemName = mItemName;
        this.mListener = mListener;
    }



    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if(index<0)
            throw new IllegalArgumentException("index must not be less than 0");
        this.index = index;
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


    @Override
    public boolean equals(Object obj) {
        if(this==obj)
            return true;

        if(obj instanceof Debugger) {
            return this.index == ((Debugger) obj).getIndex();
        }
        return false;
    }


    @Override
    public int compareTo(Debugger o) {
        if(o==null)
            return -1;
        return index-o.getIndex();
    }
}