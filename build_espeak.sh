#!/bin/bash
# build_espeak.sh
# Script to cross-compile the espeak-ng Grapheme-To-Phoneme converter for Android devices.
# This produces the libespeak-ng.so required for offline token processing.

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NDK_PATH="${1:-$ANDROID_NDK_HOME}"

echo "=========================================================="
echo "    KOKILA TTS - Cross-compiling espeak-ng                "
echo "=========================================================="

if [ -z "$NDK_PATH" ] || [ ! -d "$NDK_PATH" ]; then
    echo "Error: NDK location must be set to build native libraries."
    exit 1
fi

ESPEAK_SRC_DIR="${PROJECT_ROOT}/external/espeak-ng"
if [ ! -d "$ESPEAK_SRC_DIR" ]; then
    echo "Cloning espeak-ng source code repository..."
    mkdir -p "${PROJECT_ROOT}/external"
    git clone https://github.com/espeak-ng/espeak-ng.git "$ESPEAK_SRC_DIR"
fi

cd "$ESPEAK_SRC_DIR"

# Ensure clean build slate
git clean -xdf

echo "Running autogen and configure..."
./autogen.sh

# Cross-compile for ARM64 as target standard
echo "Configuring cross compilation toolchain..."
export CC="${NDK_PATH}/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android24-clang"
export AR="${NDK_PATH}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar"
export RANLIB="${NDK_PATH}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ranlib"

./configure \
    --host=aarch64-linux-android \
    --prefix="${PROJECT_ROOT}/external/build-espeak-ng" \
    --with-extdict=no \
    --with-speech=no \
    --with-klatt=no \
    --with-mbrola=no \
    --enable-shared=yes \
    --enable-static=no

echo "Compiling espeak-ng shared objects..."
make -j4
make install

# Copy outputs
mkdir -p "${PROJECT_ROOT}/app/src/main/jniLibs/arm64-v8a"
cp "${PROJECT_ROOT}/external/build-espeak-ng/lib/libespeak-ng.so" "${PROJECT_ROOT}/app/src/main/jniLibs/arm64-v8a/"

echo "--------------------------------------------------------"
echo "  espeak-ng compiled successfully for arm64-v8a!"
echo "--------------------------------------------------------"
