#!/bin/bash
# verify_assets.sh
# Check compile-time asset requirements, verifying presence of binary 
# libraries, models, and phonemizer files before initiating a production build.

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ASSETS_DIR="${PROJECT_ROOT}/app/src/main/assets"
JNI_LIBS_DIR="${PROJECT_ROOT}/app/src/main/jniLibs"

echo "=========================================================="
echo "    KOKILA TTS - Pre-build Verification Check             "
echo "=========================================================="

FAILED=0

# 1. Check Voice Assets
echo -n "Checking Neural Model: "
if [ -f "${ASSETS_DIR}/vits-en-gigaspeech.onnx" ]; then
    echo "OK"
else
    echo "MISSING!"
    FAILED=1
fi

echo -n "Checking Tokens List: "
if [ -f "${ASSETS_DIR}/tokens.txt" ]; then
    echo "OK"
else
    echo "MISSING!"
    FAILED=1
fi

echo -n "Checking Lexicon: "
if [ -f "${ASSETS_DIR}/lexicon.txt" ]; then
    echo "OK"
else
    echo "MISSING!"
    FAILED=1
fi

echo -n "Checking espeak-ng-data directory: "
if [ -d "${ASSETS_DIR}/espeak-ng-data" ]; then
    echo "OK"
else
    echo "MISSING!"
    FAILED=1
fi

# 2. Check compiled native binaries
ABIs=("arm64-v8a" "armeabi-v7a" "x86_64" "x86")
for ABI in "${ABIs[@]}"; do
    echo -n "Checking native compilation (.so) for ${ABI}: "
    if [ -f "${JNI_LIBS_DIR}/${ABI}/libonnxruntime.so" ] && \
       [ -f "${JNI_LIBS_DIR}/${ABI}/libespeak-ng.so" ] && \
       [ -f "${JNI_LIBS_DIR}/${ABI}/libsherpa-onnx-core.so" ]; then
        echo "OK"
    else
        echo "PARTIALLY OR FULLY MISSING!"
        FAILED=1
    fi
done

echo "--------------------------------------------------------"
if [ $FAILED -eq 1 ]; then
    echo "CRITICAL: Pre-build check FAILED."
    echo "Please run download_models.sh, build_onnxruntime.sh, and build_sherpa.sh before compiling the APK."
    exit 1
else
    echo "SUCCESS: All native dependencies and voice assets verified."
    echo "You are ready to compile the production APK / AAB!"
    exit 0
fi
