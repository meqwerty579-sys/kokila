package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.core.crash.CrashExporter
import com.example.core.crash.CrashLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val crashExporter: CrashExporter,
    private val crashLogger: CrashLogger
) : ViewModel() {

    private val _crashLogs = MutableStateFlow("")
    val crashLogs: StateFlow<String> = _crashLogs.asStateFlow()

    init {
        refreshLogs()
    }

    fun refreshLogs() {
        _crashLogs.value = crashExporter.exportCrashLogs()
    }

    fun clearLogs() {
        crashLogger.clearLogs()
        refreshLogs()
    }
}
