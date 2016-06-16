/*
 * ACDD Project
 * file AndroidHack.java  is  part of ACCD
 * The MIT License (MIT)  Copyright (c) 2015 Bunny Blue,achellies.
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */
package org.acdd.hack;

import android.app.Application;
import android.app.Instrumentation;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.acdd.android.compat.ThrowableCustomHandler;
import org.acdd.bundleInfo.BundleInfoList;
import org.acdd.framework.ACDD;
import org.acdd.framework.Framework;
import org.acdd.android.compat.ICrashReporter;
import org.acdd.hack.Hack.HackDeclaration.HackAssertionException;
import org.acdd.log.Logger;
import org.acdd.log.LoggerFactory;
import org.acdd.runtime.ClassLoadFromBundle;
import org.acdd.runtime.DelegateClassLoader;
import org.acdd.runtime.DelegateResources;
import org.acdd.runtime.RuntimeVariables;
import org.osgi.framework.Bundle;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/****
 * Hack Android ActivityThread
 ***/
public class AndroidHack {
    private static Object _mLoadedApk;
    private static Object _sActivityThread;
    private static HandlerHack sHandlerHack;
    public static final int STOP_ACTIVITY_SHOW;
    public static final int STOP_ACTIVITY_HIDE;
    public static final int RECEIVER;
    public static final int CREATE_SERVICE;
    public static final int GC_WHEN_IDLE;
    public static final int LAUNCH_ACTIVITY;
    public static final int DESTROY_ACTIVITY;
	
    static Logger logger= LoggerFactory.getInstance("AndroidHack");
	
