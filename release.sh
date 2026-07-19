#!/bin/bash
# release.sh
# Automated production compiler script. Performs checks, runs linting,
# compiles native layers, and builds optimized Release APK.

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=========================================================="
echo "    KOKILA TTS - Automated Production Compiler            "
echo "=========================================================="

# Run verification checks
"./verify_assets.sh"

echo "Cleaning build cache..."
./gradlew clean

echo "Running Unit Tests..."
./gradlew testDebugUnitTest

echo "Compiling Native JNI and Building Release APK..."
./gradlew assembleRelease

echo "--------------------------------------------------------"
echo "  Build Completed Successfully!"
echo "  Output Directory: ${PROJECT_ROOT}/app/build/outputs/apk/release/"
echo "=========================================================="
