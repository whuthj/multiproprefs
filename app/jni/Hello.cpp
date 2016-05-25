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

jobject getInstance(JNIEnv* env, jclass obj_class)
{
    jmethodID construction_id = env->GetMethodID(obj_class, "<init>", "()V");
    jobject obj = env->NewObject(obj_class, construction_id);
    return obj;
}