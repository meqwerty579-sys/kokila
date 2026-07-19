// C++ Implementation wrapping Sherpa-ONNX Offline TTS C-API
#include "sherpa-onnx-wrapper.h"
#include <android/log.h>
#include <cstring>

#define TAG "KokilaTts-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// Include the exact sherpa-onnx C-API layout.
// Since headers might be external, we define the exact C-API structures here as well
// to guarantee compilation under any environment, linking to the actual .so binary!
extern "C" {

struct SherpaOnnxOfflineTtsVitsModelConfig {
    const char* model;
    const char* lexicon;
    const char* tokens;
    const char* data_dir;
    float noise_scale;
    float noise_scale_w;
    float length_scale;
};

struct SherpaOnnxOfflineTtsModelConfig {
    SherpaOnnxOfflineTtsVitsModelConfig vits;
    int32_t num_threads;
    int32_t debug;
    const char* provider;
};

struct SherpaOnnxOfflineTtsConfig {
    SherpaOnnxOfflineTtsModelConfig model;
    const char* rule_fsts;
    float max_num_sentences;
};

struct SherpaOnnxOfflineTts;

struct SherpaOnnxOfflineTtsGeneratedAudio {
    const float* samples;
    int32_t n;
    int32_t sample_rate;
};

// C-API external function declarations
extern SherpaOnnxOfflineTts* SherpaOnnxCreateOfflineTts(const SherpaOnnxOfflineTtsConfig* config);
extern void SherpaOnnxDestroyOfflineTts(SherpaOnnxOfflineTts* tts);
extern const SherpaOnnxOfflineTtsGeneratedAudio* SherpaOnnxOfflineTtsGenerate(
    const SherpaOnnxOfflineTts* tts, 
    const char* text, 
    int32_t sid, 
    float speed
);
extern void SherpaOnnxDestroyOfflineTtsGeneratedAudio(const SherpaOnnxOfflineTtsGeneratedAudio* audio);
extern int32_t SherpaOnnxOfflineTtsSampleRate(const SherpaOnnxOfflineTts* tts);
extern int32_t SherpaOnnxOfflineTtsNumSpeakers(const SherpaOnnxOfflineTts* tts);

} // extern "C"

namespace kokila {

SherpaOnnxWrapper::SherpaOnnxWrapper() {
    LOGI("SherpaOnnxWrapper C++ Instance Created");
}

SherpaOnnxWrapper::~SherpaOnnxWrapper() {
    std::lock_guard<std::mutex> lock(engine_mutex_);
    if (native_tts_instance_ptr_ != nullptr) {
        SherpaOnnxDestroyOfflineTts(static_cast<SherpaOnnxOfflineTts*>(native_tts_instance_ptr_));
        native_tts_instance_ptr_ = nullptr;
    }
    LOGI("SherpaOnnxWrapper C++ Instance Destroyed");
}

bool SherpaOnnxWrapper::Initialize(const OfflineTtsConfiguration& config) {
    std::lock_guard<std::mutex> lock(engine_mutex_);
    
    if (is_initialized_) {
        LOGI("Engine already initialized, skipping duplicate setup.");
        return true;
    }

    LOGI("Initializing Sherpa-ONNX C++ engine with:");
    LOGI(" - Model: %s", config.model.vits.model_path.c_str());
    LOGI(" - Lexicon: %s", config.model.vits.lexicon_path.c_str());
    LOGI(" - Tokens: %s", config.model.vits.tokens_path.c_str());
    LOGI(" - Threads: %d", config.model.num_threads);

    // Map internal config struct to Sherpa-ONNX C-API structure
    SherpaOnnxOfflineTtsConfig c_config;
    std::memset(&c_config, 0, sizeof(c_config));

    c_config.model.vits.model = config.model.vits.model_path.empty() ? nullptr : config.model.vits.model_path.c_str();
    c_config.model.vits.lexicon = config.model.vits.lexicon_path.empty() ? nullptr : config.model.vits.lexicon_path.c_str();
    c_config.model.vits.tokens = config.model.vits.tokens_path.empty() ? nullptr : config.model.vits.tokens_path.c_str();
    c_config.model.vits.data_dir = config.model.vits.data_dir.empty() ? nullptr : config.model.vits.data_dir.c_str();
    
    c_config.model.vits.noise_scale = config.model.vits.noise_scale;
    c_config.model.vits.noise_scale_w = config.model.vits.noise_scale_w;
    c_config.model.vits.length_scale = config.model.vits.length_scale;

    c_config.model.num_threads = config.model.num_threads;
    c_config.model.debug = config.model.debug ? 1 : 0;
    c_config.model.provider = config.model.provider.c_str();

    c_config.rule_fsts = config.rule_fsts.empty() ? nullptr : config.rule_fsts.c_str();
    c_config.max_num_sentences = config.max_num_sentences;

    // Instantiate native TTS engine using Sherpa C-API
    SherpaOnnxOfflineTts* tts = SherpaOnnxCreateOfflineTts(&c_config);
    if (tts == nullptr) {
        LOGE("FAILED to create Sherpa-ONNX Offline TTS Native Instance");
        return false;
    }

    native_tts_instance_ptr_ = static_cast<void*>(tts);
    
    // Retrieve metrics from the loaded model
    sample_rate_ = SherpaOnnxOfflineTtsSampleRate(tts);
    num_speakers_ = SherpaOnnxOfflineTtsNumSpeakers(tts);
    
    is_initialized_ = true;
    stop_requested_ = false;
    
    LOGI("Sherpa-ONNX Native Initialization Successful! Sample Rate: %d Hz, Speakers: %d", sample_rate_, num_speakers_);
    return true;
}

SynthesizedAudio SherpaOnnxWrapper::Synthesize(const std::string& text, int32_t speaker_id, float speed) {
    std::lock_guard<std::mutex> lock(engine_mutex_);
    
    SynthesizedAudio result;
    result.sample_rate = sample_rate_;

    if (!is_initialized_ || native_tts_instance_ptr_ == nullptr) {
        LOGE("Synthesize called but engine is NOT initialized");
        return result;
    }

    if (text.empty()) {
        LOGI("Synthesize received empty text request");
        return result;
    }

    LOGI("Running native neural inference for: \"%s\" [Speaker ID: %d, Speed: %.2f]", text.c_str(), speaker_id, speed);
    
    stop_requested_ = false;
    SherpaOnnxOfflineTts* tts = static_cast<SherpaOnnxOfflineTts*>(native_tts_instance_ptr_);

    // Call native synthesis generator
    const SherpaOnnxOfflineTtsGeneratedAudio* audio = SherpaOnnxOfflineTtsGenerate(tts, text.c_str(), speaker_id, speed);
    
    if (audio == nullptr) {
        LOGE("Native inference failed or returned NULL audio");
        return result;
    }

    if (stop_requested_) {
        LOGI("Synthesis loop interrupted by stop() request, discarding frames.");
        SherpaOnnxDestroyOfflineTtsGeneratedAudio(audio);
        return result;
    }

    LOGI("Inference complete. Generated %d samples at %d Hz", audio->n, audio->sample_rate);
    
    // Copy the float samples over to safe wrapper vector
    if (audio->n > 0 && audio->samples != nullptr) {
        result.samples.assign(audio->samples, audio->samples + audio->n);
        result.sample_rate = audio->sample_rate;
    }

    // Free native generated audio structure
    SherpaOnnxDestroyOfflineTtsGeneratedAudio(audio);
    
    return result;
}

void SherpaOnnxWrapper::Stop() {
    stop_requested_ = true;
    LOGI("Stop requested on native synthesis stream");
}

} // namespace kokila
