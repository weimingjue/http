package com.wang.http.base;

import android.app.Application;

/**
 * Created by W on 2016/8/11.
 */
public class MApp extends Application {
    public static MApp mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

}
