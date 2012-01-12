/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "jni.h"
#include "byteswap.h"
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

int jniThrowException(JNIEnv* env, const char* className, const char* msg)
{
    jclass exceptionClass;

    exceptionClass = env->FindClass(className);
    if (exceptionClass == NULL) {
        assert(0);      /* fatal during dev; should always be fatal? */
        return -1;
    }

    if (env->ThrowNew(exceptionClass, msg) != JNI_OK) {
        assert(!"failed to throw");
    }
    return 0;
}

extern "C" JNIEXPORT jlong JNICALL Java_java_nio_luni_OSMemory_malloc(JNIEnv* env, jclass, jint size) {
    /* mzechner, not on dalvik, so no need to report
	jboolean allowed = env->CallBooleanMethod(gIDCache.runtimeInstance,
            gIDCache.method_trackExternalAllocation, extern "C" JNIEXPORT_cast<jlong>(size));
    if (!allowed) {
        LOGW("External allocation of %d bytes was rejected\n", size);
        jniThrowException(env, "java/lang/OutOfMemoryError", NULL);
        return 0;
    }*/

    void* block = malloc(size + sizeof(jlong));
    if (block == NULL) {
        jniThrowException(env, "java/lang/OutOfMemoryError", NULL);
        return 0;
    }

    /*
     * Tuck a copy of the size at the head of the buffer.  We need this
     * so JNICALL Java_java_nio_luni_OSMemory_free() knows how much memory is being freed.
     */
    jlong* result = (jlong*)(block);
    *result++ = size;
    return (jlong)(result);
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_free(JNIEnv*, jclass, jlong address) {
    jlong* p = (jlong*)(address);
	/* mzechner not on davlik
    jlong size = *--p;
    env->CallVoidMethod(gIDCache.runtimeInstance, gIDCache.method_trackExternalFree, size); */
    free((void*)p);
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_memset(JNIEnv*, jclass, jlong dstAddress, jbyte value, jlong length) {
    memset((void*)(dstAddress), value, length);
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_memmove(JNIEnv*, jclass, jlong dstAddress, jlong srcAddress, jlong length) {
    memmove((void*)(dstAddress), (const void*)(srcAddress), length);
}

extern "C" JNIEXPORT jbyte JNICALL Java_java_nio_luni_OSMemory_getByte(JNIEnv*, jclass, jlong srcAddress) {
    return *((const jbyte*)(srcAddress));
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_getByteArray(JNIEnv* env, jclass, jlong srcAddress,
        jbyteArray dst, jint offset, jint length) {
    env->SetByteArrayRegion(dst, offset, length, (const jbyte*)(srcAddress));
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setByte(JNIEnv*, jclass, jlong dstAddress, jbyte value) {
    *((jbyte*)(dstAddress)) = value;
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setByteArray(JNIEnv* env, jclass,
        jlong dstAddress, jbyteArray src, jint offset, jint length) {
    env->GetByteArrayRegion(src, offset, length, (jbyte*)(dstAddress));
}

extern "C" JNIEXPORT inline void swapShorts(jshort* shorts, int count) {
    jbyte *srcShorts = (jbyte*)(shorts);
    jbyte *dstShorts = srcShorts;
    // Do 32-bit swaps as long as possible...
    jint* dst = (jint*)(dstShorts);
    const jint* src = (const jint*)(srcShorts);
    for (int i = 0; i < count / 2; ++i) {
        jint v = *src++;                            // v=ABCD
        v = bswap_32(v);                            // v=DCBA
        jint v2 = (v << 16) | ((v >> 16) & 0xffff); // v=BADC
        *dst++ = v2;
    }
    // ...with one last 16-bit swap if necessary.
    if ((count % 2) != 0) {
        jshort v = *((const jshort*)src);
        *((jshort*)(dst)) = bswap_16(v);
    }
}

extern "C" JNIEXPORT void swapInts(jint* ints, int count) {
    jint* src = ints;
    jint* dst = src;
    for (int i = 0; i < count; ++i) {
	jint v = *src++;
	*dst++ = bswap_32(v);
    }
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setFloatArray(JNIEnv* env, jclass, jlong dstAddress,
        jfloatArray src, jint offset, jint length, jboolean swap) {
    jfloat* dst = (jfloat*)(dstAddress);
    env->GetFloatArrayRegion(src, offset, length, dst);
    if (swap) {
        swapInts((jint*)(dst), length);
    }
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setIntArray(JNIEnv* env, jclass,
       jlong dstAddress, jintArray src, jint offset, jint length, jboolean swap) {
    jint* dst = (jint*)(dstAddress);
    env->GetIntArrayRegion(src, offset, length, dst);
    if (swap) {
        swapInts(dst, length);
    }
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setShortArray(JNIEnv* env, jclass,
       jlong dstAddress, jshortArray src, jint offset, jint length, jboolean swap) {
    jshort* dst = (jshort*)(dstAddress);
    env->GetShortArrayRegion(src, offset, length, dst);
    if (swap) {
        swapShorts(dst, length);
    }
}

extern "C" JNIEXPORT jshort JNICALL Java_java_nio_luni_OSMemory_getShort(JNIEnv*, jclass, jlong srcAddress) {
    if ((srcAddress & 0x1) == 0) {
        return *((const jshort*)(srcAddress));
    } else {
        // Handle unaligned memory access one byte at a time
        jshort result;
        const jbyte* src = (const jbyte*)(srcAddress);
        jbyte* dst = (jbyte*)(&result);
        dst[0] = src[0];
        dst[1] = src[1];
        return result;
    }
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setShort(JNIEnv*, jclass, jlong dstAddress, jshort value) {
    if ((dstAddress & 0x1) == 0) {
        *((jshort*)(dstAddress)) = value;
    } else {
        // Handle unaligned memory access one byte at a time
        const jbyte* src = (const jbyte*)(&value);
        jbyte* dst = (jbyte*)(dstAddress);
        dst[0] = src[0];
        dst[1] = src[1];
    }
}

extern "C" JNIEXPORT jint JNICALL Java_java_nio_luni_OSMemory_getInt(JNIEnv*, jclass, jlong srcAddress) {
    if ((srcAddress & 0x3) == 0) {
        return *((const jint*)(srcAddress));
    } else {
        // Handle unaligned memory access one byte at a time
        jint result;
        const jbyte* src = (const jbyte*)(srcAddress);
        jbyte* dst = (jbyte*)(&result);
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
        dst[3] = src[3];
        return result;
    }
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setInt(JNIEnv*, jclass, jlong dstAddress, jint value) {
    if ((dstAddress & 0x3) == 0) {
        (*(jint*)(dstAddress)) = value;
    } else {
        // Handle unaligned memory access one byte at a time
        const jbyte* src = (const jbyte*)(&value);
        jbyte* dst = (jbyte*)(dstAddress);
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
        dst[3] = src[3];
    }
}

template <typename T> T get(jlong srcAddress) {
    if ((srcAddress & (sizeof(T) - 1)) == 0) {
        return *((const T*)(srcAddress));
    } else {
        // Cast to void* so GCC can't assume correct alignment and optimize this out.
        const void* src = (const void*)(srcAddress);
        T result;
        memcpy(&result, src, sizeof(T));
        return result;
    }
}

template <typename T> void set(jlong dstAddress, T value) {
    if ((dstAddress & (sizeof(T) - 1)) == 0) {
        *((T*)(dstAddress)) = value;
    } else {
        // Cast to void* so GCC can't assume correct alignment and optimize this out.
        void* dst = (void*)(dstAddress);
        memcpy(dst, &value, sizeof(T));
    }
}

extern "C" JNIEXPORT jlong JNICALL Java_java_nio_luni_OSMemory_getLong(JNIEnv*, jclass, jlong srcAddress) {
    return get<jlong>(srcAddress);
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setLong(JNIEnv*, jclass, jlong dstAddress, jlong value) {
    set<jlong>(dstAddress, value);
}

extern "C" JNIEXPORT jfloat JNICALL Java_java_nio_luni_OSMemory_getFloat(JNIEnv*, jclass, jlong srcAddress) {
    return get<jfloat>(srcAddress);
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setFloat(JNIEnv*, jclass, jlong dstAddress, jfloat value) {
    set<jfloat>(dstAddress, value);
}

extern "C" JNIEXPORT jdouble JNICALL Java_java_nio_luni_OSMemory_getDouble(JNIEnv*, jclass, jlong srcAddress) {
    return get<jdouble>(srcAddress);
}

extern "C" JNIEXPORT void JNICALL Java_java_nio_luni_OSMemory_setDouble(JNIEnv*, jclass, jlong dstAddress, jdouble value) {
    set<jdouble>(dstAddress, value);
}