    static {
        Class clazz = null;
        try {
            clazz = Class.forName("android.app.ActivityThread$H");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int id;
        id = getHMember(clazz, "STOP_ACTIVITY_SHOW");
        STOP_ACTIVITY_SHOW = (id >= 0) ? id : 103;
        id = getHMember(clazz, "STOP_ACTIVITY_HIDE");
        STOP_ACTIVITY_HIDE = (id >= 0) ? id : 104;
        id = getHMember(clazz, "RECEIVER");
        RECEIVER = (id >= 0) ? id : 113;
        id = getHMember(clazz, "CREATE_SERVICE");
        CREATE_SERVICE = (id >= 0) ? id : 114;
        id = getHMember(clazz, "GC_WHEN_IDLE");
        GC_WHEN_IDLE = (id >= 0) ? id : 120;
        id = getHMember(clazz, "LAUNCH_ACTIVITY");
        LAUNCH_ACTIVITY = (id >= 0) ? id : 100;
        id = getHMember(clazz, "DESTROY_ACTIVITY");
        DESTROY_ACTIVITY = (id >= 0) ? id : 109;
    }

    static int getHMember(Class clazz, String name) {
        int ret = -1;
        if (clazz == null) {
            return ret;
        }
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            ret = field.getInt(null);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return ret;
    }

    static final class HandlerHack implements Callback {
        final Object activityThread;
        final Handler handler;

        HandlerHack(Handler handler, Object obj) {
            this.handler = handler;
            this.activityThread = obj;
        }

        @Override
        public boolean handleMessage(Message message) {
            try {
                AndroidHack.ensureLoadedApk();
                if (message.what == LAUNCH_ACTIVITY) {
                    Field intentField = message.obj.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent)intentField.get(message.obj);
                    String component = intent.getComponent().getClassName();
                    String bundleName = BundleInfoList.getInstance().getBundleNameForComponet(component);
                    if (bundleName != null) {
                        Bundle bundle = Framework.getBundle(bundleName);
                        if (bundle == null) {
                            bundle = Framework.tryLoadBundleInstance(bundleName);
                            if (bundle == null) {
                                ClassLoadFromBundle.checkInstallBundleIfNeed(component);
                                bundle = Framework.getBundle(bundleName);
                            }
                        }
                        if (bundle != null) {
                            bundle.start();
                        }
                    }
                } else if (message.what == CREATE_SERVICE) {
                    try {
                        Field infoField = message.obj.getClass().getDeclaredField("info");
                        infoField.setAccessible(true);
                        ServiceInfo info = (ServiceInfo) infoField.get(message.obj);

                        String name = info.name;
                        String bundleName = BundleInfoList.getInstance().getBundleNameForComponet(name);
                        if (bundleName != null) {
                            Bundle bundle = Framework.getBundle(bundleName);
                            if (bundle == null) {
                                bundle = Framework.tryLoadBundleInstance(bundleName);
                                if (bundle == null) {
                                    ClassLoadFromBundle.checkInstallBundleIfNeed(name);
                                    bundle = Framework.getBundle(bundleName);
                                }
                            }
                            if (bundle != null) {
                                bundle.start();
                            } else {
                                return true;
                            }
                        }
                    } catch (Throwable throwable) {
                        ACDD.getInstance().reportCrash(ICrashReporter.ACDD_CREATE_SERVICE_ERROR, throwable);
                        throwable.printStackTrace();
                    }
                } else if (message.what == RECEIVER) {
                    try {
                        Field intentField = message.obj.getClass().getDeclaredField("intent");
                        intentField.setAccessible(true);
                        Intent intent = (Intent)intentField.get(message.obj);
                        String component = intent.getComponent().getClassName();
                        String bundleName = BundleInfoList.getInstance().getBundleNameForComponet(component);
                        if (bundleName != null) {
                            Bundle bundle = Framework.getBundle(bundleName);
                            if (bundle == null) {
                                bundle = Framework.tryLoadBundleInstance(bundleName);
                                if (bundle == null) {
                                    ClassLoadFromBundle.checkInstallBundleIfNeed(component);
                                    bundle = Framework.getBundle(bundleName);
                                }
                            }
                            if (bundle != null) {
                                bundle.start();
                            }
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                int what = message.what;
                this.handler.handleMessage(message);
                if (what == GC_WHEN_IDLE || what == DESTROY_ACTIVITY) {
                    clearDrawableCache();
                }
                AndroidHack.ensureLoadedApk();
            } catch (Throwable th) {
                th.printStackTrace();

                if ((th instanceof ClassNotFoundException)
                        || th.toString().contains("ClassNotFoundException")) {
                    if (message.what != RECEIVER) {
                        Object loadedApk = AndroidHack.getLoadedApk(
                                RuntimeVariables.androidApplication,
                                this.activityThread,
                                RuntimeVariables.androidApplication
                                        .getPackageName());
                        if (loadedApk == null) {
                            logger.error("",new RuntimeException("loadedapk is null"));
                        } else {
                            ClassLoader classLoader = ACDDHacks.LoadedApk_mClassLoader.get(loadedApk);
                            if (classLoader instanceof DelegateClassLoader) {
                                logger.error("",new RuntimeException("From ACDD:classNotFound ---", th));

                            } else {
                                logger.error("",new RuntimeException("wrong classloader in loadedapk---" + classLoader.getClass().getName(), th));

                            }
                        }
                    }
                } else if ((th instanceof ClassCastException)
                        || th.toString().contains("ClassCastException")) {
                    Process.killProcess(Process.myPid());
                } else {
                    if (message.what == STOP_ACTIVITY_SHOW || message.what == STOP_ACTIVITY_HIDE) {
                        // don't throw NullPointerException while handleStopActivity
                        return true;
                    }
                    if (message.what == RECEIVER) {
                        // if receiver is in plugin and plugin is not installed then shall crash, so skip throw here
                        return true;
                    }

                    // skip exception about com.cm.perm.PermService
                    if (th.toString().contains("com.cm.perm.PermService")) {
                        return true;
                    }
                    if (th instanceof IllegalStateException && th.toString().contains("The current thread must have a looper!")) {
                        return true;
                    }

                    if (th instanceof IllegalArgumentException && th.toString().contains("View not attached to window manager")) {
                        return true;
                    }

                    if (ThrowableCustomHandler.onHandleMessageException(th, message)) {
                        return true;
                    }

                    throw new RuntimeException(th);
                }
            }
            return true;
        }
    }

    static HandlerHack getHandlerHack(Handler handler, Object obj) {
        synchronized (AndroidHack.class) {
            if (sHandlerHack == null) {
                sHandlerHack = new HandlerHack(handler, obj);
            }
            return sHandlerHack;
        }
    }

    static void clearDrawableCache() {
        Resources resources = RuntimeVariables.delegateResources;
        if (resources instanceof DelegateResources) {
            ((DelegateResources)resources).clearDrawableCache();

        } else if (resources.getClass().getSuperclass().equals(Resources.class)) {
            DelegateResources.clearDrawableCache(resources);
        }
    }
	
    static class ActvityThreadGetter implements Runnable {
        ActvityThreadGetter() {
        }

        @Override
        public void run() {
            try {
                AndroidHack._sActivityThread = ACDDHacks.ActivityThread_currentActivityThread
                        .invoke(ACDDHacks.ActivityThread.getmClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (ACDDHacks.ActivityThread_currentActivityThread) {
                ACDDHacks.ActivityThread_currentActivityThread.notify();
            }
        }
    }

    static {
        _sActivityThread = null;
        _mLoadedApk = null;
    }

    public static Object getActivityThread() throws Exception {
        if (_sActivityThread == null) {
            if (Thread.currentThread().getId() == Looper.getMainLooper()
                    .getThread().getId()) {
                _sActivityThread = ACDDHacks.ActivityThread_currentActivityThread
                        .invoke(null);
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                synchronized (ACDDHacks.ActivityThread_currentActivityThread) {
                    handler.post(new ActvityThreadGetter());
                    ACDDHacks.ActivityThread_currentActivityThread.wait();
                }
            }
        }
        return _sActivityThread;
    }

    /**
     * we  nedd hook H(handler),hanlde message
     ***/
    public static Handler hackH() throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception(
                    "Failed to get ActivityThread.sCurrentActivityThread");
        }
        try {
            Handler handler = (Handler) ACDDHacks.ActivityThread
                    .field("mH")
                    .ofType(Hack.into("android.app.ActivityThread$H")
                            .getmClass()).get(activityThread);
            Field declaredField = Handler.class.getDeclaredField("mCallback");
            declaredField.setAccessible(true);
            declaredField.set(handler, getHandlerHack(handler,
                    activityThread));
        } catch (HackAssertionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ensureLoadedApk() throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception(
                    "Failed to get ActivityThread.sCurrentActivityThread");
        }
        Object loadedApk = getLoadedApk(RuntimeVariables.androidApplication,
                activityThread,
                RuntimeVariables.androidApplication.getPackageName());
        if (loadedApk == null) {
            loadedApk = createNewLoadedApk(RuntimeVariables.androidApplication,
                    activityThread);
            if (loadedApk == null) {
                throw new RuntimeException("can't create loadedApk");
            }
        }
        activityThread = loadedApk;
        if (!((ACDDHacks.LoadedApk_mClassLoader
                .get(activityThread)) instanceof DelegateClassLoader)) {
            ACDDHacks.LoadedApk_mClassLoader.set(activityThread,
                    RuntimeVariables.delegateClassLoader);
            ACDDHacks.LoadedApk_mResources.set(activityThread,
                    RuntimeVariables.delegateResources);
        }
    }

    public static Object getLoadedApk(Application application, Object obj,
                                      String str) {
        WeakReference weakReference = (WeakReference) ((Map) ACDDHacks.ActivityThread_mPackages
                .get(obj)).get(str);
        if (weakReference == null || weakReference.get() == null) {
            return null;
        }
        _mLoadedApk = weakReference.get();
        return _mLoadedApk;
    }

    public static Object createNewLoadedApk(Application application, Object obj) {
        try {
            Method declaredMethod = null;
            ApplicationInfo applicationInfo = application.getPackageManager()
                    .getApplicationInfo(application.getPackageName(), 1152);
            application.getPackageManager();
            Resources resources = application.getResources();
            if (resources instanceof DelegateResources) {
                declaredMethod = resources
                        .getClass()
                        .getSuperclass()
                        .getDeclaredMethod("getCompatibilityInfo");
            } else {
                try {
                    declaredMethod = resources.getClass().getDeclaredMethod(
                            "getCompatibilityInfo");
                } catch (NoSuchMethodException e) {
                }
                if (declaredMethod == null) {
                    declaredMethod = resources.getClass().getMethod("getCompatibilityInfo");
                }
            }
            declaredMethod.setAccessible(true);
            Class cls = Class.forName("android.content.res.CompatibilityInfo");
            Object invoke = declaredMethod.invoke(application.getResources()
            );
            Method declaredMethod2 = ACDDHacks.ActivityThread.getmClass()
                    .getDeclaredMethod("getPackageInfoNoCheck",
                            ApplicationInfo.class, cls);
            declaredMethod2.setAccessible(true);
            invoke = declaredMethod2.invoke(obj, applicationInfo, invoke);
            _mLoadedApk = invoke;
            return invoke;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * inject  system  classloader,we need handle  load class from  bundle
     * @param packageName  package name
     * @param classLoader    delegate  classloader
     * ***/
    public static void injectClassLoader(String packageName, ClassLoader classLoader)
            throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception(
                    "Failed to get ActivityThread.sCurrentActivityThread");
        }
        Object loadedApk = getLoadedApk(RuntimeVariables.androidApplication,
                activityThread, packageName);
        if (loadedApk == null) {
            loadedApk = createNewLoadedApk(RuntimeVariables.androidApplication,
                    activityThread);
        }
        if (loadedApk == null) {
            throw new Exception("Failed to get ActivityThread.mLoadedApk");
        }
        ACDDHacks.LoadedApk_mClassLoader.set(loadedApk, classLoader);
    }

    public static void injectApplication(String packageName, Application application)
            throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception(
                    "Failed to get ActivityThread.sCurrentActivityThread");
        }
        Object loadedApk = getLoadedApk(application, activityThread,
                application.getPackageName());
        if (loadedApk == null) {
            throw new Exception("Failed to get ActivityThread.mLoadedApk");
        }
        ACDDHacks.LoadedApk_mApplication.set(loadedApk, application);
        ACDDHacks.ActivityThread_mInitialApplication.set(activityThread,
                application);
    }

    /***
     * hack Resource  use delegate resource,process  resource in bundle
     *
     * @param application host application object
     * @param resources   delegate resource
     *****/
    public static void injectResources(Application application,
                                       Resources resources) throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception(
                    "Failed to get ActivityThread.sCurrentActivityThread");
        }
        Object loadedApk = getLoadedApk(application, activityThread,
                application.getPackageName());
        if (loadedApk == null) {
            activityThread = createNewLoadedApk(application, activityThread);
            if (activityThread == null) {
                throw new RuntimeException(
                        "Failed to get ActivityThread.mLoadedApk");
            }
            if (!((ACDDHacks.LoadedApk_mClassLoader
                    .get(activityThread)) instanceof DelegateClassLoader)) {
                ACDDHacks.LoadedApk_mClassLoader.set(activityThread,
                        RuntimeVariables.delegateClassLoader);
            }
            loadedApk = activityThread;
        }
        ACDDHacks.LoadedApk_mResources.set(loadedApk, resources);
        ACDDHacks.ContextImpl_mResources.set(application.getBaseContext(),
                resources);
        ACDDHacks.ContextImpl_mTheme.set(application.getBaseContext(), null);
    }

    /***
     * get Instrumentation,should be  hacked Instrumentation
     */
    public static Instrumentation getInstrumentation() throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread != null) {
            return ACDDHacks.ActivityThread_mInstrumentation
                    .get(activityThread);
        }
        throw new Exception(
                "Failed to get ActivityThread.sCurrentActivityThread");
    }

    /***
     * hack Instrumentation,we replace Instrumentation used HackInstrumentation<br>
     * such start activity in Instrumentation ,before this ,we need verify  target class is loaded or
     * load  target class,and so on
     **/
    public static void injectInstrumentationHook(Instrumentation instrumentation)
            throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception(
                    "Failed to get ActivityThread.sCurrentActivityThread");
        }
        ACDDHacks.ActivityThread_mInstrumentation.set(activityThread,
                instrumentation);
    }

    @SuppressWarnings("unused")
    public static void injectContextHook(ContextWrapper contextWrapper,
                                         ContextWrapper contextWrapperValue) {
        ACDDHacks.ContextWrapper_mBase.set(contextWrapper, contextWrapperValue);
    }
}