package com.lib.multiproprefs_demo.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lib.multiproprefs_demo.R;
import com.lib.multiproprefs_demo.ndk.NdkFunc;

public class NdkFragment extends BaseFragment {

    private NdkFunc mNdkFunc = new NdkFunc();

    public NdkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.activity_ndk, container, false);
        init();

        return mMainView;
    }

    private void init() {
        String str =  mNdkFunc.fromJNI();
        TextView tv_1 = (TextView) mMainView.findViewById(R.id.textView);
        tv_1.setText(str);
    }

}
