#include "crashhandler.h"

#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <time.h>

#include "jnidatatypeutil.h"



static pthread_key_t    sg_envKey = 0;
static jclass           sg_clsJniCrashHandler = NULL;
static jmethodID        sg_midNativeReportData = NULL;



void RegisterCrashHandler(JNIEnv* env)
{
    ::pthread_key_create(&sg_envKey, NULL);
    ::pthread_setspecific(sg_envKey, NULL);

    jclass clsJniCrashHandler = env->FindClass("com/cleanmaster/util/a");
    if (env->ExceptionCheck() || NULL == clsJniCrashHandler)
    {
        env->ExceptionClear();
        return;
    }

    if (NULL != sg_clsJniCrashHandler)
    {
        env->DeleteGlobalRef(sg_clsJniCrashHandler);
        sg_clsJniCrashHandler = NULL;
    }
    sg_clsJniCrashHandler = (jclass)env->NewGlobalRef(clsJniCrashHandler);

    sg_midNativeReportData = env->GetStaticMethodID(sg_clsJniCrashHandler, "a", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (env->ExceptionCheck())
    {
        env->ExceptionClear();
        sg_midNativeReportData = NULL;
    }
}

void ReadyToHandlerCrash(JNIEnv* env)
{
    if (0 != sg_envKey)
    {
        ::pthread_setspecific(sg_envKey, env);
    }
}

void KInfocReportData(const char* szTableName, const char* szData)
{
    if (NULL == szTableName || NULL == szData)
    {
        return;
    }

    JNIEnv *env = (JNIEnv*)::pthread_getspecific(sg_envKey);
    if (NULL != env && NULL != sg_clsJniCrashHandler && NULL != sg_midNativeReportData)
    {
        const_char_ptr_2_jstring jstrTableName(env, szTableName);
        const_char_ptr_2_jstring jstrData(env, szData);
        env->CallStaticVoidMethod(
                sg_clsJniCrashHandler, 
                sg_midNativeReportData, 
                jstrTableName.get(), 
                jstrData.get());
        if (env->ExceptionCheck())
        {
            env->ExceptionClear();
        }
    }
}

