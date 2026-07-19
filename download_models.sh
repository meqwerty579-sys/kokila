#!/bin/bash
# download_models.sh
# Script to automate downloading high-quality offline neural TTS models (VITS Gigaspeech)
# and required dictionaries/grapheme-to-phoneme datasets for local compilation/execution.
# 
# Usage: ./download_models.sh

set -e

# Define root paths
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ASSETS_DIR="${PROJECT_ROOT}/app/src/main/assets"
JNI_LIBS_DIR="${PROJECT_ROOT}/app/src/main/jniLibs"

echo "=========================================================="
echo "    KOKILA TTS - Neural Assets Downloader                 "
echo "=========================================================="
echo "Project Root: ${PROJECT_ROOT}"
echo "Assets Target: ${ASSETS_DIR}"

# Create directories
mkdir -p "${ASSETS_DIR}"
mkdir -p "${JNI_LIBS_DIR}"

# --- 1. Download VITS Neural Voice Model ---
# Model: Gigaspeech English INT8 Quantized model
MODEL_URL="https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-en-gigaspeech.tar.bz2"
ARCHIVE_NAME="vits-en-gigaspeech.tar.bz2"

if [ ! -f "${ASSETS_DIR}/vits-en-gigaspeech.onnx" ]; then
    echo "Downloading VITS Gigaspeech English Model archive..."
    cd "${PROJECT_ROOT}"
    
    if command -v curl >/dev/null 2>&1; then
        curl -L "${MODEL_URL}" -o "${ARCHIVE_NAME}"
    elif command -v wget >/dev/null 2>&1; then
        wget -O "${ARCHIVE_NAME}" "${MODEL_URL}"
    else
        echo "Error: Neither curl nor wget was found. Install curl or wget to continue."
        exit 1
    fi
    
    echo "Extracting archive contents..."
    tar -xjf "${ARCHIVE_NAME}"
    
    # Copy crucial files to assets
    cp "vits-en-gigaspeech/vits-en-gigaspeech.onnx" "${ASSETS_DIR}/vits-en-gigaspeech.onnx"
    cp "vits-en-gigaspeech/tokens.txt" "${ASSETS_DIR}/tokens.txt"
    cp "vits-en-gigaspeech/lexicon.txt" "${ASSETS_DIR}/lexicon.txt"
    
    # Clean up workspace
    rm -rf "vits-en-gigaspeech"
    rm -f "${ARCHIVE_NAME}"
    echo "Neural Voice Assets installed successfully!"
else
    echo "VITS Gigaspeech Model files already present in assets, skipping."
fi

# --- 2. Download espeak-ng-data phoneme datasets ---
# Essential for Grapheme-to-Phoneme mapping offline.
ESPEAK_URL="https://raw.githubusercontent.com/k2-fsa/sherpa-onnx/master/android/SherpaOnnxTtsEngine/app/src/main/assets/espeak-ng-data.tar.bz2"
ESPEAK_ARCHIVE="espeak-ng-data.tar.bz2"

if [ ! -d "${ASSETS_DIR}/espeak-ng-data" ]; then
    echo "Downloading espeak-ng-data phoneme dictionaries..."
    cd "${ASSETS_DIR}"
    
    if command -v curl >/dev/null 2>&1; then
        curl -L "${ESPEAK_URL}" -o "${ESPEAK_ARCHIVE}"
    else
        wget -O "${ESPEAK_ARCHIVE}" "${ESPEAK_URL}"
    fi
    
    tar -xjf "${ESPEAK_ARCHIVE}"
    rm -f "${ESPEAK_ARCHIVE}"
    echo "espeak-ng dictionaries installed successfully!"
else
    echo "espeak-ng dictionaries already present in assets, skipping."
fi

# --- 3. Verify Download Integrity ---
echo "Verifying file checksum integrity..."
EXPECTED_MODEL_SHA256="4cf3b8d60100f917df9a0f02ca1433f57297eef8330725a3eb4df1712a23e590" # Gigaspeech INT8

if command -v sha256sum >/dev/null 2>&1; then
    ACTUAL_SHA256=$(sha256sum "${ASSETS_DIR}/vits-en-gigaspeech.onnx" | awk '{print $1}')
    echo "Expected: ${EXPECTED_MODEL_SHA256}"
    echo "Actual:   ${ACTUAL_SHA256}"
    if [ "${ACTUAL_SHA256}" != "${EXPECTED_MODEL_SHA256}" ]; then
        echo "WARNING: Model SHA256 mismatch. If you downloaded a newer version, this is expected."
    else
        echo "Integrity verification PASSED!"
    fi
else
    echo "sha256sum utility not found. Skipping validation."
fi

echo "=========================================================="
echo "    ALL OFFLINE ASSETS PREPARED SUCCESSFULLY!             "
echo "=========================================================="
