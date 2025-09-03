#include <jni.h>
#include "pjsipsdkOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::pjsipsdk::initialize(vm);
}
