#include <jni.h>

JNIEXPORT jbooleanJNICALL
Java_ru_nt202_decoder_MoveDetection_moveHappened(JNIEnv
*env, jclass type,
        jstring url_)
{
const char *url = (*env)->GetStringUTFChars(env, url_, 0);

// TODO

(*env)->ReleaseStringUTFChars(env, url_, url
JNIEXPORT jboolean
);
}