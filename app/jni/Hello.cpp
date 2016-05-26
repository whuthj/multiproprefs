#include "Hello.h"
#include <vector>
#include <string>
#include <thread>
#include <future>
#include <tuple>
#include "StringUtils.h"

using namespace::std;

jobject getInstance(JNIEnv* env, jclass obj_class);

void threadFunc1(tuple<int, float, double> t)
{

}

JNIEXPORT jstring JNICALL Java_com_lib_multiproprefs_1demo_act_MainActivity_fromJNI(JNIEnv* env, jobject obj)
{
    vector<string> vec;
    vec.push_back("From#NDK");
    vec.push_back("#Hello");

    string str = "";
    vector<string>::const_iterator itr = vec.begin();
    while (itr != vec.end())
    {
        str += *itr;
        ++itr;
    }

    auto tuple_1 = tuple<int, float, double>(1, 1.2f, 1.23);
    thread t(threadFunc1, tuple_1);
    t.join();

    thread t_1([&]() {
        vector<string> vec_1;
        StringUtils::Split(str, "#", vec_1, false);
        str = vec_1[2];
    });
    t_1.join();

    auto val = env->NewStringUTF(str.c_str());
    return val;
}

JNIEXPORT void JNICALL Java_com_lib_multiproprefs_1demo_act_MainActivity_grayPhoto(
    JNIEnv *env, jobject activity, jobject bmOriginal, jobject bmGray)
{
    AndroidBitmapInfo origanalColor;
    AndroidBitmapInfo infogray;
    void* pixelscolor;
    void* pixelsgray;
    int ret;
    int y;
    int x;
    LOGI("In convertToGray");

    if ((ret = AndroidBitmap_getInfo(env, bmOriginal, &origanalColor)) < 0)
    {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if ((ret = AndroidBitmap_getInfo(env, bmGray, &infogray)) < 0)
    {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    LOGI("original image :: width is %d; height is %d; stride is %d; format is %d;flags is	%d,stride is %u",
        origanalColor.width, origanalColor.height, origanalColor.stride, origanalColor.format, origanalColor.flags, origanalColor.stride);

    if (origanalColor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }

    if (origanalColor.format == ANDROID_BITMAP_FORMAT_RGB_565)
    {
        LOGI("Original Image is ANDROID_BITMAP_FORMAT_RGB_565");
        if ((ret = AndroidBitmap_lockPixels(env, bmOriginal, &pixelscolor)) < 0)
        {
            LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        }
        if ((ret = AndroidBitmap_lockPixels(env, bmGray, &pixelsgray)) < 0)
        {
            LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        }
        // modify pixels with image processing algorithm 
        for (y = 0; y < origanalColor.height; y++)
        {
            uint16_t * line = (uint16_t *)pixelscolor;
            uint8_t * grayline = (uint8_t *)pixelsgray;

            for (x = 0; x < origanalColor.width; x++)
            {
                grayline[x] = (uint8_t)(((line[x] >> 11 << 3) + (line[x] >> 5 & 63 * 16) + (line[x] & 31 * 8)) / 3);
                //LOGI("%d %d %d %d", line[x].alpha, line[x].red, line[x].green, line[x].blue);

                if (x == 0)
                {
                    LOGI("line:%o grayline %o ", line[x], grayline[x]);
                }
            }
            pixelscolor = (char *)pixelscolor + origanalColor.stride;
            pixelsgray = (char *)pixelsgray + infogray.stride;
        }
        LOGI("unlocking pixels");
        AndroidBitmap_unlockPixels(env, bmOriginal);
        AndroidBitmap_unlockPixels(env, bmGray);
        LOGI("Return !! ");
        return;
    }

    LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d;flags is %d %d,stride is %u",
        infogray.width, infogray.height, infogray.stride, infogray.format, infogray.flags, infogray.stride);

    if (infogray.format != ANDROID_BITMAP_FORMAT_A_8)
    {
        LOGE("Bitmap format is not A_8 !");
        return;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bmOriginal, &pixelscolor)) < 0)
    {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    if ((ret = AndroidBitmap_lockPixels(env, bmGray, &pixelsgray)) < 0)
    {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    // modify pixels with image processing algorithm 

    for (y = 0; y < origanalColor.height; y++)
    {
        argb * line = (argb *)pixelscolor;
        uint8_t * grayline = (uint8_t *)pixelsgray;
        for (x = 0; x < origanalColor.width; x++)
        {
            grayline[x] = (line[x].red + line[x].green + line[x].blue) / 3;
        }

        pixelscolor = (char *)pixelscolor + origanalColor.stride;
        pixelsgray = (char *)pixelsgray + infogray.stride;
    }

    LOGI("unlocking pixels");
    AndroidBitmap_unlockPixels(env, bmOriginal);
    AndroidBitmap_unlockPixels(env, bmGray);
}

jobject getInstance(JNIEnv* env, jclass obj_class)
{
    jmethodID construction_id = env->GetMethodID(obj_class, "<init>", "()V");
    jobject obj = env->NewObject(obj_class, construction_id);
    return obj;
}