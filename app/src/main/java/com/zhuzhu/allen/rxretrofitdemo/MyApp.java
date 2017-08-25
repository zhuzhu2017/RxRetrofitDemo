package com.zhuzhu.allen.rxretrofitdemo;

import android.app.Application;
import android.content.Context;

import com.zhuzhu.allen.rxretrofitlib.BuildConfig;
import com.zhuzhu.allen.rxretrofitlib.RxRetrofitApp;

/**
 * Created by allen on 2017/8/24.
 */

public class MyApp extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        RxRetrofitApp.init(this, BuildConfig.DEBUG);
    }
}
