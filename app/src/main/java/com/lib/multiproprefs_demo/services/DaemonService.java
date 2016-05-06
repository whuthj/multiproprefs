package com.lib.multiproprefs_demo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lib.multiproprefs.MPSharedPrefs;

public class DaemonService extends Service {

    public static void startService(Context ctx) {
        if (null == ctx) {
            return;
        }

        try {
            Intent it = new Intent(ctx, DaemonService.class);
            ctx.startService(it);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DaemonService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread() {
            private int count = 0;

            public void run() {
                while (true) {
                    MPSharedPrefs prefs = new MPSharedPrefs(getApplicationContext(), "test");
                    prefs.setString("value", "hello" + count);

                    count++;
                    count %= 100;

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
