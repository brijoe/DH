package io.github.brijoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.brijoe.tool.DeviceHelper;
import io.github.brijoe.R;
import io.github.brijoe.bean.NetworkInfo;


public class HttpDetailActivity extends Activity {

    private TextView date, url, code, latency, requestHeaders, responseHeaders, postData, response;

    private ImageView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_detail);
        init();
    }

    private void init() {
        final NetworkInfo networkLog = (NetworkInfo) getIntent().getSerializableExtra("networkLog");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        date = findViewById(R.id.date);
        url = findViewById(R.id.url);
        code = findViewById(R.id.code);
        status = findViewById(R.id.code_img);
        latency = findViewById(R.id.latency);
        requestHeaders = findViewById(R.id.tv_request_headers);
        responseHeaders = findViewById(R.id.headers);
        postData = findViewById(R.id.postData);
        response = findViewById(R.id.response);

        date.setText(dateFormat.format(new Date(networkLog.getDate())));
        url.setText("[" + networkLog.getRequestType() + "] " + networkLog.getUrl());
        code.setText(networkLog.getResponseCode());
        latency.setText(String.format("%sms", networkLog.getDuration().intValue()));

        if (!TextUtils.isEmpty(networkLog.getRequestHeaders()))
            requestHeaders.setText(networkLog.getRequestHeaders());

        if (!TextUtils.isEmpty(networkLog.getResponseHeaders()))
            responseHeaders.setText(networkLog.getResponseHeaders());

        if (!TextUtils.isEmpty(networkLog.getPostData()))
            postData.setText(networkLog.getPostData());
        if (!TextUtils.isEmpty(networkLog.getResponseData()))
            response.setText(DeviceHelper.formatJson(networkLog.getResponseData()));

        if (networkLog.getResponseCode().startsWith("2")) {
            status.setBackgroundColor(Color.GREEN);
            code.setTextColor(Color.GREEN);
        } else if (networkLog.getResponseCode().startsWith("4")) {
            status.setBackgroundColor(Color.parseColor("#ffa500"));
            code.setTextColor(Color.parseColor("#ffa500"));
        } else if (networkLog.getResponseCode().startsWith("5")) {
            status.setBackgroundColor(Color.RED);
            code.setTextColor(Color.RED);
        }

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
                sendIntent.putExtra(Intent.EXTRA_TEXT, networkLog.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_title)));
            }
        });

    }
}
