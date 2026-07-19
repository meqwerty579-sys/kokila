package com.example.core.storage

import android.content.Context
import android.os.StatFs
import android.util.Log
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageValidator @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "StorageValidator"
        private const val MIN_REQUIRED_SPACE_BYTES = 100L * 1024L * 1024L // 100 MB
    }

    /**
     * Comprehensive storage check.
     * Returns true if there is sufficient space, write permissions, and target path exists or is writable.
     */
    fun hasSufficientStorageForExtraction(targetDir: File): Boolean {
        try {
            // 1. Verify target directory exists or can be created
            if (!targetDir.exists()) {
                val created = targetDir.mkdirs()
                if (!created && !targetDir.exists()) {
                    Log.e(TAG, "Target directory does not exist and cannot be created: ${targetDir.absolutePath}")
                    return false
                }
            }

            // 2. Verify write permissions
            if (!targetDir.canWrite()) {
                Log.e(TAG, "No write permissions for target directory: ${targetDir.absolutePath}")
                return false
            }

            // 3. Verify available storage space (100 MB threshold)
            val stat = StatFs(targetDir.absolutePath)
            val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
            Log.i(TAG, "Storage validation: available space = ${availableBytes / (1024 * 1024)} MB, required = 100 MB")

            if (availableBytes < MIN_REQUIRED_SPACE_BYTES) {
                Log.w(TAG, "Insufficient storage space! Available ${availableBytes / (1024 * 1024)} MB is less than the required 100 MB.")
                return false
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Crash prevented in StorageValidator check: ${e.message}", e)
            return false // Safe failure fallback
        }
    }
}
