#!/bin/bash
# build_android.sh
# Compilation runner script to compile the complete Android JNI Shared C++ libraries
# for ALL target Android architecture ABIs (arm64-v8a, armeabi-v7a, x86, x86_64).
#
# Usage: ./build_android.sh [/path/to/android/ndk]

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NDK_PATH="${1:-$ANDROID_NDK_HOME}"

echo "=========================================================="
echo "    KOKILA TTS - Android Native C++ Build script          "
echo "=========================================================="

if [ -z "$NDK_PATH" ] || [ ! -d "$NDK_PATH" ]; then
    echo "Error: ANDROID_NDK_HOME is not set or invalid."
    echo "Please specify NDK location as argument: ./build_android.sh /path/to/ndk"
    exit 1
fi

echo "Android NDK Path: ${NDK_PATH}"
echo "Project Root:     ${PROJECT_ROOT}"

# Create build directory
BUILD_DIR="${PROJECT_ROOT}/app/src/main/cpp/build"
mkdir -p "${BUILD_DIR}"
cd "${BUILD_DIR}"

# Array of target Android ABIs
ABIs=("arm64-v8a" "armeabi-v7a" "x86_64" "x86")

for ABI in "${ABIs[@]}"; do
    echo "--------------------------------------------------------"
    echo "  Building for Target ABI: ${ABI}                       "
    echo "--------------------------------------------------------"
    
    ABI_BUILD_DIR="${BUILD_DIR}/${ABI}"
    mkdir -p "${ABI_BUILD_DIR}"
    cd "${ABI_BUILD_DIR}"
    
    # Run CMake configuration pointing to the Android NDK Toolchain file
    cmake \
        -DCMAKE_TOOLCHAIN_FILE="${NDK_PATH}/build/cmake/android.toolchain.cmake" \
        -DANDROID_ABI="${ABI}" \
        -DANDROID_PLATFORM=android-24 \
        -DANDROID_STL=c++_shared \
        -DCMAKE_BUILD_TYPE=Release \
        "${PROJECT_ROOT}/app/src/main/cpp"
        
    # Execute parallel compile
    make -j$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)
    
    # Create target jniLibs directory in App structure
    TARGET_JNI_DIR="${PROJECT_ROOT}/app/src/main/jniLibs/${ABI}"
    mkdir -p "${TARGET_JNI_DIR}"
    
    # Copy compiled .so JNI binary to target jniLibs
    cp libkokila-tts.so "${TARGET_JNI_DIR}/"
    echo "Successfully packaged binary to: ${TARGET_JNI_DIR}/libkokila-tts.so"
done

echo "=========================================================="
echo "    NATIVE COMPILATION AND ASSEMBLY COMPLETE!            "
echo "=========================================================="
