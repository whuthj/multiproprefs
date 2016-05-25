package com.hujun.helloplugin;

import android.app.Application;

import com.hujun.common.IPluginCommand;
import com.hujun.common.PluginCommand;

/**
 * Created by hujun on 2016/5/19.
 */
public class PluginApplication extends Application {
    public void onCreate() {
        super.onCreate();
        init();
    }

    public static void init() {
        PluginCommand.registerCommand(PluginCommand.CMD_HELLO, new MyPluginCommand());
        PluginCommand.Test = "FromPlugin";
    }
}
