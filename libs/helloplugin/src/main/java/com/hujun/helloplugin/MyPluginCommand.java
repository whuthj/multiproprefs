package com.hujun.helloplugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.hujun.common.IPluginCommand;

import java.util.zip.Inflater;

/**
 * Created by hujun on 2016/5/19.
 */
public class MyPluginCommand implements IPluginCommand {

    public  MyPluginCommand(){

    }

    @Override
    public Object invoke(int cmd, Object... args) throws NoSuchMethodException {
        Log.e("hello", "hhhhh");
        return "Hello From Plugin";
    }

    @Override
    public Drawable getTestDrawable(Context ctx) {
        Drawable drawable = ctx.getResources().getDrawable(R.drawable.cm_logo_notification_normal);
        return drawable;
    }

    @Override
    public View getHelloView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.hello, null);
    }
}
