package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.DiagnosticsRepository
import com.example.domain.usecase.BenchmarkEngineUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BenchmarkViewModel @Inject constructor(
    private val diagnosticsRepository: DiagnosticsRepository,
    private val benchmarkEngineUseCase: BenchmarkEngineUseCase
) : ViewModel() {

    private val _isRunningDiagnostic = MutableStateFlow(false)
    val isRunningDiagnostic: StateFlow<Boolean> = _isRunningDiagnostic.asStateFlow()

    private val _diagnosticStage = MutableStateFlow("")
    val diagnosticStage: StateFlow<String> = _diagnosticStage.asStateFlow()

    val ramUsage: StateFlow<Float> = diagnosticsRepository.getRamUsageFlow()
    val latencyFtts: StateFlow<Float> = diagnosticsRepository.getFttsFlow()
    val realTimeFactor: StateFlow<Float> = diagnosticsRepository.getRtfFlow()

    fun runDiagnostics() {
        if (_isRunningDiagnostic.value) return
        _isRunningDiagnostic.value = true
        viewModelScope.launch {
            try {
                benchmarkEngineUseCase { stage ->
                    _diagnosticStage.value = stage
                }
            } finally {
                _isRunningDiagnostic.value = false
            }
        }
    }
}
