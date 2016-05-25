package org.acdd.android.compat;

import android.content.pm.ActivityInfo;
import android.os.Message;

import org.acdd.framework.ACDD;
import org.acdd.hack.AndroidHack;

import java.lang.reflect.Field;

/**
 * Created by zhoukaifeng on 2016/1/12 11:45.
 * email: zhoukaifeng@conew.com.
 */
public class ThrowableCustomHandler {
    public static boolean onHandleMessageException(Throwable e, Message message) {
        if (handleActivityIsRunningException(e, message)) {
            return true;
        }
        if (handleIDefendServiceException(e)) {
            return true;
        }
        if (handleActivityDestroyException(e, message)) {
            return true;
        }
        return false;
    }

    private static boolean handleActivityIsRunningException(Throwable th, Message message) {
        if (message.what == AndroidHack.LAUNCH_ACTIVITY
                && th != null
                && th.toString().contains("is your activity running")) {
            try {
                Field infoField = message.obj.getClass().getDeclaredField("activityInfo");
                infoField.setAccessible(true);
                ActivityInfo info = (ActivityInfo) infoField.get(message.obj);
                ACDD.getInstance().reportCrash(ICrashReporter.ACDD_ACTIVITY_RUNNING_ERROR,
                        new RuntimeException("activity name:" + info.name, th));
                return true;
            } catch (Throwable e) {
            }
        }
        return false;
    }

    private static boolean handleActivityDestroyException(Throwable th, Message message) {
        if (message.what == AndroidHack.DESTROY_ACTIVITY
                && th != null) {
            String s = th.toString();
            if (s != null) {
                return (s.contains("but the ViewAncestor is attached to") ||
                        s.contains("not attached to window manager"));
            }
        }
        return false;
    }

    public static boolean handleIDefendServiceException(Throwable th) {
        if (th != null && (th instanceof NullPointerException)) {
            StackTraceElement[] stackTraceElements = th.getStackTrace();
            if (stackTraceElements != null) {
                for (StackTraceElement element : stackTraceElements) {
                    if (element == null) {
                        continue;
                    }
                    if (element.toString().contains("IDefendService")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
