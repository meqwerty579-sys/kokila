package com.example

import com.example.core.audio.BiquadFilter
import com.example.core.audio.AdsrEnvelope
import com.example.core.audio.FormantSynthesizer
import com.example.core.audio.QualityPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FallbackAudioQualityTest {

    @Test
    fun testBiquadFilterBandpass() {
        val filter = BiquadFilter()
        filter.configureBandpass(1000.0, 8.0, 16000.0)
        
        val input = 1.0
        val output = filter.process(input)
        assertNotNull(output)
    }

    @Test
    fun testAdsrEnvelopePhases() {
        val envelope = AdsrEnvelope(
            attackMs = 10.0,
            decayMs = 10.0,
            sustainLevel = 0.5,
            releaseMs = 10.0,
            sampleRate = 1000.0
        )
        
        val totalSamples = 50
        
        val startLevel = envelope.getLevel(0, totalSamples)
        assertEquals(0.0, startLevel, 0.001)
        
        val peakLevel = envelope.getLevel(10, totalSamples)
        assertEquals(1.0, peakLevel, 0.05)

        val sustainLevel = envelope.getLevel(25, totalSamples)
        assertEquals(0.5, sustainLevel, 0.05)

        val endLevel = envelope.getLevel(totalSamples - 1, totalSamples)
        assertTrue(endLevel < 0.5)
    }

    @Test
    fun testFormantSynthesizerWithDifferentPresets() {
        val word = "hello"
        
        val fastBuffer = FormantSynthesizer.synthesizeWord(word, 1.0f, 1.0f, QualityPreset.FAST)
        assertTrue(fastBuffer.isNotEmpty())

        val balancedBuffer = FormantSynthesizer.synthesizeWord(word, 1.0f, 1.0f, QualityPreset.BALANCED)
        assertTrue(balancedBuffer.isNotEmpty())

        val hqBuffer = FormantSynthesizer.synthesizeWord(word, 1.0f, 1.0f, QualityPreset.HIGH_QUALITY)
        assertTrue(hqBuffer.isNotEmpty())
    }
}
