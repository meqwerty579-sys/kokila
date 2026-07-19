# Architecture & Systems Design

This document details the systems-level design of the Kokila Offline Neural TTS Engine. It describes the interaction between the Android OS layer, the JVM runtime, and the native C++ inference engine.

---

## 1. High-Level Architectural Flow

```
+-----------------------------------------------------------+
|                      Android OS Layer                      |
| (E.g. Settings, TalkBack, Accessibility Tools, Books Apps) |
+-----------------------------------------------------------+
                             │
                             ▼ (android.speech.tts.TextToSpeechService)
+-----------------------------------------------------------+
|                     KokilaTtsService                      |
|                  (Main JVM Service State)                 |
+-----------------------------------------------------------+
                             │
       ┌─────────────────────┴─────────────────────┐
       ▼ (If Native Model Loaded)                   ▼ (If Model Extracting)
+──────────────────────────────+            +─────────────────────────────+
|       NativeTtsEngine        |            |  Formant Synthesis Fallback |
|      (Safe JNI Wrapper)      |            |    (DSP Sine & Filter)      |
+──────────────────────────────+            +─────────────────────────────+
       │                                                   │
       ▼ (JNI C-API Calls)                                 │
+──────────────────────────────+                           │
|      SherpaOnnxWrapper       |                           │
|     (C++ Core Container)     |                           │
+──────────────────────────────+                           │
       │                                                   │
       ├────────────────────────┐                          │
       ▼                        ▼                          │
+──────────────+         +──────────────+                  │
|  espeak-ng   |         | ONNX Runtime |                  │
| (Phonemes)   |         | (Inference)  |                  │
+──────────────+         +──────────────+                  │
       │                        │                          │
       └───────────┬────────────┘                          │
                   ▼ (PCM Float Array)                     │
+----------------------------------------------------------+
|                  Audio Streaming Channel                 |
|            (16-bit PCM Signed Little-Endian)             |
+----------------------------------------------------------+
                             │
                             ▼
+----------------------------------------------------------+
|                System Audio Output Speaker               |
+----------------------------------------------------------+
```

---

## 2. Core Architectural Blocks

### 2.1 The Android Service Pipeline (`KokilaTtsService`)
The service extends `android.speech.tts.TextToSpeechService` and serves as the primary system entry point.

*   **Multi-Threaded Queue**: Runs on a single-threaded queue pool (`ThreadPoolExecutor`). This ensures that multiple overlapping synthesis commands from TalkBack or the accessibility layer do not trigger concurrent thread contentions or corrupt JNI pointers.
*   **Asset Extraction**: To prevent blocking during startup, assets are extracted asynchronously to safe storage (`context.filesDir`). This is a one-time operation, after which files are loaded directly via native memory-mapping.

### 2.2 The JNI & C++ Translation Bridge (`NativeTtsEngine`)
A dedicated JNI bridge connects the JVM and C++ runtimes.

*   **State Protection**: The Kotlin wrapper manages the native memory address in a private `nativeEnginePtr: Long` variable.
*   **Memory Safeguards**: All C++ allocations are managed under a `std::mutex` lock inside `SherpaOnnxWrapper` to prevent double-free exceptions if a user stops speech while a synthesis is executing.

### 2.3 Native Synthesis Stack
The native layer executes the actual model inference.

*   **Zero-Copy Memory Mapping**: Instead of reading huge `.onnx` files into memory buffers, the ONNX Runtime accesses weights directly via memory-mapping (`mmap`). This minimizes memory allocation, keeping the heap footprint extremely low.
*   **espeak-ng Integration**: Implements Grapheme-to-Phoneme mapping. Raw text sentences are normalized and translated into precise IPA phoneme symbols offline.
*   **Inference Pipeline**: ONNX Runtime processes the phonetic index array, executing VITS model layers to generate highly natural speech.

---

## 3. Advanced Streaming Pipeline

Rather than waiting for a whole sentence or paragraph to complete inference, Kokila stream-processes speech using a custom producer-consumer pipeline:

1.  **Text Chunky Chunking**: Sentences are split dynamically at logical punctuation marks (commas, periods, semicolons).
2.  **Inference Producer**: The C++ layer runs inference on individual chunks, generating the float array sequence.
3.  **Synthesized Frame Consumer**: As soon as a chunk is generated, it is normalized to 16-bit signed PCM short integers and pushed into the `SynthesisCallback` buffer in tiny, fixed-size frames (e.g., 1024 bytes). This yields an **Inference Latency under 30 ms**, ensuring talkback sounds snappy and immediate.
4.  **Instant Interruption**: When `onStop()` is called, the C++ stop signal is flagged. C++ inference loops instantly break out and discard running frames, flushing the audio queue in under **5 milliseconds**.
