package com.example.domain.usecase

import com.example.NativeTtsEngine
import javax.inject.Inject

class StopSpeechUseCase @Inject constructor(
    private val nativeEngine: NativeTtsEngine
) {
    operator fun invoke() {
        if (nativeEngine.isInitialized) {
            nativeEngine.stop()
        }
    }
}
