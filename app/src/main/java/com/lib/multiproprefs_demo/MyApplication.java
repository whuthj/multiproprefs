package com.lib.multiproprefs_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.lib.multiproprefs_demo.model.HelloPluginInfo;

import org.acdd.android.compat.ACDDApp;
import org.acdd.framework.InternalConstant;
import org.acdd.framework.PluginRemoveListener;

/**
 * Created by hujun on 2016/5/19.
 */
public class MyApplication extends ACDDApp {
    @Override
    protected boolean isPurgeUpdate() {
        String hostCode = lastHostVersionCode;
        String prevVersion = getLastHostVersionCode();

        if (TextUtils.equals(prevVersion, hostCode)) {
            return false;
        }

        setLastHostVersionCode(hostCode);
        return true;
    }

    public void onCreate() {
        super.onCreate();
    }

    protected PluginRemoveListener getPluginRemoveListener() {
        return new CustomPluginRemoveListener();
    }

    class CustomPluginRemoveListener implements PluginRemoveListener {
        @Override
        public boolean shouldRemoved(String pluginPkg) {
            if (HelloPluginInfo.getPluginInfo().getPkgName().equals(pluginPkg)) {
                return true;
            }
            return true;
        }
    }

    private static final String LAST_HOST_VERSION_CODE = "last_host_version_code";
    private static final String lastHostVersionCode = "1.3";

    private String getLastHostVersionCode() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(InternalConstant.ACDD_CONFIGURE, 0);
            return sharedPreferences.getString(LAST_HOST_VERSION_CODE, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setLastHostVersionCode(String lastHostVersionCode) {
        try {
            SharedPreferences.Editor edit = getSharedPreferences(InternalConstant.ACDD_CONFIGURE, Context.MODE_PRIVATE).edit();
            edit.putString(LAST_HOST_VERSION_CODE, lastHostVersionCode);
            edit.commit();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
