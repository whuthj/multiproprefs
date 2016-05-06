package com.lib.multiproprefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtils {
    private Context mContext;
    private String mPrefName;

    public SharedPrefsUtils(Context context, String prefName) {
        mContext = context;
        mPrefName = prefName;
    }

    public boolean hasKey(final String key) {
        return mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE).contains(key);
    }

    public void remove(final String key) {
        final SharedPreferences prefs = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }

    public void clear(final SharedPreferences p) {
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.apply();
    }

    public String getValue(final String key, final String defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public void setValue(final String key, final String value) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putString(key, value).apply();
    }

    public boolean getValue(final String key, final boolean defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public void setValue(final String key, final boolean value) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putBoolean(key, value).apply();
    }

    public int getValue(final String key, final int defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    public void setValue(final String key, final int value) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putInt(key, value).apply();
    }

    public float getValue(final String key, final float defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    public void setValue(final String key, final float value) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putFloat(key, value).apply();
    }

    public long getValue(final String key, final long defaultValue) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public void setValue(final String key, final long value) {
        final SharedPreferences settings = mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putLong(key, value).apply();
    }
}
