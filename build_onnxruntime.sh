#!/bin/bash
# build_onnxruntime.sh
# Automates fetching the official ONNX Runtime Android prebuilt binaries
# and extracting the native .so files into the JNI directory structure.

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERSION="1.17.1"
AAR_URL="https://repo1.maven.org/maven2/com/microsoft/onnxruntime/onnxruntime-android/${VERSION}/onnxruntime-android-${VERSION}.aar"
TMP_DIR="${PROJECT_ROOT}/external/tmp-onnx"

echo "=========================================================="
echo "    KOKILA TTS - ONNX Runtime Fetcher                     "
echo "=========================================================="
echo "Version: ${VERSION}"
echo "Source:  ${AAR_URL}"

mkdir -p "${TMP_DIR}"
cd "${TMP_DIR}"

echo "Downloading ONNX Runtime Android Archive..."
curl -L "${AAR_URL}" -o "onnxruntime-android.aar"

echo "Extracting native shared library .so files..."
# Unzip the .aar file (which is a zip archive under the hood)
unzip -o "onnxruntime-android.aar" "jni/*"

# Copy corresponding ABIs to project's jniLibs
ABIs=("arm64-v8a" "armeabi-v7a" "x86_64" "x86")
for ABI in "${ABIs[@]}"; do
    TARGET_DIR="${PROJECT_ROOT}/app/src/main/jniLibs/${ABI}"
    mkdir -p "${TARGET_DIR}"
    
    if [ -f "jni/${ABI}/libonnxruntime.so" ]; then
        cp "jni/${ABI}/libonnxruntime.so" "${TARGET_DIR}/"
        echo "Installed libonnxruntime.so for ${ABI}"
    fi
done

# Clean up
cd "${PROJECT_ROOT}"
rm -rf "${TMP_DIR}"

echo "=========================================================="
echo "    ONNX RUNTIME PREBUILTS SET UP SUCCESSFULLY!            "
echo "=========================================================="
