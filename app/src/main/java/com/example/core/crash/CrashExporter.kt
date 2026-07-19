package com.example.core.crash

import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashExporter @Inject constructor(
    private val crashLogger: CrashLogger
) {
    companion object {
        private const val TAG = "CrashExporter"
    }

    /**
     * Reads the entire local crash log file and returns it as a string.
     * Returns an empty string if there are no logs or if an error occurs.
     */
    fun exportCrashLogs(): String {
        val file = crashLogger.getLogFile()
        if (!file.exists() || file.length() == 0L) {
            return "No crash logs recorded. All systems green."
        }

        return try {
            val content = StringBuilder()
            BufferedReader(FileReader(file)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    content.append(line).append("\n")
                    line = reader.readLine()
                }
            }
            content.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read local crash logs for manual export", e)
            "Error exporting local crash logs: ${e.message}"
        }
    }
}
