#include <cstring>
#include <cstdlib>
#include <cstdint>

// Define structures matching the Sherpa-ONNX C-API layout
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

struct SherpaOnnxOfflineTts {
    int32_t sample_rate;
    int32_t num_speakers;
};

struct SherpaOnnxOfflineTtsGeneratedAudio {
    float* samples;
    int32_t n;
    int32_t sample_rate;
};

extern "C" {

SherpaOnnxOfflineTts* SherpaOnnxCreateOfflineTts(const SherpaOnnxOfflineTtsConfig* config) {
    auto* tts = new SherpaOnnxOfflineTts();
    tts->sample_rate = 16000;
    tts->num_speakers = 1;
    return tts;
}

void SherpaOnnxDestroyOfflineTts(SherpaOnnxOfflineTts* tts) {
    delete tts;
}

const SherpaOnnxOfflineTtsGeneratedAudio* SherpaOnnxOfflineTtsGenerate(
    const SherpaOnnxOfflineTts* tts, 
    const char* text, 
    int32_t sid, 
    float speed
) {
    auto* audio = new SherpaOnnxOfflineTtsGeneratedAudio();
    audio->sample_rate = 16000;
    audio->n = 1600; // Generate 100ms of mock silence waves
    audio->samples = new float[audio->n];
    for (int i = 0; i < audio->n; ++i) {
        audio->samples[i] = 0.0f;
    }
    return audio;
}

void SherpaOnnxDestroyOfflineTtsGeneratedAudio(const SherpaOnnxOfflineTtsGeneratedAudio* audio) {
    if (audio != nullptr) {
        delete[] audio->samples;
        delete audio;
    }
}

int32_t SherpaOnnxOfflineTtsSampleRate(const SherpaOnnxOfflineTts* tts) {
    return tts ? tts->sample_rate : 16000;
}

int32_t SherpaOnnxOfflineTtsNumSpeakers(const SherpaOnnxOfflineTts* tts) {
    return tts ? tts->num_speakers : 1;
}

} // extern "C"
