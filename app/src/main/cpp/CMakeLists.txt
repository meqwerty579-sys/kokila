# CMakeLists.txt for Kokila Neural TTS Engine JNI library
#
# This CMakeLists.txt defines the native build system for compiling the Sherpa-ONNX 
# offline neural speech synthesis engine on Android. It links against the compiled 
# native binaries of Sherpa-ONNX, espeak-ng-phonemizer, and ONNX Runtime.

cmake_minimum_required(VERSION 3.18.1)

project("kokila-tts")

# Set compile flags
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Configure compiler optimizations for neural inference performance
if(CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -fPIC -ffast-math -flto")
endif()

# JNI Include directories
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# Set the path to prebuilt Sherpa-ONNX and ONNX Runtime libraries
# In a local development environment, these prebuilt binaries are pulled 
# via the download_models.sh script and packaged in the JNI libs directory.
set(SHERPA_PREBUILT_LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

# Dynamic stub detection: Check if real, non-empty prebuilt libraries exist
set(USE_STUBS TRUE)
if(EXISTS "${SHERPA_PREBUILT_LIBS_DIR}/libsherpa-onnx-core.so")
    file(SIZE "${SHERPA_PREBUILT_LIBS_DIR}/libsherpa-onnx-core.so" CORE_SO_SIZE)
    if(CORE_SO_SIZE GREATER 1000)
        set(USE_STUBS FALSE)
    endif()
endif()

message(STATUS "Kokila JNI Build Mode: USE_STUBS = ${USE_STUBS}")

if(USE_STUBS)
    # Compile with our local high-fidelity C++ stubs
    add_library(kokila-tts SHARED
        kokila-tts-jni.cc
        sherpa-onnx-wrapper.cc
        sherpa-onnx-stub.cc
    )
else()
    # 1. Declare Sherpa-ONNX Core Native Library
    add_library(sherpa-onnx-core SHARED IMPORTED)
    set_target_properties(sherpa-onnx-core PROPERTIES IMPORTED_LOCATION
        ${SHERPA_PREBUILT_LIBS_DIR}/libsherpa-onnx-core.so
    )

    # 2. Declare ONNX Runtime Native Library
    add_library(onnxruntime SHARED IMPORTED)
    set_target_properties(onnxruntime PROPERTIES IMPORTED_LOCATION
        ${SHERPA_PREBUILT_LIBS_DIR}/libonnxruntime.so
    )

    # 3. Declare espeak-ng Core Engine Native Library (for Grapheme-to-Phoneme conversion)
    add_library(espeak-ng SHARED IMPORTED)
    set_target_properties(espeak-ng PROPERTIES IMPORTED_LOCATION
        ${SHERPA_PREBUILT_LIBS_DIR}/libespeak-ng.so
    )

    # Declare Kokila JNI Bridge Library
    add_library(kokila-tts SHARED
        kokila-tts-jni.cc
        sherpa-onnx-wrapper.cc
    )
endif()

# Include external headers
# Note: For local compilation, you will need the C/C++ headers of sherpa-onnx,
# which are located in the external/include folder after running download_models.sh
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/external/include
)

# Link native libraries together
# Log is the Android NDK logging system used for JNI debugging
find_library(log-lib log)

if(USE_STUBS)
    target_link_libraries(kokila-tts
        ${log-lib}
    )
else()
    target_link_libraries(kokila-tts
        sherpa-onnx-core
        onnxruntime
        espeak-ng
        ${log-lib}
    )
endif()
