# Troubleshooting Guide

This guide describes common issues encountered during compile-time or runtime of the offline neural TTS engine, along with step-by-step instructions to resolve them.

---

## 1. Compile-Time Issues

### Issue 1.1: Missing prebuilt native shared libraries (`.so`)
If your Gradle build fails with compilation errors about missing symbols or missing `.so` dependencies:

*   **Cause**: You have not run the prebuilt binaries fetcher.
*   **Solution**: Run `./build_onnxruntime.sh` and `./build_sherpa.sh` before compiling the project. Ensure your `app/src/main/jniLibs` directory is populated with `libonnxruntime.so` and `libsherpa-onnx-core.so` for each ABI.

### Issue 1.2: Gradle cannot locate NDK path
Build fails with: *NDK at /path/to/ndk was not found...*

*   **Cause**: The local SDK configuration does not know where your NDK is installed.
*   **Solution**: Open `local.properties` (in your local build environment, not in AI Studio) and specify the path:
    ```properties
    ndk.dir=/Users/Username/Library/Android/sdk/ndk/25.1.8937393
    ```
    Alternatively, set the environment variable `ANDROID_NDK_HOME` in your shell profile.

---

## 2. Runtime and Service Activation Issues

### Issue 2.1: "Kokila Offline TTS" is grayed out or fails to load in Android Settings
When selecting the Preferred engine, it fails to activate or falls back to Google TTS.

*   **Cause**: Model weights were not found or asset extraction failed due to low storage space.
*   **Solution**: 
    1.  Verify that your device has at least **300 MB** of free storage.
    2.  Open Logcat in Android Studio and filter for `KokilaTts-Service`.
    3.  Check if there are messages like `Model weights missing`.
    4.  Verify that you have run `./download_models.sh` before compiling the APK, so the model weights are actually bundled inside the app's assets.

### Issue 2.2: Stuttering or choppy audio during system speech
Audio drops out or stutters when TalkBack is reading long lists of items.

*   **Cause**: High CPU usage or insufficient processing threads on the device.
*   **Solution**:
    *   Open `KokilaTtsService.kt` and locate the `nativeEngine.initialize(...)` block.
    *   Increase `numThreads` to `4` (or match the device's physical high-performance CPU core count).
    *   Ensure the model used is the **INT8 quantized** version (`vits-en-gigaspeech.onnx`), which runs significantly faster and uses less CPU than full-precision float models.
