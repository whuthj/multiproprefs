package com.lib.multiproprefs;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPrefsImpl implements ISharedPrefs {

    private Context mContext;
    private String mPrefName;

    public SharedPrefsImpl(Context context, String prefName) {
        mContext = context;
        mPrefName = prefName;
    }

    public String getString(final String key,
                                final String defaultValue) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public void setString(final String key, final String value) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putString(key, value).apply();
    }

    public boolean getBoolean(final String key,
                                  final boolean defaultValue) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public boolean hasKey(final String key) {
        return mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE)
                .contains(key);
    }

    public void setBoolean(final String key, final boolean value) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putBoolean(key, value).apply();
    }

    public void setInt(final String key, final int value) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putInt(key, value).apply();
    }

    public void increaseInt(final String key) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        increaseInt(settings, key);
    }

    public void increaseInt(final SharedPreferences sp, final String key) {
        final int v = sp.getInt(key, 0) + 1;
        sp.edit().putInt(key, v).apply();
    }

    public void increaseInt(final SharedPreferences sp, final String key,
                                final int increment) {
        final int v = sp.getInt(key, 0) + increment;
        sp.edit().putInt(key, v).apply();
    }

    public int getInt(final String key, final int defaultValue) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    public void setFloat(final String key, final float value) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putFloat(key, value).apply();
    }

    public float getFloat(final String key, final float defaultValue) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    public void setLong(final String key, final long value) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        settings.edit().putLong(key, value).apply();
    }

    public long getLong(final String key, final long defaultValue) {
        final SharedPreferences settings =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public void remove(final String key) {
        final SharedPreferences prefs =
                mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }

    public void clear(final SharedPreferences p) {
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.apply();
    }

}
