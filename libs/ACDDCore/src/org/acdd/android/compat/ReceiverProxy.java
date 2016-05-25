package org.acdd.android.compat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.acdd.log.ACDDLog;
import org.acdd.runtime.Globals;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2016/5/11.
 */
public class ReceiverProxy extends BroadcastReceiver {
    private static final String TAG = "ReceiverProxy";
    private String mTargetReceiverClassName = null;
    private SoftReference<BroadcastReceiver> mTargetReceiverRefer = null;

    public ReceiverProxy(String targetReceiverClassName) {
        mTargetReceiverClassName = targetReceiverClassName;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BroadcastReceiver targetReceiver = null;
        if (mTargetReceiverRefer == null || mTargetReceiverRefer.get() == null) {
            targetReceiver = createBroadcastReceiver();
        } else {
            targetReceiver = mTargetReceiverRefer.get();
        }
        if (targetReceiver != null) {
            targetReceiver.onReceive(context, intent);
            ACDDLog.d(TAG, " onReceive Success " + mTargetReceiverClassName + " Intent:" + String.valueOf(intent));
        } else {
            ACDDLog.d(TAG, " Get Target BroadcastReceiver:" + mTargetReceiverClassName + " Failed!");
        }
    }

    private BroadcastReceiver createBroadcastReceiver() {
        try {
            Class<?> targetReceiverClass = Globals.getClassLoader().loadClass(mTargetReceiverClassName);
            if (targetReceiverClass == null) {
                ACDDLog.d(TAG, "Can not found target BroadcastReceiver Class:" + mTargetReceiverClassName);
                return null;
            }
            Constructor<?> receiverConstructor = targetReceiverClass.getConstructor();
            if (receiverConstructor == null) {
                ACDDLog.d(TAG, "Get Constructor Failed:" + mTargetReceiverClassName);
                return null;
            }
            receiverConstructor.setAccessible(true);
            return (BroadcastReceiver) receiverConstructor.newInstance();
        } catch (Exception e) {
//            e.printStackTrace();
            ACDDLog.d(TAG, "create Target BroadcastReceiver:" + mTargetReceiverClassName + " Error!Exception:" + e.getMessage());
        }
        return null;
    }
}
