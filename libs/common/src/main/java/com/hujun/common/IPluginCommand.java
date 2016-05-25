package com.hujun.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by hujun on 2016/5/19.
 */
public interface IPluginCommand {
    Object invoke(int cmd, Object... args) throws NoSuchMethodException;
    Drawable getTestDrawable(Context ctx);
    View getHelloView(Context ctx);
}
