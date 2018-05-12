package io.github.brijoe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

 class CopyText extends TextView {

    private int padding = DHTool.dip2px(getContext(), 5);

    public CopyText(Context context) {
        this(context, null);
    }

    public CopyText(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CopyText(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setBackgroundColor(Color.parseColor("#60ffffff"));
        setPadding(padding, padding, padding, padding);
        setTextColor(Color.parseColor("#ffffff"));
        setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(getText())) {
                    ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(getText(), getText());
                    //添加ClipData对象到剪切板中
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), getContext().getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.copy_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
