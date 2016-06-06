package com.lib.multiproprefs_demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lib.multiproprefs_demo.R;
import com.lib.multiproprefs_demo.adapter.FileAdapter;

import org.libsdl.app.SDLActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class VideoFragment extends BaseFragment {

    private Button mBtnView;
    private EditText mEditText;
    private ListView mListView;
    private FileAdapter mFileAdapter;

    private static class MyHandler extends Handler {
        private WeakReference<VideoFragment> mAct;
        public VideoFragment getCurVideoFragment() {
            return mAct.get();
        }

        public MyHandler(VideoFragment act) {
            mAct = new WeakReference<VideoFragment>(act);
        }
    }
    private MyHandler mHandler = new MyHandler(this);
    private String mStrCurPath;

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
        mEditText = (EditText) mMainView.findViewById(R.id.editText);
        mListView = (ListView) mMainView.findViewById(R.id.listView);
    }

    private void init() {
        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUrl = mEditText.getText().toString();
                startPlay(strUrl);
            }
        });

        mFileAdapter = new FileAdapter(getActivity());
        mListView.setAdapter(mFileAdapter);
        mFileAdapter.setOnClickFileItem(new FileAdapter.OnClickFileItem() {
            @Override
            public void OnClick(File file) {
                if (file.isDirectory()) {
                    mStrCurPath = file.getAbsolutePath();
                    getFileList();
                } else {
                    String strUrl = file.getAbsolutePath();
                    startPlay(strUrl);
                }
            }

            @Override
            public void OnBack() {
                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                if (mStrCurPath.equals(sdPath)) {
                    return;
                }

                int index = mStrCurPath.lastIndexOf("/");
                mStrCurPath = mStrCurPath.substring(0, index);
                getFileList();
            }
        });

        mStrCurPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        getFileList();
    }

    private void getFileList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFileListInThread();
            }
        }).start();
    }

    private void getFileListInThread() {
        File file = new File(mStrCurPath);
        File[] arrFile = file.listFiles();

        final List<File> lstFile = Arrays.asList(arrFile);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFileAdapter.setLstFile(lstFile);
                mFileAdapter.notifyDataSetChanged();
            }
        });
    }

    private void startPlay(String fullPath)
    {
        Intent intent = new Intent(getActivity(), SDLActivity.class);
        intent.putExtra("filename", fullPath);
        startActivity(intent);
    }

}
