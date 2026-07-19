# Kokila Offline Neural TTS Engine (Based on Sherpa-ONNX)

Kokila is a real, production-quality, fully offline Neural Text-To-Speech (TTS) engine for Android devices. Based on the `k2-fsa/sherpa-onnx` framework, it integrates with the Android OS system settings, allowing users to select "Kokila Offline TTS" as their primary system voice synthesizer. It performs genuine local neural synthesis, never utilizing cloud APIs or delegating to other services.

## Core Highlights
*   **100% Offline Neural Synthesis**: Powered by highly optimized INT8 quantized VITS neural voice models running directly on device via ONNX Runtime.
*   **System Integration**: Registers at the Android system level. Appears directly inside Android Accessibility/Language Settings and supports streaming speech across any system app.
*   **Dual Pipeline Architecture**: Combines an offline ONNX neural inference stream with a highly optimized local DSP formant fallback engine to ensure guaranteed speech output in all system scenarios.
*   **Low Latency Streaming**: Features a continuous producer-consumer thread pipeline with ring buffering and custom latency-controlled audio tracks to prevent voice stuttering.

---

## Repository Architecture

```
.
├── app/
│   ├── build.gradle.kts           # App Gradle Configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml # Service permissions, intents, and metadata
│   │   │   ├── cpp/
│   │   │   │   ├── CMakeLists.txt  # Native CMake configuration
│   │   │   │   ├── kokila-tts-jni.cc # JNI translation bridge
│   │   │   │   ├── sherpa-onnx-wrapper.h # C++ model wrapper definitions
│   │   │   │   └── sherpa-onnx-wrapper.cc # C++ model wrapper implementation
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt    # Sleek System UI and Diagnostic dashboard
│   │   │   │   ├── KokilaTtsService.kt # System-level TextToSpeechService pipeline
│   │   │   │   └── NativeTtsEngine.kt # Safe JVM JNI bridge mapping
│   │   │   └── res/
│   │   │       └── xml/tts_engine.xml  # System TTS registry definition
│   │   └── test/
│   │       └── java/com/example/
│   │           └── TtsEngineTest.kt   # System pipeline and lifecycle tests
├── .github/
│   └── workflows/
│       └── build.yml               # Automated CI Workflow
├── download_models.sh             # Voice model and dictionary fetcher (Linux/macOS)
├── download_models.ps1            # Voice model and dictionary fetcher (Windows)
├── build_android.sh               # JNI compilation script for Android target ABIs
├── build_espeak.sh                # Cross-compile script for espeak-ng engine
├── build_onnxruntime.sh           # prebuilt ORT .so extraction utility
├── build_sherpa.sh                # Cross-compile script for sherpa-onnx libraries
├── verify_assets.sh               # Check compile-time asset requirements
├── release.sh                     # Automated Release APK assembly script
├── README.md                      # Core introduction guide
├── BUILD.md                       # Local compilation instruction set
├── INSTALL.md                     # Device flashing and activation steps
├── ARCHITECTURE.md                # System engineering documentation
├── TROUBLESHOOTING.md             # Common developer error fixes
└── KNOWN_LIMITATIONS.md           # Model scope and memory optimizations
```

---

## Operational Mechanics Overview

1.  **System Binding**: Android OS loads `KokilaTtsService` using the standard `android.intent.action.TTS_SERVICE` intent.
2.  **Asset Loading**: When initialized, the service extracts voice model files (`vits-en-gigaspeech.onnx`), token definitions, and `espeak-ng` assets to the safe sandboxed space (`context.filesDir`).
3.  **Memory Mapping**: The C++ layer memory-maps the assets directly using `mmap` to keep a zero-copy footprint and avoid standard JVM memory pressure issues.
4.  **Phonemization & Tokenization**: Text sentences received in `onSynthesizeText` are parsed into phonemes using `espeak-ng`, which are mapped to target index tokens.
5.  **Neural Inference**: The index tokens are fed into ONNX Runtime, executing the quantized neural model layers to yield raw PCM audio samples.
6.  **Low Latency Audio Streaming**: Samples are queued in a synchronized pipeline and fed in real-time to the OS using the standard `SynthesisCallback` stream, preventing delays.

For detailed development setup and building, refer to the [BUILD.md](BUILD.md) file.
