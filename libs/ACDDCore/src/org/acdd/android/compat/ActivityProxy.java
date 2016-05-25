package org.acdd.android.compat;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.app.Instrumentation;
import android.view.Window;
import android.view.WindowManager;

import com.android.internal.app.IVoiceInteractor;

import org.acdd.framework.ACDD;
import org.acdd.hack.ACDDHacks;
import org.acdd.hack.AndroidHack;
import org.acdd.runtime.ActivityProxyHook;
import org.acdd.runtime.ContextImplHook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 插件代理Activity, 在插件模块的Activity在宿主没有注册时,
 * 可以通过此Activity代理启动,仅支持lunchMode:standard.
 *
 * 注意:
 * 1 super.OnXxx()的调用时机一定要在
 * Instrumentation.callActivityOnXxx()的后面调用
 *
 * 2 PluginStubActivity 要声明为android:launchMode="standard"
 */
public class ActivityProxy extends Activity {
    private static final String TAG = "ActivityProxy";

    private String mClassName;
    private ClassLoader mClassLoader;
    /* 代理的插件activity */
    private Activity mPluginActivity;
    /* 通过此对象调用插件Activity的生命周期,比如onCreate等 */
    private Instrumentation mInstrumentation;

    private Field mToken, mIdent, mLastNonConfigurationInstances
            , mCurrentConfig, mEmbeddedID, mActivityInfo, mReferrer, mVoiceInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*如果不成功,要结束此activity*/
        boolean proxySuccess = true;
        Intent intent = getIntent();

        try {
            mInstrumentation = AndroidHack.getInstrumentation();
        } catch (Exception e) {
            e.printStackTrace();
            proxySuccess = false;
        }

        /*创建代理Activity*/
        if (intent != null) {
            mPluginActivity = createProxyActivity(intent);
        }
        else {
            proxySuccess = false;
            Log.e(TAG, "createProxyActivity failed ...");
        }

        /*调用插件onCreate生命周期*/
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnCreate(mPluginActivity, savedInstanceState);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        else {
            proxySuccess = false;
            Log.e(TAG, "mPluginActivity empty, please check ...");
        }

