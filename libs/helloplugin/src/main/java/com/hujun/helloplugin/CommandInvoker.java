package com.hujun.helloplugin;

/**
 * Created by hujun on 2016/5/19.
 */
public interface CommandInvoker {
    Object invoke(Object... args);
}