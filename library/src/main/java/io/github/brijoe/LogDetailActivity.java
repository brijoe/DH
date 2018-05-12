package io.github.brijoe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * 查看网络详情Activity
 *
 * @Bridge
 */

public class LogDetailActivity extends Activity {

    private TextView date, url, code, latency, requestHeaders, responseHeaders, postData, response;

    private ImageView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);
        init();
    }

    private void init() {
        final NetworkLog networkLog = (NetworkLog) getIntent().getSerializableExtra("networkLog");
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
        requestHeaders.setText(networkLog.getRequestHeaders());
        responseHeaders.setText(networkLog.getResponseHeaders());
        postData.setText(networkLog.getPostData());
        response.setText(DHUtil.formatJson(networkLog.getResponseData()));
        response.setTextSize(15);
        postData.setText(networkLog.getPostData());

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
                startActivity(Intent.createChooser(sendIntent, "Share with"));
            }
        });

    }
}
