#!/bin/bash
# build_sherpa.sh
# Script to clone and compile the core k2-fsa/sherpa-onnx engine for Android.
# Integrates with both espeak-ng and ONNX Runtime to produce libsherpa-onnx-core.so.

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NDK_PATH="${1:-$ANDROID_NDK_HOME}"

echo "=========================================================="
echo "    KOKILA TTS - Compiling Sherpa-ONNX Core               "
echo "=========================================================="

if [ -z "$NDK_PATH" ] || [ ! -d "$NDK_PATH" ]; then
    echo "Error: NDK location must be set to build native libraries."
    exit 1
fi

SHERPA_SRC_DIR="${PROJECT_ROOT}/external/sherpa-onnx"
if [ ! -d "$SHERPA_SRC_DIR" ]; then
    echo "Cloning sherpa-onnx source code..."
    mkdir -p "${PROJECT_ROOT}/external"
    git clone https://github.com/k2-fsa/sherpa-onnx.git "$SHERPA_SRC_DIR"
fi

cd "$SHERPA_SRC_DIR"

echo "Configuring cmake cross compilation build..."
BUILD_DIR="${SHERPA_SRC_DIR}/build-android"
mkdir -p "${BUILD_DIR}"
cd "${BUILD_DIR}"

ABIs=("arm64-v8a" "armeabi-v7a" "x86_64" "x86")
for ABI in "${ABIs[@]}"; do
    echo "Compiling Core Engine for: ${ABI}"
    ABI_DIR="${BUILD_DIR}/${ABI}"
    mkdir -p "${ABI_DIR}"
    cd "${ABI_DIR}"
    
    cmake \
        -DCMAKE_TOOLCHAIN_FILE="${NDK_PATH}/build/cmake/android.toolchain.cmake" \
        -DANDROID_ABI="${ABI}" \
        -DANDROID_PLATFORM=android-24 \
        -DANDROID_STL=c++_shared \
        -DCMAKE_BUILD_TYPE=Release \
        -DSHERPA_ONNX_ENABLE_TTS=ON \
        -DSHERPA_ONNX_ENABLE_BINARY=OFF \
        -DSHERPA_ONNX_ENABLE_PORTAUDIO=OFF \
        ../..
        
    make -j4
    
    # Copy compiled core library
    TARGET_DIR="${PROJECT_ROOT}/app/src/main/jniLibs/${ABI}"
    mkdir -p "${TARGET_DIR}"
    cp "lib/libsherpa-onnx-core.so" "${TARGET_DIR}/"
    echo "Successfully updated libsherpa-onnx-core.so for ${ABI}"
done

echo "=========================================================="
echo "    SHERPA-ONNX CORE BUILT AND ASSEMBLED SUCCESSFULLY!     "
echo "=========================================================="
