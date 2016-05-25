package com.lib.multiproprefs;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class MPSharedPrefsProvider extends ContentProvider {
    private static final String AUTHORITY = "com.lib.multiproprefs.MPSharedPrefsProvider";

    public static final String CONTENT_PREFS_BOOLEAN_URI = "content://" + AUTHORITY + "/boolean/";
    public static final String CONTENT_PREFS_STRING_URI = "content://" + AUTHORITY + "/string/";
    public static final String CONTENT_PREFS_INT_URI = "content://" + AUTHORITY + "/integer/";
    public static final String CONTENT_PREFS_LONG_URI = "content://" + AUTHORITY + "/long/";

    public static final String PREFS_KEY = "key";
    public static final String PREFS_VALUE = "value";

    public static final int PREFS_BOOLEAN = 1;
    public static final int PREFS_STRING = 2;
    public static final int PREFS_INT = 3;
    public static final int PREFS_LONG = 4;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "boolean/*/*", PREFS_BOOLEAN);
        sUriMatcher.addURI(AUTHORITY, "string/*/*", PREFS_STRING);
        sUriMatcher.addURI(AUTHORITY, "integer/*/*", PREFS_INT);
        sUriMatcher.addURI(AUTHORITY, "long/*/*", PREFS_LONG);
    }

    private static String[] PREFERENCE_COLUMNS = {PREFS_VALUE};
    private static Map<String, SharedPrefsUtils> sPreferences = new HashMap<>();

    private static class SharedPrefsModel {
        String name;
        String key;

        public SharedPrefsModel(String name, String key) {
            this.name = name;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }
    }

    public static Uri buildUri(String name, String key, int type) {
        StringBuilder strUri = new StringBuilder();

        switch (type) {
            case MPSharedPrefsProvider.PREFS_BOOLEAN:
                strUri.append(MPSharedPrefsProvider.CONTENT_PREFS_BOOLEAN_URI);
                break;
            case MPSharedPrefsProvider.PREFS_INT:
                strUri.append(MPSharedPrefsProvider.CONTENT_PREFS_INT_URI);
                break;
            case MPSharedPrefsProvider.PREFS_LONG:
                strUri.append(MPSharedPrefsProvider.CONTENT_PREFS_LONG_URI);
                break;
            case MPSharedPrefsProvider.PREFS_STRING:
                strUri.append(MPSharedPrefsProvider.CONTENT_PREFS_STRING_URI);
                break;
            default:
                throw new IllegalStateException("unsupport type:" + type);
        }
        strUri.append(name);
        strUri.append("/");
        strUri.append(key);

        return Uri.parse(strUri.toString());
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = null;
        SharedPrefsModel model = getModelByUri(uri);
        switch (sUriMatcher.match(uri)) {
            case PREFS_BOOLEAN:
                if (getSharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getSharedPrefs(model.getName()).getValue(model.getKey(), false) ? 1 : 0);
                }
                break;
            case PREFS_STRING:
                if (getSharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getSharedPrefs(model.getName()).getValue(model.getKey(), ""));
                }
                break;
            case PREFS_INT:
                if (getSharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getSharedPrefs(model.getName()).getValue(model.getKey(), -1));
                }
                break;
            case PREFS_LONG:
                if (getSharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getSharedPrefs(model.getName()).getValue(model.getKey(), -1));
                }
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new IllegalStateException("unsupport insert!");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case PREFS_BOOLEAN:
            case PREFS_LONG:
            case PREFS_STRING:
            case PREFS_INT:
                SharedPrefsModel model = getModelByUri(uri);
                if (model != null) {
                    getSharedPrefs(model.getName()).remove(model.getKey());
                }
                break;
            default:
                throw new IllegalStateException(" unsupported uri : " + uri);
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SharedPrefsModel model = getModelByUri(uri);

        if(model == null) {
            throw new IllegalArgumentException("update prefModel is null");
        }

        switch (sUriMatcher.match(uri)) {
            case PREFS_BOOLEAN:
                setBoolean(model.getName(), values);
                break;
            case PREFS_LONG:
                setLong(model.getName(), values);
                break;
            case PREFS_STRING:
                setString(model.getName(), values);
                break;
            case PREFS_INT:
                setInt(model.getName(), values);
                break;
            default:
                throw new IllegalStateException("update unsupported uri : " + uri);
        }

        return 0;
    }

    private <T> MatrixCursor preferenceToCursor(T value) {
        MatrixCursor matrixCursor = new MatrixCursor(PREFERENCE_COLUMNS, 1);
        MatrixCursor.RowBuilder builder = matrixCursor.newRow();
        builder.add(value);
        return matrixCursor;
    }

    private SharedPrefsUtils getSharedPrefs(String name) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("getSharedPrefs name is null!");
        }
        if (sPreferences.get(name) == null) {
            SharedPrefsUtils pref = new SharedPrefsUtils(getContext(), name);
            sPreferences.put(name, pref);
        }
        return sPreferences.get(name);
    }

    private SharedPrefsModel getModelByUri(Uri uri) {
        if (uri == null || uri.getPathSegments().size() != 3) {
            throw new IllegalArgumentException("getModelByUri uri is wrong : " + uri);
        }
        String name = uri.getPathSegments().get(1);
        String key = uri.getPathSegments().get(2);
        return new SharedPrefsModel(name, key);
    }

    private void setInt(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null!");
        }
        String kInteger = values.getAsString(PREFS_KEY);
        int vInteger = values.getAsInteger(PREFS_VALUE);
        getSharedPrefs(name).setValue(kInteger, vInteger);
    }

    private void setBoolean(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null!");
        }
        String kBoolean = values.getAsString(PREFS_KEY);
        boolean vBoolean = values.getAsBoolean(PREFS_VALUE);
        getSharedPrefs(name).setValue(kBoolean, vBoolean);
    }

    private void setLong(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null!");
        }
        String kLong = values.getAsString(PREFS_KEY);
        long vLong = values.getAsLong(PREFS_VALUE);
        getSharedPrefs(name).setValue(kLong, vLong);
    }

    private void setString(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null!");
        }
        String kString = values.getAsString(PREFS_KEY);
        String vString = values.getAsString(PREFS_VALUE);
        getSharedPrefs(name).setValue(kString, vString);
    }
}
