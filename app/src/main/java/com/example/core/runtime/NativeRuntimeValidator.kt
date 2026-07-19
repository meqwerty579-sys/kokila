package com.example.core.runtime

import android.os.Build
import android.util.Log

object NativeRuntimeValidator {
    private const val TAG = "NativeRuntimeValidator"

    private val SUPPORTED_ABIS = setOf("arm64-v8a", "armeabi-v7a", "x86_64")

    private val REQUIRED_LIBS = listOf(
        "onnxruntime",
        "sherpa-onnx-core",
        "espeak-ng",
        "kokila-tts"
    )

    fun isAbiSupported(): Boolean {
        val deviceAbis = Build.SUPPORTED_ABIS ?: emptyArray()
        return deviceAbis.any { SUPPORTED_ABIS.contains(it) }
    }

    fun validateRuntime(): ValidationResult {
        Log.i(TAG, "Starting native runtime validation...")

        val abiSupported = isAbiSupported()
        if (!abiSupported) {
            Log.w(TAG, "Device ABIs ${Build.SUPPORTED_ABIS?.joinToString() ?: "none"} are not supported for neural engine.")
            return ValidationResult(
                isSupported = false,
                reason = "Unsupported ABI: ${Build.SUPPORTED_ABIS?.firstOrNull() ?: "unknown"}"
            )
        }

        val loadedLibs = mutableListOf<String>()
        val failedLibs = mutableListOf<String>()

        for (libName in REQUIRED_LIBS) {
            try {
                System.loadLibrary(libName)
                loadedLibs.add(libName)
                Log.i(TAG, "Successfully loaded library: $libName")
            } catch (t: Throwable) {
                failedLibs.add(libName)
                Log.w(TAG, "Failed to load library: $libName. Error: ${t.localizedMessage}")
            }
        }

        if (failedLibs.isNotEmpty()) {
            return ValidationResult(
                isSupported = false,
                reason = "Failed to load libraries: ${failedLibs.joinToString()}",
                loadedLibraries = loadedLibs,
                failedLibraries = failedLibs
            )
        }

        return ValidationResult(
            isSupported = true,
            reason = "All required native libraries loaded successfully",
            loadedLibraries = loadedLibs
        )
    }

    data class ValidationResult(
        val isSupported: Boolean,
        val reason: String,
        val loadedLibraries: List<String> = emptyList(),
        val failedLibraries: List<String> = emptyList()
    )
}