        super.onCreate(savedInstanceState);
        if (!proxySuccess) {
            handleException(null);/*结束此activity,以防止黑屏出现*/
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnNewIntent(mPluginActivity, intent);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnStart(mPluginActivity);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnRestart(mPluginActivity);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnResume(mPluginActivity);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnPause(mPluginActivity);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mPluginActivity != null) {
            try {
                mInstrumentation.callActivityOnStop(mPluginActivity);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mPluginActivity != null) {
/*            try {
                Method incrementExpectedActivityCount = StrictMode.class.getDeclaredMethod("decrementExpectedActivityCount", Class.class);
                incrementExpectedActivityCount.setAccessible(true);
                incrementExpectedActivityCount.invoke(null, mClassLoader.loadClass(mClassName));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }*/
            try {
                mInstrumentation.callActivityOnDestroy(mPluginActivity);
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        super.onDestroy();
    }

    private void handleException(Exception e) {
        if (e != null) {
            ACDD.getInstance().reportCrash(
                    ICrashReporter.ACDD_ACTIVITY_PROXY_ERROR, e.getCause());
            e.printStackTrace();
        }
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            mInstrumentation.callActivityOnSaveInstanceState(mPluginActivity, outState);
        }
        catch (Exception e) {
            handleException(e);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            mInstrumentation.callActivityOnRestoreInstanceState(mPluginActivity, savedInstanceState);
        }
        catch (Exception e) {
            handleException(e);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private Activity createProxyActivity(Intent customIntent) {
        Activity activity;

        try {
            String pluginName = customIntent.getStringExtra(ActivityProxyHook.ACDD_PROXY_MODULE);
            mClassName = customIntent.getStringExtra(ActivityProxyHook.ACDD_PROXY_CLASS);
            if (TextUtils.isEmpty(pluginName) || TextUtils.isEmpty(mClassName)) {
                return null;
            }

            mClassLoader = ACDD.getInstance().getBundleClassLoader(pluginName);
            if (mClassLoader == null) {
                return null;
            }
        }
        catch (Exception e) {
            /*
             * 防止类似以下异常出现
             * Caused by: java.lang.RuntimeException: Parcelable encounteredClassNotFoundException
             * reading a Serializable object (name = com.ijinshan.common.kinfoc_sjk.MapPath)
             at android.os.Parcel.readSerializable(Parcel.java:2226)
             at android.os.Parcel.readValue(Parcel.java:2071)
             at android.os.Parcel.readArrayMapInternal(Parcel.java:2321)
             at android.os.Bundle.unparcel(Bundle.java:249)
             at android.os.Bundle.getString(Bundle.java:1118)
             at android.content.Intent.getStringExtra(Intent.java:4859)
             at org.acdd.android.compat.ActivityProxy.createProxyActivity(ActivityProxy.java:169)
             at org.acdd.android.compat.ActivityProxy.onCreate(ActivityProxy.java:68)
             at android.app.Activity.performCreate(Activity.java:5264)
             at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1088)
             at org.acdd.runtime.k.callActivityOnCreate(InstrumentationHook.java:533)
             ... 13 more
             */
            e.printStackTrace();
            return null;
        }
        try {
            activity = mInstrumentation.newActivity(mClassLoader, mClassName, customIntent);
            customIntent.setExtrasClassLoader(mClassLoader);

            ContextImplHook contextImplHook = (ContextImplHook) ACDDHacks.ContextWrapper_mBase.get(this);
            contextImplHook.setClassLoader(mClassLoader);

/*            Method incrementExpectedActivityCount = StrictMode.class.getDeclaredMethod("incrementExpectedActivityCount", Class.class);
            incrementExpectedActivityCount.setAccessible(true);
            incrementExpectedActivityCount.invoke(null, classLoader.loadClass(mClassName));*/

            Method attachMethod = reflectAttachMethod();
            invokeAttachMethod(activity, attachMethod, customIntent);

            /*绑定Window,以使插件窗口能刷新*/
            Window window = getWindow();
            WindowManager windowManager = getWindowManager();
            Field windowField = Activity.class.getDeclaredField("mWindow");
            windowField.setAccessible(true);
            windowField.set(activity, window);

            Field windowManagerField = Activity.class.getDeclaredField("mWindowManager");
            windowManagerField.setAccessible(true);
            windowManagerField.set(activity, windowManager);
            return activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  final void attach(Context context, ActivityThread aThread,
     Instrumentation instr, IBinder token, int ident,
     Application application, Intent intent, ActivityInfo info,
     CharSequence title, Activity parent, String id,
     NonConfigurationInstances lastNonConfigurationInstances,
     Configuration config, String referrer, IVoiceInteractor voiceInteractor)
     */
    private Method reflectAttachMethod() throws NoSuchMethodException {
        Method attachMethod;

        Class<?> NonConfigurationInstances = null;
        try {
            NonConfigurationInstances = Class.forName("android.app.Activity$NonConfigurationInstances");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT == 21) {
            attachMethod = Activity.class.getDeclaredMethod("attach", Context.class, ActivityThread.class,
                    Instrumentation.class, IBinder.class, int.class, Application.class, Intent.class, ActivityInfo.class,
                    CharSequence.class, Activity.class, String.class, NonConfigurationInstances, Configuration.class, IVoiceInteractor.class);
        }
        else if (Build.VERSION.SDK_INT >= 22) {
            attachMethod = Activity.class.getDeclaredMethod("attach", Context.class, ActivityThread.class,
                    Instrumentation.class, IBinder.class, int.class, Application.class, Intent.class, ActivityInfo.class,
                    CharSequence.class, Activity.class, String.class, NonConfigurationInstances, Configuration.class, String.class, IVoiceInteractor.class);
        }
        else {
            attachMethod = Activity.class.getDeclaredMethod("attach", Context.class, ActivityThread.class,
                    Instrumentation.class, IBinder.class, int.class, Application.class, Intent.class, ActivityInfo.class,
                    CharSequence.class, Activity.class, String.class, NonConfigurationInstances, Configuration.class);
        }

        attachMethod.setAccessible(true);
        return attachMethod;
    }

    private void reflectActivityField() throws NoSuchFieldException {
        mToken = Activity.class.getDeclaredField("mToken");
        mToken.setAccessible(true);

        mIdent  = Activity.class.getDeclaredField("mIdent");
        mIdent.setAccessible(true);

        mLastNonConfigurationInstances = Activity.class.getDeclaredField("mLastNonConfigurationInstances");
        mLastNonConfigurationInstances.setAccessible(true);

        mCurrentConfig = Activity.class.getDeclaredField("mCurrentConfig");
        mCurrentConfig.setAccessible(true);

        mEmbeddedID = Activity.class.getDeclaredField("mEmbeddedID");
        mEmbeddedID.setAccessible(true);

        mActivityInfo = Activity.class.getDeclaredField("mActivityInfo");
        mActivityInfo.setAccessible(true);

        if (Build.VERSION.SDK_INT >= 22) {
            mReferrer = Activity.class.getDeclaredField("mReferrer");
            mReferrer.setAccessible(true);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mVoiceInteractor = Activity.class.getDeclaredField("mVoiceInteractor");
            mVoiceInteractor.setAccessible(true);
        }
    }

    private void invokeAttachMethod(Activity activity, Method attachMethod, Intent intent) throws Exception {
        /*要首先调用此函数*/
        reflectActivityField();

        ActivityInfo activityInfo = (ActivityInfo)mActivityInfo.get(this);
        activityInfo.name = mClassName;
        IBinder token = (IBinder) mToken.get(this);
        int ident = (Integer) mIdent.get(this);
        String embeddedID = (String) mEmbeddedID.get(this);
        CharSequence title = getTitle();
        Activity parent = getParent();

        if (Build.VERSION.SDK_INT == 21) {
            attachMethod.invoke(activity, getBaseContext(), AndroidHack.getActivityThread(), mInstrumentation,
                    token, ident, getApplication(), intent, activityInfo, title, parent, embeddedID,
                    mLastNonConfigurationInstances.get(this), mCurrentConfig.get(this), mVoiceInteractor.get(this));
        }
        else if (Build.VERSION.SDK_INT >= 22) {
            attachMethod.invoke(activity, getBaseContext(), AndroidHack.getActivityThread(), mInstrumentation,
                    token, ident, getApplication(), intent, activityInfo, title, parent, embeddedID,
                    mLastNonConfigurationInstances.get(this), mCurrentConfig.get(this), mReferrer.get(this), mVoiceInteractor.get(this));
        }
        else {
            attachMethod.invoke(activity, getBaseContext(), AndroidHack.getActivityThread(), mInstrumentation,
                    token, ident, getApplication(), intent, activityInfo, title, parent, embeddedID,
                    mLastNonConfigurationInstances.get(this), mCurrentConfig.get(this));
        }
    }
}
