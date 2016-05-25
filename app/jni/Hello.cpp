#include "Hello.h"
#include <vector>
#include <string>
#include "StringUtils.h"

using namespace::std;

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

    vector<string> vec_1;
    StringUtils::Split(str, "#", vec_1, false);
    str = vec_1[2];

    return env->NewStringUTF(str.c_str());
}