package org.acdd.android.compat;

/**
 * Created by zhoukaifeng on 2016/1/8 13:28.
 * email: zhoukaifeng@conew.com.
 */
public interface ICrashReporter {
    public static final String ACDD_CREATE_SERVICE_ERROR = "4012";
    public static final String ACDD_ACTIVITY_RUNNING_ERROR = "4013";
    public static final String ACDD_RELOAD_BUNDLE_FAILED = "4014";
    public static final String ACDD_META_READ_ERROR = "4015";
    public static final String ACDD_META_WRITE_ERROR = "4016";
    public static final String ACDD_ACTIVITY_DESTROY_ERROR = "4017";
    public static final String ACDD_ACTIVITY_PROXY_ERROR = "4021";
    public static final String ACDD_PARSE_BUNDLE_INFO_ERROR = "4022";
    public static final String ACDD_CREATE_PLUGIN_APPLICATION_ERROR = "4030";

    void reportCrash(String dumpKey, Throwable throwable);
}
