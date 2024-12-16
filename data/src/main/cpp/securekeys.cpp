#include <jni.h>
#include <string>

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_project_common_utils_Environment_baseUrl(
        JNIEnv *env,
        jobject) {

    std::string baseUrl = "https://api.weatherapi.com/";
    return env->NewStringUTF(baseUrl.c_str());
}

JNIEXPORT jstring  JNICALL
Java_com_project_common_utils_Environment_apiKey(
        JNIEnv *env,
        jobject
) {
    std::string apiKey = "944cf1b576104450bdb91830241512";
    return env->NewStringUTF(apiKey.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_project_common_utils_Environment_certPinning(JNIEnv *env,
                                                      jobject) {
    std::string cert = "sha256/cyv9/qC13bZl7zH4gF…Rht+re5RJcxdf8yOE=";
    return env->NewStringUTF(cert.c_str());
}

}