#include <jni.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/wait.h>
#include <utime.h>
#include <sys/system_properties.h>

#ifndef _HELLO_
#define _HELLO_
#ifdef __cplusplus

extern "C" {
#endif

#include <sys/system_properties.h>
int ACDD_property_get(const char *key, char *value, const char *default_value)
{
    int len;
    len = __system_property_get(key, value);
    if(len > 0) {
        return len;
    }

    if(default_value) {
        len = strlen(default_value);
        memcpy(value, default_value, len + 1);
    }
    return len;
}
JNIEXPORT jstring JNICALL Java_com_lib_multiproprefs_1demo_act_MainActivity_fromJNI(JNIEnv* env, jobject obj);

#ifdef __cplusplus
}
#endif
#endif
