package io.github.brijoe.example;

import android.app.Application;

import io.github.brijoe.DH;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DH.install(this);
    }
}
