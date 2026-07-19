package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.VoiceModelInfo
import com.example.data.repository.ModelRepository
import com.example.data.repository.SettingsRepository
import com.example.domain.usecase.ChangeModelUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ModelViewModel @Inject constructor(
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository,
    private val changeModelUseCase: ChangeModelUseCase
) : ViewModel() {

    val voiceModels: StateFlow<List<VoiceModelInfo>> = modelRepository.getVoiceModelsFlow()
    val selectedVoiceId: StateFlow<String> = settingsRepository.getSelectedVoice()

    private val _simulatingDownloadId = MutableStateFlow<String?>(null)
    val simulatingDownloadId: StateFlow<String?> = _simulatingDownloadId.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    fun downloadModel(voiceId: String) {
        _simulatingDownloadId.value = voiceId
        _downloadProgress.value = 0f
        viewModelScope.launch {
            while (_downloadProgress.value < 1.0f) {
                delay(200)
                _downloadProgress.value += 0.1f
            }
            
            // Mark as standby in repository once downloaded
            val currentList = modelRepository.getVoiceModelsFlow().value
            val updatedList = currentList.map {
                if (it.id == voiceId) it.copy(status = "Standby") else it
            }
            modelRepository.setVoiceModels(updatedList)
            _simulatingDownloadId.value = null
        }
    }

    fun activateModel(voiceId: String) {
        changeModelUseCase(voiceId)
    }
}
