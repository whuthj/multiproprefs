package com.hujun.common;

import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by hujun on 2016/5/19.
 */
public class PluginCommand {
    public static final int CMD_HELLO = 123;
    public static String Test = "Failed";
    private static HashMap<Integer, IPluginCommand> sCommanderMap = new HashMap<>();
    private static IPluginCommand sCmd = null;

    public static synchronized void registerCommand(int id, IPluginCommand cmd) {
        sCommanderMap.put(id, cmd);
        sCmd = cmd;
    }

    public static synchronized IPluginCommand getCommand(int id) {
        try {
            sCmd.invoke(123);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        IPluginCommand cmd = sCommanderMap.get(id);
        return sCmd;
    }
}
