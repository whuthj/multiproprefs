package com.lib.multiproprefs_demo.act;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lib.multiproprefs.MPSharedPrefs;
import com.lib.multiproprefs_demo.R;
import com.lib.multiproprefs_demo.aidl.IMyService;
import com.lib.multiproprefs_demo.services.DaemonService;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mAct;

        public MyHandler(MainActivity act) {
            mAct = new WeakReference<MainActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity act = mAct.get();
            if (null == act) {
                return;
            }

            switch (msg.what) {
                case 1:
                    act.refreshText();
                    break;
                default:
                    break;
            }
        }
    }
    private MyHandler mHandler = new MyHandler(this);

    private Timer mTimer = new Timer();

    private IMyService mIMyService = null;
    private final ServiceConnection mSerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyService = IMyService.Stub.asInterface(service);
            try {
                mIMyService.getPerson();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIMyService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }, 1000, 1000);

        DaemonService.startService(getApplicationContext());

        Intent it = new Intent("com.lib.multiproprefs_demo.services.DaemonService");
        it.setPackage("com.lib.multiproprefs");
        bindService(it, mSerConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (null != mIMyService) {

        }
        super.onDestroy();
    }

    private void refreshText() {
        MPSharedPrefs sharedPrefs = new MPSharedPrefs(getApplicationContext(), "test");

        TextView tv = (TextView)findViewById(R.id.txt);
        tv.setText("多进程SharedPrefs--" + sharedPrefs.getString("value", ""));

        if (null == mIMyService) {
            return;
        }
        try {
            mIMyService.getPerson();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
