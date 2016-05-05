package com.lib.multiproprefs_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lib.multiproprefs.MPSharedPrefs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MPSharedPrefs sharedPrefs = new MPSharedPrefs(getApplicationContext(), "test");
        sharedPrefs.setString("value", "hello");
    }
}
