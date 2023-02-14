#include <jni.h>


extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_tuanictu97_codedynamic_CodeLoadLoadingController_getDemoCodeLoading(JNIEnv *env,
                                                                                   jobject thiz) {
    return env->NewStringUTF("This is a demo using code loading android.")
}