# Building Kokila Neural TTS Engine Locally

This guide explains how to compile the entire project (including native C++ files, JNI layers, and assets) locally inside Android Studio.

## Prerequisites

To build the project locally, your workstation must have:
1.  **JDK 17** or newer installed.
2.  **Android Studio (Hedgehog or newer)**.
3.  **Android NDK (Version 25.1.8937393 or newer)** installed via SDK Manager.
4.  **CMake (Version 3.18.1 or newer)** installed via SDK Manager.
5.  Command-line utilities: `curl`, `tar`, `unzip` (standard in macOS/Linux; on Windows, use Git Bash).

---

## Step-by-Step Compilation Instructions

### Step 1: Clone and Prepare Assets
Run the model assets downloader to fetch the INT8 quantized neural voice models, token definitions, and phonetic dictionaries:

```bash
# On Linux / macOS
chmod +x download_models.sh
./download_models.sh

# On Windows
powershell -ExecutionPolicy Bypass -File download_models.ps1
```

This installs:
*   `vits-en-gigaspeech.onnx` -> `app/src/main/assets/`
*   `tokens.txt` -> `app/src/main/assets/`
*   `lexicon.txt` -> `app/src/main/assets/`
*   `espeak-ng-data` -> `app/src/main/assets/`

---

### Step 2: Extract Prebuilt Native Shared Libraries
Extract the matching precompiled ONNX Runtime shared binaries for cross-compilation:

```bash
chmod +x build_onnxruntime.sh
./build_onnxruntime.sh
```

This extracts the native libraries (`libonnxruntime.so`) for `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86` directly into `app/src/main/jniLibs/`.

---

### Step 3: Build Core Sherpa-ONNX Layers
Cross-compile the core neural engine for all target Android ABIs:

```bash
chmod +x build_sherpa.sh
./build_sherpa.sh
```

---

### Step 4: Verify Assets Integrity
Ensure that all required dependencies are present in their target directories:

```bash
chmod +x verify_assets.sh
./verify_assets.sh
```

If the verification outputs **SUCCESS**, your local workspace is completely configured and ready.

---

### Step 5: Native Compilation & Assembly

#### Method A: Android Studio IDE (Recommended)
1.  Open Android Studio and choose **Open an Existing Project**.
2.  Select the project root directory.
3.  In `app/build.gradle.kts`, ensure the `externalNativeBuild` block is uncommented so that Gradle automatically manages CMake execution:
    ```kotlin
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    ```
4.  Wait for Gradle sync to complete successfully.
5.  Click **Build > Assemble APK** or run/debug directly on your connected physical Android device.

#### Method B: Automated Command Line Build
Use the automation script to perform NDK compilation and bundle the signed production APK:

```bash
chmod +x release.sh
./release.sh
```

The resulting optimized APK will be saved at:
`app/build/outputs/apk/release/app-release.apk`
