package com.example.core.crash

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashLogger @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "CrashLogger"
        private const val LOG_FILE_NAME = "kokila_crash_diagnostics.log"
    }

    private val _logFile: File by lazy {
        File(context.filesDir, LOG_FILE_NAME)
    }

    fun logCrash(throwable: Throwable, engineState: String = "ACTIVE") {
        try {
            val writer = FileWriter(_logFile, true)
            val printWriter = PrintWriter(writer)

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
            printWriter.println("==================================================")
            printWriter.println("TIMESTAMP: $timestamp")
            printWriter.println("ENGINE STATE: $engineState")
            printWriter.println("DEVICE INFO:")
            printWriter.println("  Brand: ${Build.BRAND}")
            printWriter.println("  Device: ${Build.DEVICE}")
            printWriter.println("  Model: ${Build.MODEL}")
            printWriter.println("  Android OS: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            printWriter.println("  Hardware: ${Build.HARDWARE}")
            printWriter.println("STACK TRACE:")

            val stringWriter = StringWriter()
            throwable.printStackTrace(PrintWriter(stringWriter))
            printWriter.println(stringWriter.toString())
            printWriter.println("==================================================")
            printWriter.println()

            printWriter.flush()
            printWriter.close()
            Log.i(TAG, "Successfully wrote local crash log to ${_logFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write local crash log offline", e)
        }
    }

    fun clearLogs(): Boolean {
        return try {
            if (_logFile.exists()) {
                _logFile.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear local crash log file", e)
            false
        }
    }

    fun getLogFile(): File = _logFile
}
