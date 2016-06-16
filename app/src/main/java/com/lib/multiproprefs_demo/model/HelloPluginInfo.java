package com.lib.multiproprefs_demo.model;

import android.os.Environment;

import com.lib.multiproprefs_demo.vo.PluginInfo;

/**
 * Created by hujun on 2016/6/16.
 */
public class HelloPluginInfo {
    private static PluginInfo mPluginInfo = null;

    public static PluginInfo getPluginInfo() {
        if (mPluginInfo == null) {
            mPluginInfo = new PluginInfo();
            mPluginInfo.setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/helloplugin.so");
            mPluginInfo.setPkgName("com.hujun.helloplugin");
            mPluginInfo.setAppClzz("com.hujun.helloplugin.PluginApplication");
        }
        return mPluginInfo;
    }
}
