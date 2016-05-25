package com.lib.multiproprefs_demo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.lib.multiproprefs.MPSharedPrefs;
import com.lib.multiproprefs_demo.vo.Person;

import java.util.ArrayList;
import java.util.List;

public class DaemonService extends Service {
    private static final String TAG = DaemonService.class.getSimpleName();

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

    private List<Person> mPersons = new ArrayList<Person>();
    private final IMyService.Stub mBinder = new IMyService.Stub() {

        @Override
        public List<Person> getPerson() throws RemoteException {
            synchronized (mPersons) {
                return mPersons;
            }
        }

        @Override
        public void addPerson(Person person) throws RemoteException {
            synchronized (mPersons) {
                if (!mPersons.contains(person)) {
                    mPersons.add(person);
                }
            }
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            String packageName = null;
            String[] packages = DaemonService.this.getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            Log.d(TAG, "onTransact: " + packageName);

            return super.onTransact(code, data, reply, flags);
        }

    };

    public DaemonService() {
        Person person = new Person();
        person.age = 20;
        person.sno = 1;
        person.name = "test";
        person.sex = 2;

        mPersons.add(person);
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
        return mBinder;
    }
}
