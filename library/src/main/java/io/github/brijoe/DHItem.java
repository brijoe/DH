package io.github.brijoe;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * 菜单项
 *
 * @author Bridge
 */

public class DHItem extends TextView {

    private ViewGroup.LayoutParams layoutParams =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

    private int padding = DHUtil.dip2px(getContext(), 10);

    private Context mContext;

    public DHItem(Context context) {
        this(context, null);
    }

    public DHItem(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DHItem(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    private void init() {
        setTextColor(Color.parseColor("#666666"));
        setLayoutParams(layoutParams);
        setPadding(padding, padding, padding, padding);
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        setBackground(mContext.getDrawable(R.drawable.dh_item_selector) );
    }
}
