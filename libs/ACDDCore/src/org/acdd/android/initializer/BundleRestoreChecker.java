package org.acdd.android.initializer;

/**
 * Created by Jiang on 15/10/15.
 * 用于判断Bundle是否允许被restoreProfile中创建
 * 否则将其删除
 */
public interface BundleRestoreChecker {
    boolean isAllowRestore(String name);
}
