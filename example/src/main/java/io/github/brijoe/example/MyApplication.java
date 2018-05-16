package io.github.brijoe.example;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

import io.github.brijoe.DH;
import io.github.brijoe.Debugger;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // you must call install(context) using Application context.
        DH.install(this);
        //you can write your configuration anywhere.
        configDH();
    }

    private void configDH(){

        DH.addDebugger(new Debugger(0,"My Item0", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyApplication.this,"you click my item",Toast.LENGTH_SHORT).show();
            }
        }));
        DH.addDebugger(new Debugger(1,"My Item1", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyApplication.this,"you click my item",Toast.LENGTH_SHORT).show();
            }
        }));
        DH.addDebugger(new Debugger(1,"My Item1Repeat", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyApplication.this,"you click my item",Toast.LENGTH_SHORT).show();
            }
        }));

        DH.addDebugger(new Debugger(2,"My Item2", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyApplication.this,"you click my item",Toast.LENGTH_SHORT).show();
            }
        }));

        DH.addDebugger(new Debugger(3,"My Item3", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyApplication.this,"you click my item",Toast.LENGTH_SHORT).show();
            }
        }));

    }
}
