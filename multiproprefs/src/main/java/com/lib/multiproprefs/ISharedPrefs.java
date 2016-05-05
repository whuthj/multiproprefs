package com.lib.multiproprefs;

interface ISharedPrefs {

    String getString(String key, String defaultValue);

    void setString(String key, String value);

    boolean getBoolean(String key, boolean defaultValue);

    void setBoolean(final String key, final boolean value);

    void setInt(final String key, final int value);

    int getInt(final String key, final int defaultValue);

    void setFloat(final String key, final float value);

    float getFloat(final String key, final float defaultValue);

    void setLong(final String key, final long value);

    long getLong(final String key, final long defaultValue);

    void remove(final String key);

    boolean hasKey(String key);

}
