package io.github.brijoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import io.github.brijoe.R;
import io.github.brijoe.adapter.BlockLogAdapter;
import io.github.brijoe.bean.BlockInfo;
import io.github.brijoe.db.BlockRepository;

public class BlockActivity extends Activity {

    private ListView mListView;
    private BlockLogAdapter mAdapter;
    private BlockRepository mLogRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        init();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.list_block);
        mLogRepository = new BlockRepository(this);
        final List<BlockInfo> blockLogs = mLogRepository.readAllLogs();
        mAdapter = new BlockLogAdapter(this, blockLogs);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BlockActivity.this
                        , BlockDetailActivity.class);
                intent.putExtra("blockLog", blockLogs.get(position));
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
