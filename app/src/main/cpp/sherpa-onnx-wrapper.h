// JNI-C++ Wrapper for Kokila Offline Neural TTS Engine (Based on Sherpa-ONNX)
#ifndef KOKILA_TTS_SHERPA_ONNX_WRAPPER_H_
#define KOKILA_TTS_SHERPA_ONNX_WRAPPER_H_

#include <string>
#include <vector>
#include <memory>
#include <mutex>

// Define structures matching the Sherpa-ONNX offline TTS models and configs
namespace kokila {

struct OfflineTtsVitsModelConfig {
    std::string model_path;
    std::string lexicon_path;
    std::string tokens_path;
    std::string data_dir;
    float noise_scale = 0.667f;
    float noise_scale_w = 0.8f;
    float length_scale = 1.0f;
};

struct OfflineTtsModelConfig {
    OfflineTtsVitsModelConfig vits;
    int32_t num_threads = 2;
    bool debug = false;
    std::string provider = "cpu"; // "cpu" or "gpu" (NNAPI/Vulkan where available)
};

struct OfflineTtsConfiguration {
    OfflineTtsModelConfig model;
    std::string rule_fsts;
    float max_num_sentences = 2.0f;
};

struct SynthesizedAudio {
    std::vector<float> samples;
    int32_t sample_rate = 16000;
};

class SherpaOnnxWrapper {
public:
    SherpaOnnxWrapper();
    ~SherpaOnnxWrapper();

    // Initialize the engine with config paths
    bool Initialize(const OfflineTtsConfiguration& config);

    // Synthesize text into raw float PCM samples
    SynthesizedAudio Synthesize(const std::string& text, int32_t speaker_id, float speed);

    // Stop active synthesis
    void Stop();

    // Query states
    bool IsInitialized() const { return is_initialized_; }
    int32_t GetSampleRate() const { return sample_rate_; }
    int32_t GetNumSpeakers() const { return num_speakers_; }

private:
    bool is_initialized_ = false;
    int32_t sample_rate_ = 16000;
    int32_t num_speakers_ = 1;
    bool stop_requested_ = false;

    std::mutex engine_mutex_;

    // Handle to the sherpa-onnx native instance (Opaque pointer to avoid header inclusion hell in JNI)
    void* native_tts_instance_ptr_ = nullptr;
};

} // namespace kokila

#endif // KOKILA_TTS_SHERPA_ONNX_WRAPPER_H_
