package io.github.brijoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public  class LogActivity extends Activity {

    private ListView mListView;
    private NetworkLogAdapter mAdapter;
    private LogRepository mLogRepository;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        init();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.list_network_logs);

        mLogRepository = new LogRepository(this);
        final List<NetworkLog> networkLogs = mLogRepository.readAllLogs();
        mAdapter = new NetworkLogAdapter(this,networkLogs);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LogActivity.this
                        , LogDetailActivity.class);
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
