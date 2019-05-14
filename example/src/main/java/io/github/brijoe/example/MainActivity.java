package io.github.brijoe.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.brijoe.example.adapter.EmployeeAdapter;
import io.github.brijoe.example.model.Employee;
import io.github.brijoe.example.model.EmployeeList;
import io.github.brijoe.example.network.ServiceFactory;
import io.github.brijoe.example.parse.HttpUrlHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    private EmployeeAdapter adapter;
    private Button btnSend, btnChange, btnBlock;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btnSend = (Button) findViewById(R.id.btn_send);
        btnChange = findViewById(R.id.btn_change);
        btnBlock = findViewById(R.id.btn_block);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_employee_list);
        btnSend.setOnClickListener(this);
        btnBlock.setOnClickListener(this);
        btnChange.setOnClickListener(this);

        GhostThread.start();
    }


    private void sendReq() {
        Call<EmployeeList> call = ServiceFactory.getBusService().getEmployeeData(100);
        call.enqueue(new Callback<EmployeeList>() {
            @Override
            public void onResponse(Call<EmployeeList> call, Response<EmployeeList> response) {
                if (response != null && response.body() != null)
                    generateEmployeeList(response.body().getEmployeeArrayList());
            }

            @Override
            public void onFailure(Call<EmployeeList> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "error occurred,please try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void generateEmployeeList(ArrayList<Employee> empDataList) {
        if (empDataList != null) {
            adapter = new EmployeeAdapter(empDataList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                sendReq();
                break;
            case R.id.btn_block:
                block();

            case R.id.btn_change:
                changeBaseUrl();
                break;
        }
    }

    private static final String TAG = "MainActivity";

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e(TAG, "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(ev);
    }

    private void block() {

//        int count=Integer.MAX_VALUE/100;
//        int  i=0;
//        while(i<count){
//            double result=Math.PI*Math.PI*Math.PI;
//            i++;
//        }

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void changeBaseUrl() {
        HttpUrlHelper.changeBaseUrl("http://change.navjacinth9.000webhostapp.com/json/");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GhostThread.stop();
    }
}
