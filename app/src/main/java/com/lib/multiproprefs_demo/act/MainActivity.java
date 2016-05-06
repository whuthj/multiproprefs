package com.lib.multiproprefs_demo.act;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lib.multiproprefs.MPSharedPrefs;
import com.lib.multiproprefs_demo.R;
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
    }

    private void refreshText() {
        MPSharedPrefs sharedPrefs = new MPSharedPrefs(getApplicationContext(), "test");

        TextView tv = (TextView)findViewById(R.id.txt);
        tv.setText("多进程SharedPrefs--" + sharedPrefs.getString("value", ""));
    }
}
