#include "Hello.h"
#include <vector>
#include <string>
#include <thread>
#include "StringUtils.h"

using namespace::std;

void threadFunc1()
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

    thread t(threadFunc1);
    t.join();

    vector<string> vec_1;
    StringUtils::Split(str, "#", vec_1, false);
    str = vec_1[2];

    auto val = env->NewStringUTF(str.c_str());
    return val;
}