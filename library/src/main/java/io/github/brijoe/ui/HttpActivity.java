package io.github.brijoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import io.github.brijoe.db.HttpRepository;
import io.github.brijoe.adapter.HttpLogAdapter;
import io.github.brijoe.R;
import io.github.brijoe.bean.NetworkInfo;

public  class HttpActivity extends DHBaseActivity {

    private ListView mListView;
    private HttpLogAdapter mAdapter;
    private HttpRepository mLogRepository;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        init();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.list_network_logs);

        mLogRepository = new HttpRepository(this);
        final List<NetworkInfo> networkLogs = mLogRepository.readAllLogs();
        mAdapter = new HttpLogAdapter(this,networkLogs);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HttpActivity.this
                        , HttpDetailActivity.class);
                intent.putExtra("networkLog", networkLogs.get(position));
                startActivity(intent);
            }
        });
        //返回
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //刷新
        findViewById(R.id.btnFlush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.flush(mLogRepository.readAllLogs());
            }
        });
        //清空
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogRepository.deleteAll();
                mAdapter.clear();
            }
        });

    }

}
