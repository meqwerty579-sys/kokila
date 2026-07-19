package com.example.domain.usecase

import android.util.Log
import com.example.NativeTtsEngine
import javax.inject.Inject

class SynthesizeSpeechUseCase @Inject constructor(
    private val nativeEngine: NativeTtsEngine
) {
    companion object {
        private const val TAG = "SynthesizeSpeechUseCase"
    }

    /**
     * Executes neural speech synthesis if native engine is active, or returns null.
     */
    operator fun invoke(text: String, speakerId: Int = 0, speed: Float = 1.0f): FloatArray? {
        return try {
            if (nativeEngine.isInitialized) {
                nativeEngine.synthesize(text, speakerId, speed)
            } else {
                Log.w(TAG, "Native engine is not initialized. Cannot perform neural synthesis.")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during neural synthesis: ${e.message}", e)
            null
        }
    }
}
