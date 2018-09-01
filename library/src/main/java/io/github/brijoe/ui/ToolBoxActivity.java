package io.github.brijoe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.github.brijoe.bean.DHInfo;
import io.github.brijoe.R;


public class ToolBoxActivity extends Activity {


    private TextView mTvPageName, mTvPageIntent;
    private TextView mTvPkgName, mTvPkgVersion, mTvSignMd5, mTvSignSha1, mTvSignSha256;

    private TextView mTvProcId,mTvProcMemory;

    private TextView mTvOSversion, mTvModel, mTvResolution, mTvDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbox);
        init();
    }

    private void init() {
        mTvPageName = findViewById(R.id.tvPageName);
        mTvPageIntent = findViewById(R.id.tvPageIntent);
        mTvPkgName = findViewById(R.id.tvPkgName);
        mTvPkgVersion = findViewById(R.id.tvAppVersion);
        mTvSignMd5 = findViewById(R.id.tvSignMd5);
        mTvSignSha1 = findViewById(R.id.tvSignSha1);
        mTvSignSha256 = findViewById(R.id.tvSignSha256);

        mTvProcId=findViewById(R.id.tvProcId);
        mTvProcMemory=findViewById(R.id.tvProcMemory);

        mTvOSversion = findViewById(R.id.tvOSversion);
        mTvModel = findViewById(R.id.tvModel);
        mTvResolution = findViewById(R.id.tvResolution);
        mTvDensity = findViewById(R.id.tvDensity);

        mTvPkgVersion.setText( DHInfo.getsAppInfo().getVersion());
        mTvPageName.setText( DHInfo.getsPageInfo().getPageName());
        mTvPageIntent.setText( DHInfo.getsPageInfo().getIntentStr());
        mTvPkgName.setText(DHInfo.getsAppInfo().getPkgName());
        mTvSignMd5.setText(DHInfo.getsAppInfo().getSignMd5());
        mTvSignSha1.setText( DHInfo.getsAppInfo().getSignSha1());
        mTvSignSha256.setText(DHInfo.getsAppInfo().getSignSha256());

        mTvProcId.setText(DHInfo.getsProcessInfo().getProName());
        mTvProcMemory.setText(DHInfo.getsProcessInfo().getFreeMemory());

        mTvOSversion.setText(DHInfo.getsDeviceInfo().getOsVersion());
        mTvModel.setText(DHInfo.getsDeviceInfo().getModel());
        mTvResolution.setText(DHInfo.getsDeviceInfo().getResolution());
        mTvDensity.setText( DHInfo.getsDeviceInfo().getDensity());

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
