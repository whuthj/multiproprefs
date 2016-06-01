package com.lib.multiproprefs_demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lib.multiproprefs_demo.R;

import org.libsdl.app.SDLActivity;

public class VideoFragment extends BaseFragment {

    private Button mBtnView;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.activity_video, container, false);
        findViewById();
        init();

        return mMainView;
    }

    private void findViewById() {
        mBtnView = (Button) mMainView.findViewById(R.id.btnView);
    }

    private void init() {
        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), SDLActivity.class);
                startActivity(it);
            }
        });
    }

}
