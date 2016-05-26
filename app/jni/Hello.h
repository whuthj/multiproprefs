#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <android/bitmap.h>
#include <stdint.h>

#ifndef _HELLO_
#define _HELLO_

#define LOG_TAG "HelloJni" 
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__) 
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)  

typedef struct 
{ 
    uint8_t alpha; 
    uint8_t red; 
    uint8_t green; 
    uint8_t blue; 
} argb;

#ifdef __cplusplus

extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_lib_multiproprefs_1demo_act_MainActivity_fromJNI(JNIEnv* env, jobject obj);
JNIEXPORT void JNICALL Java_com_lib_multiproprefs_1demo_act_MainActivity_grayPhoto(
    JNIEnv *env, jobject activity, jobject bmOriginal, jobject bmGray);

#ifdef __cplusplus
}
#endif
#endif
