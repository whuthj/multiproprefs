package com.lib.multiproprefs_demo;

import org.acdd.android.compat.ACDDApp;

/**
 * Created by hujun on 2016/5/19.
 */
public class MyApplication extends ACDDApp {
    @Override
    protected boolean isPurgeUpdate() {
        return false;
    }

    public void onCreate() {
        super.onCreate();
    }
}
