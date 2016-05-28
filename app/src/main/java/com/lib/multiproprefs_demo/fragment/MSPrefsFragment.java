package com.lib.multiproprefs_demo.fragment;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lib.multiproprefs.MPSharedPrefs;
import com.lib.multiproprefs_demo.R;
import com.lib.multiproprefs_demo.services.DaemonService;
import com.lib.multiproprefs_demo.services.IMyService;
import com.lib.multiproprefs_demo.vo.Person;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MSPrefsFragment extends BaseFragment {

    private static class MyHandler extends Handler {
        private WeakReference<MSPrefsFragment> mAct;

        public MyHandler(MSPrefsFragment act) {
            mAct = new WeakReference<MSPrefsFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            MSPrefsFragment act = mAct.get();
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
                            TextView tv = (TextView) mMainView.findViewById(com.lib.multiproprefs_demo.R.id.txt_1);
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
    private Timer mTimer = new Timer();

    public MSPrefsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.activity_msprefs, container, false);
        init();

        return mMainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mSerConn) {
            getActivity().unbindService(mSerConn);
        }
    }

    private void init() {
        DaemonService.startService(getActivity().getApplicationContext());

        Intent it = new Intent("com.lib.multiproprefs_demo.services.DaemonService");
        it.setPackage("com.lib.multiproprefs");
        getActivity().bindService(it, mSerConn, getActivity().BIND_AUTO_CREATE);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }, 1000, 1000);
    }

    private void refreshText() {
        MPSharedPrefs sharedPrefs = new MPSharedPrefs(getActivity().getApplicationContext(), "test");

        TextView tv = (TextView) mMainView.findViewById(com.lib.multiproprefs_demo.R.id.txt);
        tv.setText("多进程SharedPrefs--" + sharedPrefs.getString("value", ""));

        if (null == mIMyService) {
            return;
        }
        try {
            final List<Person> lstPersion = mIMyService.getPerson();
            if (lstPersion != null && lstPersion.size() > 0) {
                TextView tv_1 = (TextView) mMainView.findViewById(com.lib.multiproprefs_demo.R.id.txt_1);
                tv_1.setText("name:" + lstPersion.get(0).name + " age:" + lstPersion.get(0).age);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
