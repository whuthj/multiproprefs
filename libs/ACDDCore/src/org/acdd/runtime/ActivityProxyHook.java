package org.acdd.runtime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import org.acdd.android.compat.ActivityProxy;
import org.acdd.android.compat.ICrashReporter;
import org.acdd.framework.ACDD;

import java.io.File;

/**
 * 尝试在Activity找不到时,自动查找没有声明的Activity,
 * 以实现Host对Plugin的代理.
 */
public class ActivityProxyHook {
    private static final String TAG = "ActivityProxyHook";

    public static final String ACDD_PROXY_CLASS = "_plugin_class_name_";
    public static final String ACDD_PROXY_MODULE = "_plugin_module_name_";
    //"org.acdd.android.compat.ActivityProxy"
    private static final String ACDD_DEFAULT_STUB_CLASS = ActivityProxy.class.getName();

    private static PackageLite parse = null;
    private static String packageName, mComponentName;

    private static boolean parseTargetIntent(Context context, Intent intent) {
        packageName = null;
        mComponentName = null;

        if (intent.getComponent() != null) {
            packageName = intent.getComponent().getPackageName();
            mComponentName = intent.getComponent().getClassName();
        } else {
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
            if (resolveActivity == null || resolveActivity.activityInfo == null) {
            } else {
                packageName = resolveActivity.activityInfo.packageName;
                mComponentName = resolveActivity.activityInfo.name;
            }
        }
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(mComponentName)) {
            return false;
        }
        return true;
    }

    public static void forwardActivity2StubProxy(Context context, Intent intent) {
        if (!parseTargetIntent(context, intent)) {
            Log.d(TAG, "parseTargetIntent failed ...");
            return;
        }

        String proxyModule = lookupNotDeclaredActivity();
        if (TextUtils.isEmpty(proxyModule)) {
            Log.d(TAG, "lookupNotDeclaredActivity failed ...");
            return;
        }

        intent.putExtra(ACDD_PROXY_CLASS, mComponentName);
        intent.putExtra(ACDD_PROXY_MODULE, proxyModule);
        intent.setComponent(new ComponentName(packageName, ACDD_DEFAULT_STUB_CLASS));
    }

    private static String lookupNotDeclaredActivity() {
        try {
            if (isRequirePackageInfo(parse)) {
                parse = PackageLite.parse(
                        new File(RuntimeVariables.androidApplication.getPackageResourcePath()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isRequirePackageInfo(parse)
                || parse.components.contains(mComponentName)) {
            return null;
        }
        /*NOTE:找于查找问题,后面会去掉*/
        reportProxyError();
        return DelegateComponent.locateComponent(mComponentName);
    }

    private static boolean isRequirePackageInfo(PackageLite parse) {
        return parse == null || parse.components.size() <= 0;
    }

    private static void reportProxyError() {
        String errorComponent = "com.ijinshan.screensavernew.ScreenSaver2Activity";
        if (errorComponent.equalsIgnoreCase(mComponentName)) {
            String detailCause;
            if (parse == null) {
                detailCause = "package info as null, please check!";
            } else {
                detailCause = "Throwable: package info activities size: " + parse.components.size();
            }
            ACDD.getInstance().reportCrash(
                    ICrashReporter.ACDD_ACTIVITY_PROXY_ERROR, new Throwable(detailCause));
        }
    }
}
