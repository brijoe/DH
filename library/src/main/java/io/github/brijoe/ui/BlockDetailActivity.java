package io.github.brijoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.brijoe.DH;
import io.github.brijoe.R;
import io.github.brijoe.bean.BlockInfo;
import io.github.brijoe.tool.DeviceHelper;


public class BlockDetailActivity extends DHBaseActivity {

    private TextView mStackTraces;

    private Pattern p = Pattern.compile(DeviceHelper.getAppPackageName(DH.getContext())+".+?\n");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_detail);
        init();
    }

    private void init() {
        final BlockInfo blockInfo = (BlockInfo) getIntent().getSerializableExtra("blockLog");
        mStackTraces = findViewById(R.id.tv_block_trace);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btn = findViewById(R.id.shareInfo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, blockInfo.getTraces());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_title)));
            }
        });

        SpannableString s = new SpannableString(blockInfo.getTraces());

        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        mStackTraces.setText(s);


    }
}
