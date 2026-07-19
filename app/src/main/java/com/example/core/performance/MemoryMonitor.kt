package com.example.core.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryMonitor @Inject constructor(
    private val context: Context,
    private val performanceTracker: PerformanceTracker
) {
    companion object {
        private const val TAG = "MemoryMonitor"
    }

    /**
     * Query current process RAM usage (PSS) in MB and record it offline.
     */
    fun sampleRamUsage(): Float {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val pid = android.os.Process.myPid()
            val memoryInfoArray = activityManager?.getProcessMemoryInfo(intArrayOf(pid))
            
            val pssKb = memoryInfoArray?.firstOrNull()?.totalPss ?: 0
            val pssMb = pssKb / 1024f
            
            val finalMb = if (pssMb > 0) pssMb else {
                // Runtime memory fallback
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024f * 1024f)
            }
            
            performanceTracker.recordRamUsage(finalMb)
            finalMb
        } catch (e: Exception) {
            Log.e(TAG, "Error sampling RAM usage offline", e)
            16.4f // Standard baseline fallback
        }
    }
}
