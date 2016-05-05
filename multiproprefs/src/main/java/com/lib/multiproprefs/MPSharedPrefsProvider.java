package com.lib.multiproprefs;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;
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

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = null;
        SharedPrefsModel model = getModelByUri(uri);
        switch (sUriMatcher.match(uri)) {
            case PREFS_BOOLEAN:
                if (getIEasySharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getIEasySharedPrefs(model.getName()).getBoolean(model.getKey(), false) ? 1 : 0);
                }
                break;
            case PREFS_STRING:
                if (getIEasySharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getIEasySharedPrefs(model.getName()).getString(model.getKey(), ""));
                }
                break;
            case PREFS_INT:
                if (getIEasySharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getIEasySharedPrefs(model.getName()).getInt(model.getKey(), -1));
                }
                break;
            case PREFS_LONG:
                if (getIEasySharedPrefs(model.getName()).hasKey(model.getKey())) {
                    cursor = preferenceToCursor(getIEasySharedPrefs(model.getName()).getLong(model.getKey(), -1));
                }
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new IllegalStateException("insert unsupport!!!");
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
                    getIEasySharedPrefs(model.getName()).remove(model.getKey());
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

    private static String[] PREFERENCE_COLUMNS = {PREFS_VALUE};

    private <T> MatrixCursor preferenceToCursor(T value) {
        MatrixCursor matrixCursor = new MatrixCursor(PREFERENCE_COLUMNS, 1);
        MatrixCursor.RowBuilder builder = matrixCursor.newRow();
        builder.add(value);
        return matrixCursor;
    }

    private void setInt(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        String kInteger = values.getAsString(PREFS_KEY);
        int vInteger = values.getAsInteger(PREFS_VALUE);
        getIEasySharedPrefs(name).setInt(kInteger, vInteger);
    }

    private void setBoolean(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        String kBoolean = values.getAsString(PREFS_KEY);
        boolean vBoolean = values.getAsBoolean(PREFS_VALUE);
        getIEasySharedPrefs(name).setBoolean(kBoolean, vBoolean);
    }

    private void setLong(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        String kLong = values.getAsString(PREFS_KEY);
        long vLong = values.getAsLong(PREFS_VALUE);
        getIEasySharedPrefs(name).setLong(kLong, vLong);
    }

    private void setString(String name, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        String kString = values.getAsString(PREFS_KEY);
        String vString = values.getAsString(PREFS_VALUE);
        getIEasySharedPrefs(name).setString(kString, vString);
    }

    private static Map<String, ISharedPrefs> sPreferences = new HashMap<>();

    private ISharedPrefs getIEasySharedPrefs(String name) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("getIEasySharedPrefs name is null!!!");
        }
        if (sPreferences.get(name) == null) {
            ISharedPrefs pref = new SharedPrefsImpl(getContext(), name);
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


    public static Uri buildUri(String name, String key, int type) {
        return Uri.parse(getUriByType(type) + name + "/" + key);
    }

    private static String getUriByType(int type) {
        switch (type) {
            case MPSharedPrefsProvider.PREFS_BOOLEAN:
                return MPSharedPrefsProvider.CONTENT_PREFS_BOOLEAN_URI;
            case MPSharedPrefsProvider.PREFS_INT:
                return MPSharedPrefsProvider.CONTENT_PREFS_INT_URI;
            case MPSharedPrefsProvider.PREFS_LONG:
                return MPSharedPrefsProvider.CONTENT_PREFS_LONG_URI;
            case MPSharedPrefsProvider.PREFS_STRING:
                return MPSharedPrefsProvider.CONTENT_PREFS_STRING_URI;
        }
        throw new IllegalStateException("UnSupport PrefsType : " + type);
    }
}
