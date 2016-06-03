package com.lib.multiproprefs_demo.act;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.lib.multiproprefs_demo.R;
import com.lib.multiproprefs_demo.adapter.FragmentAdapter;
import com.lib.multiproprefs_demo.animation.CubePageTransformer;
import com.lib.multiproprefs_demo.animation.RotateDownPageTransformer;
import com.lib.multiproprefs_demo.animation.ZoomOutPageTransformer;
import com.lib.multiproprefs_demo.fragment.MSPrefsFragment;
import com.lib.multiproprefs_demo.fragment.NdkFragment;
import com.lib.multiproprefs_demo.fragment.PluginFragment;
import com.lib.multiproprefs_demo.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentAdapter mFragmentAdapter;
    private ViewPager mPageVp;

    private MSPrefsFragment mMsprefsFrag;
    private NdkFragment mNdkFrag;
    private PluginFragment mPluginFrag;
    private VideoFragment mVideoFrag;

    private View mllTabMsprefs;
    private View mllTabNdk;
    private View mllTabPlugin;
    private View mllTabVideo;
    private TextView mTvMsprefs;
    private TextView mTvNdk;
    private TextView mTvPlugin;
    private TextView mTvVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findById();
        init();
    }

    private void findById() {
        mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);

        mllTabMsprefs = this.findViewById(R.id.ll_tab_msprefs);
        mllTabNdk = this.findViewById(R.id.ll_tab_ndk);
        mllTabPlugin = this.findViewById(R.id.ll_tab_plugin);
        mllTabVideo = this.findViewById(R.id.ll_tab_video);

        mTvMsprefs = (TextView) this.findViewById(R.id.tv_msprefs);
        mTvNdk = (TextView) this.findViewById(R.id.tv_ndk_test);
        mTvPlugin = (TextView) this.findViewById(R.id.tv_plugin_test);
        mTvVideo = (TextView) this.findViewById(R.id.tv_video_test);
    }

    private void init() {
        buildFragmentAdapter();
        mPageVp.setAdapter(mFragmentAdapter);
        mPageVp.setCurrentItem(2);
        mPageVp.addOnPageChangeListener(this);
        mPageVp.setPageTransformer(true, new RotateDownPageTransformer());

        mllTabMsprefs.setOnClickListener(this);
        mllTabNdk.setOnClickListener(this);
        mllTabPlugin.setOnClickListener(this);
        mllTabVideo.setOnClickListener(this);

        mTvPlugin.setTextColor(Color.BLUE);
    }

    private void buildFragmentAdapter() {
        mMsprefsFrag = new MSPrefsFragment();
        mNdkFrag = new NdkFragment();
        mPluginFrag = new PluginFragment();
        mVideoFrag = new VideoFragment();
        mFragmentList.add(mMsprefsFrag);
        mFragmentList.add(mNdkFrag);
        mFragmentList.add(mPluginFrag);
        mFragmentList.add(mVideoFrag);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetTabTextColor();
        mPageVp.setCurrentItem(position);

        switch (position) {
            case 0:
                mTvMsprefs.setTextColor(Color.BLUE);
                break;
            case 1:
                mTvNdk.setTextColor(Color.BLUE);
                break;
            case 2:
                mTvPlugin.setTextColor(Color.BLUE);
                break;
            case 3:
                mTvVideo.setTextColor(Color.BLUE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        resetTabTextColor();
        setTabTextColor(v.getId());
    }

    private void setTabTextColor(int id) {
        switch (id) {
            case R.id.ll_tab_msprefs:
                mPageVp.setCurrentItem(0);
                mTvMsprefs.setTextColor(Color.BLUE);
                break;
            case R.id.ll_tab_ndk:
                mPageVp.setCurrentItem(1);
                mTvNdk.setTextColor(Color.BLUE);
                break;
            case R.id.ll_tab_plugin:
                mPageVp.setCurrentItem(2);
                mTvPlugin.setTextColor(Color.BLUE);
                break;
            case R.id.ll_tab_video:
                mPageVp.setCurrentItem(3);
                mTvVideo.setTextColor(Color.BLUE);
                break;
            default:
                break;
        }
    }

    private void resetTabTextColor() {
        mTvMsprefs.setTextColor(Color.BLACK);
        mTvNdk.setTextColor(Color.BLACK);
        mTvPlugin.setTextColor(Color.BLACK);
        mTvVideo.setTextColor(Color.BLACK);
    }
}
