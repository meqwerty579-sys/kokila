package com.example.domain.usecase

import android.content.Context
import android.util.Log
import com.example.core.storage.StorageValidator
import com.example.data.repository.ModelRepository
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ExtractAssetsUseCase @Inject constructor(
    private val context: Context,
    private val storageValidator: StorageValidator,
    private val modelRepository: ModelRepository
) {
    companion object {
        private const val TAG = "ExtractAssetsUseCase"
    }

    /**
     * Unpacks model files from APK assets to internal storage.
     * Integrates storage safety validation. If storage is insufficient,
     * it skips extraction, continues utilizing DSP fallbacks, and logs warnings.
     */
    operator fun invoke(assetName: String, targetFile: File): Boolean {
        try {
            val parentDir = targetFile.parentFile ?: context.filesDir
            
            // 1. Storage safety validation check
            if (!storageValidator.hasSufficientStorageForExtraction(parentDir)) {
                Log.w(TAG, "Storage validation failed for extracting asset: $assetName. Skipping extraction to prevent crashes.")
                modelRepository.setModelExtracted(assetName, false)
                return false
            }

            // 2. Perform extraction safely
            context.assets.open(assetName).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.i(TAG, "Successfully extracted asset: $assetName to ${targetFile.absolutePath}")
            modelRepository.setModelExtracted(assetName, true)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract asset $assetName safely: ${e.message}", e)
            modelRepository.setModelExtracted(assetName, false)
            return false
        }
    }
}
