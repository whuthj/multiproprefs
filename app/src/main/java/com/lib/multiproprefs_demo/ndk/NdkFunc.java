package com.lib.multiproprefs_demo.ndk;

import android.graphics.Bitmap;

/**
 * Created by hujun on 2016/5/28.
 */
public class NdkFunc {
    private static final String libSoName = "test";

    static {
        try {
            System.loadLibrary(libSoName);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("JniMissingFunction")
    public native String fromJNI();

    @SuppressWarnings("JniMissingFunction")
    public native void grayPhoto(Bitmap in, Bitmap out);
}
