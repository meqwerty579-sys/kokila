package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class BenchmarkResult(
    val fttsMs: Float,
    val ramMb: Float,
    val rtf: Float
)

interface DiagnosticsRepository {
    fun getRamUsageFlow(): StateFlow<Float>
    fun setRamUsage(ram: Float)
    fun getFttsFlow(): StateFlow<Float>
    fun setFtts(ftts: Float)
    fun getRtfFlow(): StateFlow<Float>
    fun setRtf(rtf: Float)
    fun getBenchmarkMetricsFlow(): StateFlow<BenchmarkResult>
    fun updateMetrics(ftts: Float, ram: Float, rtf: Float)
}

@Singleton
class DiagnosticsRepositoryImpl @Inject constructor() : DiagnosticsRepository {
    private val _ramUsage = MutableStateFlow(16.4f)
    private val _ftts = MutableStateFlow(16.8f)
    private val _rtf = MutableStateFlow(0.24f)
    private val _metrics = MutableStateFlow(BenchmarkResult(16.8f, 16.4f, 0.24f))

    override fun getRamUsageFlow(): StateFlow<Float> = _ramUsage.asStateFlow()

    override fun setRamUsage(ram: Float) {
        _ramUsage.value = ram
        updateCompositeResult()
    }

    override fun getFttsFlow(): StateFlow<Float> = _ftts.asStateFlow()

    override fun setFtts(ftts: Float) {
        _ftts.value = ftts
        updateCompositeResult()
    }

    override fun getRtfFlow(): StateFlow<Float> = _rtf.asStateFlow()

    override fun setRtf(rtf: Float) {
        _rtf.value = rtf
        updateCompositeResult()
    }

    override fun getBenchmarkMetricsFlow(): StateFlow<BenchmarkResult> = _metrics.asStateFlow()

    override fun updateMetrics(ftts: Float, ram: Float, rtf: Float) {
        _ftts.value = ftts
        _ramUsage.value = ram
        _rtf.value = rtf
        _metrics.value = BenchmarkResult(ftts, ram, rtf)
    }

    private fun updateCompositeResult() {
        _metrics.value = BenchmarkResult(_ftts.value, _ramUsage.value, _rtf.value)
    }
}
