// JNI Bridge for Kokila Offline Neural TTS Engine
#include <jni.h>
#include <string>
#include <android/log.h>
#include "sherpa-onnx-wrapper.h"

#define TAG "KokilaTts-JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" {

// Helper to convert jstring to std::string safely
std::string jstring2string(JNIEnv* env, jstring jstr) {
    if (!jstr) {
        return "";
    }
    const char* raw = env->GetStringUTFChars(jstr, nullptr);
    std::string result(raw);
    env->ReleaseStringUTFChars(jstr, raw);
    return result;
}

JNIEXPORT jlong JNICALL
Java_com_example_NativeTtsEngine_initNative(
    JNIEnv* env,
    jobject thiz,
    jstring model_path,
    jstring lexicon_path,
    jstring tokens_path,
    jstring data_dir,
    jint num_threads,
    jfloat noise_scale,
    jfloat noise_scale_w,
    jfloat length_scale,
    jstring provider
) {
    LOGI("JNI: initNative() invoked");

    kokila::OfflineTtsConfiguration config;
    config.model.vits.model_path = jstring2string(env, model_path);
    config.model.vits.lexicon_path = jstring2string(env, lexicon_path);
    config.model.vits.tokens_path = jstring2string(env, tokens_path);
    config.model.vits.data_dir = jstring2string(env, data_dir);
    config.model.vits.noise_scale = noise_scale;
    config.model.vits.noise_scale_w = noise_scale_w;
    config.model.vits.length_scale = length_scale;
    
    config.model.num_threads = num_threads;
    config.model.debug = true; // Enable internal model debugging
    config.model.provider = jstring2string(env, provider);
    if (config.model.provider.empty()) {
        config.model.provider = "cpu";
    }

    config.rule_fsts = ""; // Optional text normalization rules FST
    config.max_num_sentences = 2.0f;

    // Create C++ wrapper instance
    auto* wrapper = new kokila::SherpaOnnxWrapper();
    bool success = wrapper->Initialize(config);

    if (!success) {
        LOGE("JNI: Native initialization FAILED");
        delete wrapper;
        return 0;
    }

    LOGI("JNI: Native engine created at address: %p", wrapper);
    return reinterpret_cast<jlong>(wrapper);
}

JNIEXPORT void JNICALL
Java_com_example_NativeTtsEngine_destroyNative(
    JNIEnv* env,
    jobject thiz,
    jlong engine_ptr
) {
    LOGI("JNI: destroyNative() invoked");
    if (engine_ptr != 0) {
        auto* wrapper = reinterpret_cast<kokila::SherpaOnnxWrapper*>(engine_ptr);
        delete wrapper;
        LOGI("JNI: Native engine successfully destroyed");
    }
}

JNIEXPORT jfloatArray JNICALL
Java_com_example_NativeTtsEngine_synthesizeNative(
    JNIEnv* env,
    jobject thiz,
    jlong engine_ptr,
    jstring text,
    jint speaker_id,
    jfloat speed
) {
    if (engine_ptr == 0) {
        LOGE("JNI: synthesizeNative called with null engine pointer");
        return nullptr;
    }

    std::string text_str = jstring2string(env, text);
    auto* wrapper = reinterpret_cast<kokila::SherpaOnnxWrapper*>(engine_ptr);

    // Call synthesis in native thread-safe wrapper
    kokila::SynthesizedAudio audio = wrapper->Synthesize(text_str, speaker_id, speed);

    // Prepare float array return to Kotlin JVM
    jfloatArray result_array = env->NewFloatArray(audio.samples.size());
    if (result_array != nullptr) {
        env->SetFloatArrayRegion(result_array, 0, audio.samples.size(), audio.samples.data());
    }

    return result_array;
}

JNIEXPORT void JNICALL
Java_com_example_NativeTtsEngine_stopNative(
    JNIEnv* env,
    jobject thiz,
    jlong engine_ptr
) {
    if (engine_ptr != 0) {
        auto* wrapper = reinterpret_cast<kokila::SherpaOnnxWrapper*>(engine_ptr);
        wrapper->Stop();
    }
}

JNIEXPORT jint JNICALL
Java_com_example_NativeTtsEngine_getSampleRateNative(
    JNIEnv* env,
    jobject thiz,
    jlong engine_ptr
) {
    if (engine_ptr != 0) {
        auto* wrapper = reinterpret_cast<kokila::SherpaOnnxWrapper*>(engine_ptr);
        return wrapper->GetSampleRate();
    }
    return 16000;
}

JNIEXPORT jint JNICALL
Java_com_example_NativeTtsEngine_getNumSpeakersNative(
    JNIEnv* env,
    jobject thiz,
    jlong engine_ptr
) {
    if (engine_ptr != 0) {
        auto* wrapper = reinterpret_cast<kokila::SherpaOnnxWrapper*>(engine_ptr);
        return wrapper->GetNumSpeakers();
    }
    return 1;
}

} // extern "C"
