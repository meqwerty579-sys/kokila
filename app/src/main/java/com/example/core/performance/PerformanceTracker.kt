package com.example.core.performance

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceTracker @Inject constructor() {
    companion object {
        private const val TAG = "PerformanceTracker"
    }

    private val _coldStartTimeMs = MutableStateFlow(0L)
    val coldStartTimeMs: StateFlow<Long> = _coldStartTimeMs.asStateFlow()

    private val _modelLoadTimeMs = MutableStateFlow(0L)
    val modelLoadTimeMs: StateFlow<Long> = _modelLoadTimeMs.asStateFlow()

    private val _lastFttsMs = MutableStateFlow(16.8f)
    val lastFttsMs: StateFlow<Float> = _lastFttsMs.asStateFlow()

    private val _lastRtf = MutableStateFlow(0.24f)
    val lastRtf: StateFlow<Float> = _lastRtf.asStateFlow()

    private val _lastRamMb = MutableStateFlow(16.4f)
    val lastRamMb: StateFlow<Float> = _lastRamMb.asStateFlow()

    fun recordColdStart(timeMs: Long) {
        _coldStartTimeMs.value = timeMs
        Log.i(TAG, "Recorded Cold Start: $timeMs ms (offline-only)")
    }

    fun recordModelLoadTime(timeMs: Long) {
        _modelLoadTimeMs.value = timeMs
        Log.i(TAG, "Recorded Model Load Time: $timeMs ms")
    }

    fun recordFtts(timeMs: Float) {
        _lastFttsMs.value = timeMs
        Log.i(TAG, "Recorded FTTS: $timeMs ms")
    }

    fun recordRtf(rtf: Float) {
        _lastRtf.value = rtf
        Log.i(TAG, "Recorded RTF: $rtf")
    }

    fun recordRamUsage(ramMb: Float) {
        _lastRamMb.value = ramMb
        Log.i(TAG, "Recorded RAM Usage: $ramMb MB")
    }
}
