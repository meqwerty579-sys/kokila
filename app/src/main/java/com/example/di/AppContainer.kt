package com.example.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.NativeTtsEngine
import com.example.core.crash.CrashExporter
import com.example.core.crash.CrashLogger
import com.example.core.performance.MemoryMonitor
import com.example.core.performance.PerformanceTracker
import com.example.core.storage.StorageValidator
import com.example.data.repository.*
import com.example.domain.usecase.*
import com.example.ui.viewmodel.BenchmarkViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ModelViewModel
import com.example.ui.viewmodel.SettingsViewModel

class AppContainer(private val context: Context) {
    
    // Singletons & Services
    val nativeTtsEngine: NativeTtsEngine by lazy { NativeTtsEngine() }
    
    // Repositories
    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl() }
    val diagnosticsRepository: DiagnosticsRepository by lazy { DiagnosticsRepositoryImpl() }
    val modelRepository: ModelRepository by lazy { ModelRepositoryImpl() }
    
    // Core Utilities
    val storageValidator: StorageValidator by lazy { StorageValidator(context) }
    val crashLogger: CrashLogger by lazy { CrashLogger(context) }
    val crashExporter: CrashExporter by lazy { CrashExporter(crashLogger) }
    val performanceTracker: PerformanceTracker by lazy { PerformanceTracker() }
    val memoryMonitor: MemoryMonitor by lazy { MemoryMonitor(context, performanceTracker) }
    
    // Use Cases
    val validateModelUseCase: ValidateModelUseCase by lazy { ValidateModelUseCase(modelRepository) }
    val extractAssetsUseCase: ExtractAssetsUseCase by lazy { ExtractAssetsUseCase(context, storageValidator, modelRepository) }
    val changeModelUseCase: ChangeModelUseCase by lazy { ChangeModelUseCase(modelRepository, settingsRepository) }
    val benchmarkEngineUseCase: BenchmarkEngineUseCase by lazy { BenchmarkEngineUseCase(diagnosticsRepository, memoryMonitor, performanceTracker) }
    val synthesizeSpeechUseCase: SynthesizeSpeechUseCase by lazy { SynthesizeSpeechUseCase(nativeTtsEngine) }
    val stopSpeechUseCase: StopSpeechUseCase by lazy { StopSpeechUseCase(nativeTtsEngine) }
    
    // ViewModel Factory
    val viewModelFactory: ViewModelProvider.Factory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                        MainViewModel(synthesizeSpeechUseCase, stopSpeechUseCase) as T
                    }
                    modelClass.isAssignableFrom(ModelViewModel::class.java) -> {
                        ModelViewModel(modelRepository, settingsRepository, changeModelUseCase) as T
                    }
                    modelClass.isAssignableFrom(BenchmarkViewModel::class.java) -> {
                        BenchmarkViewModel(diagnosticsRepository, benchmarkEngineUseCase) as T
                    }
                    modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                        SettingsViewModel(crashExporter, crashLogger) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}
