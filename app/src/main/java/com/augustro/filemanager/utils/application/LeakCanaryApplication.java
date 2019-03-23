package com.augustro.filemanager.utils.application;

import android.support.multidex.MultiDexApplication;


public class LeakCanaryApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
    }

}