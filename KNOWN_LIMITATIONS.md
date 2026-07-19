# Known Limitations & Performance Trade-offs

This document outlines the current boundaries and trade-offs of the Kokila Offline Neural TTS Engine. Understanding these limitations is key to optimizing development plans for subsequent stages.

---

## 1. First-Run Asset Extraction Delay

*   **Behavior**: When the application is installed and run for the first time, there is a delay of **1.5 to 3 seconds** before the JNI engine is fully initialized.
*   **Technical Reason**: On first startup, the service must unpack compressed models (`vits-en-gigaspeech.onnx`, `espeak-ng-data`, etc.) from the APK's asset bundle into the device's physical sandbox storage (`context.filesDir`).
*   **Mitigation Strategy**: The app uses a highly efficient, real-time local DSP formant fallback engine during this initial phase. Once extraction is complete, it seamlessly switches to the neural pipeline without interrupting the user.

---

## 2. Monolingual Model Boundaries

*   **Behavior**: The standard model (`vits-en-gigaspeech`) is designed and optimized specifically for English accents. 
*   **Technical Reason**: The neural weights represent English grapheme-to-phoneme pronunciations. Attempting to pass Telugu, Hindi, or Spanish characters through this engine will result in phonetic approximations or silent blocks.
*   **Mitigation Strategy**: To add support for languages such as Telugu or Spanish, you can download specialized VITS multilingual models and package them into the assets folder, specifying the correct lexicon and token paths during `NativeTtsEngine.initialize()`.

---

## 3. INT8 Quantization Trade-offs

*   **Behavior**: While INT8 quantization decreases the file size of models from ~150 MB to **~38 MB**, it introduces minor, subtle metallic sibilance artifacts under high pitch ratios.
*   **Technical Reason**: Reducing weight precision from FP32 to INT8 introduces small quantization rounding errors.
*   **Mitigation Strategy**: For high-end tablets or premium smartphones where storage is not constrained, you can package the full FP32 VITS model weight files. The native wrapper automatically detects and scales up precision at compile time.
