package com.example.domain.usecase

import com.example.data.repository.ModelRepository
import com.example.data.repository.SettingsRepository
import javax.inject.Inject

class ChangeModelUseCase @Inject constructor(
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(voiceId: String) {
        val currentModels = modelRepository.getVoiceModelsFlow().value
        val selectedVoice = currentModels.find { it.id == voiceId } ?: return

        // Update voice list states
        val updatedModels = currentModels.map { voice ->
            when {
                voice.id == voiceId -> {
                    voice.copy(status = "Active")
                }
                // Demote other active models with SAME language to Standby
                voice.status == "Active" && voice.language == selectedVoice.language -> {
                    voice.copy(status = "Standby")
                }
                else -> voice
            }
        }
        
        modelRepository.setVoiceModels(updatedModels)
        settingsRepository.setSelectedVoice(voiceId)
    }
}
