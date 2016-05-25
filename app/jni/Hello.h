#include <jni.h>

#ifndef _HELLO_
#define _HELLO_
#ifdef __cplusplus

extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_lib_multiproprefs_1demo_act_MainActivity_fromJNI(JNIEnv* env, jobject obj);

#ifdef __cplusplus
}
#endif
#endif
