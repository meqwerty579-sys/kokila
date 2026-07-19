package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsRepository {
    fun getSpeechRate(): StateFlow<Float>
    fun setSpeechRate(rate: Float)
    fun getPitch(): StateFlow<Float>
    fun setPitch(pitch: Float)
    fun getSelectedVoice(): StateFlow<String>
    fun setSelectedVoice(voiceId: String)
}

@Singleton
class SettingsRepositoryImpl @Inject constructor() : SettingsRepository {
    private val _speechRate = MutableStateFlow(1.0f)
    private val _pitch = MutableStateFlow(1.0f)
    private val _selectedVoice = MutableStateFlow("v1")

    override fun getSpeechRate(): StateFlow<Float> = _speechRate.asStateFlow()
    
    override fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
    }

    override fun getPitch(): StateFlow<Float> = _pitch.asStateFlow()
    
    override fun setPitch(pitch: Float) {
        _pitch.value = pitch
    }

    override fun getSelectedVoice(): StateFlow<String> = _selectedVoice.asStateFlow()
    
    override fun setSelectedVoice(voiceId: String) {
        _selectedVoice.value = voiceId
    }
}
