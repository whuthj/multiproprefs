package com.lib.multiproprefs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

class MPSharedPrefsImpl {

    public static String getString(Context context, String name, String key, String defaultValue) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_STRING);
        String value = defaultValue;
        Cursor cursor = context.getContentResolver().query(URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex(MPSharedPrefsProvider.PREFS_VALUE));
        }
        closeCursor(cursor);
        return value;
    }

    public static int getInt(Context context, String name, String key, int defaultValue) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_INT);
        int value = defaultValue;
        Cursor cursor = context.getContentResolver().query(URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex(MPSharedPrefsProvider.PREFS_VALUE));
        }
        closeCursor(cursor);
        return value;
    }

    public static long getLong(Context context, String name, String key, long defaultValue) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_LONG);
        long value = defaultValue;
        Cursor cursor = context.getContentResolver().query(URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getLong(cursor.getColumnIndex(MPSharedPrefsProvider.PREFS_VALUE));
        }
        closeCursor(cursor);
        return value;
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defaultValue) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_BOOLEAN);
        int value = defaultValue ? 1 : 0;
        Cursor cursor = context.getContentResolver().query(URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex(MPSharedPrefsProvider.PREFS_VALUE));
        }
        closeCursor(cursor);
        return value == 1;
    }

    public static void remove(Context context, String name, String key) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_STRING);
        context.getContentResolver().delete(URI, null, null);
    }

    public static void setString(Context context, String name, String key, String value) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_STRING);
        ContentValues cv = new ContentValues();
        cv.put(MPSharedPrefsProvider.PREFS_KEY, key);
        cv.put(MPSharedPrefsProvider.PREFS_VALUE, value);
        context.getContentResolver().update(URI, cv, null, null);
    }

    public static void setBoolean(Context context, String name, String key, boolean value) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_BOOLEAN);
        ContentValues cv = new ContentValues();
        cv.put(MPSharedPrefsProvider.PREFS_KEY, key);
        cv.put(MPSharedPrefsProvider.PREFS_VALUE, value);
        context.getContentResolver().update(URI, cv, null, null);
    }

    public static void setInt(Context context, String name, String key, int value) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_INT);
        ContentValues cv = new ContentValues();
        cv.put(MPSharedPrefsProvider.PREFS_KEY, key);
        cv.put(MPSharedPrefsProvider.PREFS_VALUE, value);
        context.getContentResolver().update(URI, cv, null, null);
    }

    public static void setLong(Context context, String name, String key, long value) {
        Uri URI = MPSharedPrefsProvider.buildUri(name, key, MPSharedPrefsProvider.PREFS_LONG);
        ContentValues cv = new ContentValues();
        cv.put(MPSharedPrefsProvider.PREFS_KEY, key);
        cv.put(MPSharedPrefsProvider.PREFS_VALUE, value);
        context.getContentResolver().update(URI, cv, null, null);
    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}

