package com.lib.multiproprefs_demo.act;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lib.multiproprefs_demo.vo.Person;
import com.lib.multiproprefs.MPSharedPrefs;
import com.lib.multiproprefs_demo.R;
import com.hujun.common.*;
import com.lib.multiproprefs_demo.services.DaemonService;
import com.lib.multiproprefs_demo.services.IMyService;

import org.acdd.framework.ACDD;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static final String libSoName = "test";

    static {
        try {
            System.loadLibrary(libSoName);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("JniMissingFunction")
    public native String fromJNI();

    @SuppressWarnings("JniMissingFunction")
    public native void grayPhoto(Bitmap in, Bitmap out);

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
                final List<Person> lstPersion = mIMyService.getPerson();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lstPersion != null && lstPersion.size() > 0) {
                            TextView tv = (TextView) findViewById(R.id.txt_1);
                            tv.setText(lstPersion.get(0).name + " age:" + lstPersion.get(0).age);
                        }
                    }
                });

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

        String str =  fromJNI();
        TextView tv_1 = (TextView) findViewById(R.id.textView);
        tv_1.setText(str);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intPlugin = new Intent();
                    intPlugin.setClassName(MainActivity.this, "com.hujun.helloplugin.MainActivity");
                    startActivity(intPlugin);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

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

        initPlugin();

        try {
            Drawable drawable = PluginCommand.getCommand(PluginCommand.CMD_HELLO).getTestDrawable(MainActivity.this);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap in = bd.getBitmap();
            Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ALPHA_8);
            grayPhoto(in, out);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(out);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
            View view = PluginCommand.getCommand(PluginCommand.CMD_HELLO).getHelloView(MainActivity.this);
            linearLayout.addView(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initPlugin() {
        FileInputStream ins = null;
        try {
            File deployFile = new File("/sdcard/helloplugin.so");
            ins = new FileInputStream(new File(deployFile.getAbsolutePath()));
            ACDD.getInstance().installBundle("com.hujun.helloplugin", ins);
        } catch (Exception exe) {
            exe.printStackTrace();
        } finally {
            try {
                if (ins != null) ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Class<?> clazz = Class.forName("com.hujun.helloplugin.PluginApplication",
                    false, ACDD.getInstance().getBundleClassLoader("com.hujun.helloplugin"));
            Method initMethod = clazz.getMethod("init");
            IPluginCommand cmd = (IPluginCommand)initMethod.invoke(null);

            String str =  (String) cmd.invoke(123);
            TextView tv_1 = (TextView) findViewById(R.id.txt_1);
            tv_1.setText(str);
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
            final List<Person> lstPersion = mIMyService.getPerson();
            if (lstPersion != null && lstPersion.size() > 0) {
                TextView tv_1 = (TextView) findViewById(R.id.txt_1);
                tv_1.setText("name:" + lstPersion.get(0).name + " age:" + lstPersion.get(0).age);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
