package com.zhuzhu.allen.rxretrofitlib;

import android.app.Application;

/**
 * 全局Application
 * Created by allen on 2017/8/23.
 */

public class RxRetrofitApp {
    /*Application对象*/
    private static Application application;
    /*设置是否是调试模式*/
    private static boolean debug;

    /*构造函数一*/
    public static void init(Application app){
        setApplication(app);
    }

    /*构造函数二*/
    public static void init(Application app,boolean debug){
        setApplication(app);
        setDebug(debug);
    }

    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        RxRetrofitApp.application = application;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        RxRetrofitApp.debug = debug;
    }
}
