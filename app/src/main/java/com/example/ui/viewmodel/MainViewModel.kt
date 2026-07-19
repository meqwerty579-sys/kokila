package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.StopSpeechUseCase
import com.example.domain.usecase.SynthesizeSpeechUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val synthesizeSpeechUseCase: SynthesizeSpeechUseCase,
    private val stopSpeechUseCase: StopSpeechUseCase
) : ViewModel() {

    private val _textToSpeak = MutableStateFlow("Welcome to Kokila. This is an ultra-low-latency offline Text to Speech engine designed to run completely on private edge devices.")
    val textToSpeak: StateFlow<String> = _textToSpeak.asStateFlow()

    private val _pitchFactor = MutableStateFlow(1.0f)
    val pitchFactor: StateFlow<Float> = _pitchFactor.asStateFlow()

    private val _speedRate = MutableStateFlow(1.0f)
    val speedRate: StateFlow<Float> = _speedRate.asStateFlow()

    private val _vocalMode = MutableStateFlow("Warm Synth")
    val vocalMode: StateFlow<String> = _vocalMode.asStateFlow()

    private val _isSynthesizing = MutableStateFlow(false)
    val isSynthesizing: StateFlow<Boolean> = _isSynthesizing.asStateFlow()

    fun setTextToSpeak(text: String) {
        _textToSpeak.value = text
    }

    fun setPitchFactor(factor: Float) {
        _pitchFactor.value = factor
    }

    fun setSpeedRate(rate: Float) {
        _speedRate.value = rate
    }

    fun setVocalMode(mode: String) {
        _vocalMode.value = mode
    }

    fun setIsSynthesizing(synthesizing: Boolean) {
        _isSynthesizing.value = synthesizing
    }

    fun stopSynthesis() {
        stopSpeechUseCase()
        _isSynthesizing.value = false
    }
}
