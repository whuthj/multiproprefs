#ifndef __CRASHHANDLER_H__
#define __CRASHHANDLER_H__

#include <jni.h>
#include <stdio.h>

/*
 * 注册崩溃异常处理函数，同时初始化异常处理所需数据。
 * 请在JNI_OnLoad()函数中调用本函数。
 */
void RegisterCrashHandler(JNIEnv* env);

/*
 * 准备处理崩溃异常。
 * 请在所有jni函数入口调用本函数。
 */
void ReadyToHandlerCrash(JNIEnv* env);


class ReadyToHandlerCrashGuard
{
    public:
        ReadyToHandlerCrashGuard(JNIEnv* env)
        {
            ReadyToHandlerCrash(env);
        }

        ~ReadyToHandlerCrashGuard()
        {
            ReadyToHandlerCrash(NULL);
        }
};


#endif // __CRASHHANDLER_H__
