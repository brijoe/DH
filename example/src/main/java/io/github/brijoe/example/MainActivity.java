package io.github.brijoe.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.brijoe.example.adapter.EmployeeAdapter;
import io.github.brijoe.example.model.Employee;
import io.github.brijoe.example.model.EmployeeList;
import io.github.brijoe.example.network.ServiceFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    private EmployeeAdapter adapter;
    private Button btnSend;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btnSend = (Button) findViewById(R.id.btnSend);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_employee_list);
        btnSend.setOnClickListener(this);
    }


    private void sendReq() {
        Call<EmployeeList> call = ServiceFactory.getBusService().getEmployeeData(100);
        call.enqueue(new Callback<EmployeeList>() {
            @Override
            public void onResponse(Call<EmployeeList> call, Response<EmployeeList> response) {
                generateEmployeeList(response.body().getEmployeeArrayList());
            }

            @Override
            public void onFailure(Call<EmployeeList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error occurred,please try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void generateEmployeeList(ArrayList<Employee> empDataList) {

        adapter = new EmployeeAdapter(empDataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                sendReq();
                break;
        }
    }
}
