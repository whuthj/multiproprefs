package com.lib.multiproprefs;

import android.content.Context;

public class MPSharedPrefs {

    public static final String VERSION = "1.1";

    private Context mContext;
    private String mName;

    private MPSharedPrefs() {
    }

    public MPSharedPrefs(Context context, String name) {
        this.mContext = context;
        this.mName = name;
    }

    public String getString(final String key, final String defaultValue) {
        return MPSharedPrefsImpl.getString(mContext, mName, key, defaultValue);
    }

    public void setString(final String key, final String value) {
        MPSharedPrefsImpl.setString(mContext, mName, key, value);
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        return MPSharedPrefsImpl.getBoolean(mContext, mName, key, defaultValue);
    }

    public void setBoolean(final String key, final boolean value) {
        MPSharedPrefsImpl.setBoolean(mContext, mName, key, value);
    }

    public void setInt(final String key, final int value) {
        MPSharedPrefsImpl.setInt(mContext, mName, key, value);
    }

    public int getInt(final String key, final int defaultValue) {
        return MPSharedPrefsImpl.getInt(mContext, mName, key, defaultValue);
    }

    public void setLong(final String key, final long value) {
        MPSharedPrefsImpl.setLong(mContext, mName, key, value);
    }

    public long getLong(final String key, final long defaultValue) {
        return MPSharedPrefsImpl.getLong(mContext, mName, key, defaultValue);
    }

    public void remove(final String key) {
        MPSharedPrefsImpl.remove(mContext, mName, key);
    }

}
