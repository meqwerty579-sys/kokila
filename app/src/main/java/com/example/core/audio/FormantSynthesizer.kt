package com.example.core.audio

import java.util.Random
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sign

enum class QualityPreset {
    FAST,
    BALANCED,
    HIGH_QUALITY
}

class BiquadFilter {
    private var b0 = 1.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var a1 = 0.0
    private var a2 = 0.0

    private var x1 = 0.0
    private var x2 = 0.0
    private var y1 = 0.0
    private var y2 = 0.0

    fun configureBandpass(frequency: Double, q: Double, sampleRate: Double) {
        val w0 = 2.0 * PI * frequency / sampleRate
        val alpha = sin(w0) / (2.0 * q)
        val cosW0 = cos(w0)

        val a0 = 1.0 + alpha
        b0 = alpha / a0
        b1 = 0.0
        b2 = -alpha / a0
        a1 = -2.0 * cosW0 / a0
        a2 = (1.0 - alpha) / a0
    }

    fun process(sample: Double): Double {
        val out = b0 * sample + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
        x2 = x1
        x1 = sample
        y2 = y1
        y1 = out
        return out
    }
}

class AdsrEnvelope(
    attackMs: Double,
    decayMs: Double,
    private val sustainLevel: Double,
    releaseMs: Double,
    sampleRate: Double
) {
    private val attackSamples = (attackMs / 1000.0 * sampleRate).toInt()
    private val decaySamples = (decayMs / 1000.0 * sampleRate).toInt()
    private val releaseSamples = (releaseMs / 1000.0 * sampleRate).toInt()

    fun getLevel(sampleIndex: Int, totalSamples: Int): Double {
        if (sampleIndex < 0 || sampleIndex >= totalSamples) return 0.0

        val releaseStart = (totalSamples - releaseSamples).coerceAtLeast(attackSamples + decaySamples)
        
        return when {
            sampleIndex < attackSamples -> {
                if (attackSamples > 0) sampleIndex.toDouble() / attackSamples else 1.0
            }
            sampleIndex < attackSamples + decaySamples -> {
                if (decaySamples > 0) {
                    val elapsed = sampleIndex - attackSamples
                    1.0 - (elapsed.toDouble() / decaySamples) * (1.0 - sustainLevel)
                } else sustainLevel
            }
            sampleIndex < releaseStart -> {
                sustainLevel
            }
            sampleIndex < totalSamples -> {
                if (releaseSamples > 0) {
                    val elapsed = sampleIndex - releaseStart
                    sustainLevel * (1.0 - (elapsed.toDouble() / releaseSamples).coerceIn(0.0, 1.0))
                } else 0.0
            }
            else -> 0.0
        }
    }
}

object FormantSynthesizer {
    private val random = Random()

    data class FormantProfile(val f1: Double, val f2: Double, val f3: Double)

    private val VOWEL_PROFILES = mapOf(
        'a' to FormantProfile(730.0, 1090.0, 2440.0),
        'e' to FormantProfile(530.0, 1840.0, 2480.0),
        'i' to FormantProfile(270.0, 2290.0, 3010.0),
        'o' to FormantProfile(570.0, 840.0, 2410.0),
        'u' to FormantProfile(300.0, 870.0, 2240.0)
    )

    private val DEFAULT_FORMANT = FormantProfile(550.0, 1550.0, 2500.0)

    fun synthesizeWord(
        word: String,
        pitch: Float,
        speed: Float,
        preset: QualityPreset = QualityPreset.BALANCED,
        sampleRate: Int = 16000
    ): ShortArray {
        val speedFactor = 1.0 / speed.coerceIn(0.5f, 2.5f)
        val baseFreq = 125.0 * pitch.coerceIn(0.5f, 2.0f)
        val durationMs = (word.length * 110 * speedFactor).coerceIn(160.0, 750.0).toInt()
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()

        val buffer = ShortArray(numSamples)
        val lowercaseWord = word.lowercase()

        // 1. Vowel detection and formant profiling
        var profile = DEFAULT_FORMANT
        for (char in lowercaseWord) {
            if (VOWEL_PROFILES.containsKey(char)) {
                profile = VOWEL_PROFILES[char]!!
                break
            }
        }

        // 2. Setup resonators
        val filterF1 = BiquadFilter()
        val filterF2 = BiquadFilter()
        val filterF3 = BiquadFilter()

        if (preset != QualityPreset.FAST) {
            filterF1.configureBandpass(profile.f1 * pitch, 8.0, sampleRate.toDouble())
            filterF2.configureBandpass(profile.f2 * pitch, 12.0, sampleRate.toDouble())
            filterF3.configureBandpass(profile.f3 * pitch, 10.0, sampleRate.toDouble())
        }

        // 3. Fricative detection
        var isFricative = false
        var fricativeType = "" // "s", "sh", "f", "th", "z"
        if (lowercaseWord.contains('s')) {
            isFricative = true
            fricativeType = "s"
        } else if (lowercaseWord.contains("sh")) {
            isFricative = true
            fricativeType = "sh"
        } else if (lowercaseWord.contains('f')) {
            isFricative = true
            fricativeType = "f"
        } else if (lowercaseWord.contains('z')) {
            isFricative = true
            fricativeType = "z"
        } else if (lowercaseWord.contains("th")) {
            isFricative = true
            fricativeType = "th"
        }

        val isNasal = lowercaseWord.contains('n') || lowercaseWord.contains('m') || lowercaseWord.contains("ing")

        // 4. Setup ADSR Envelope
        val envelope = when (preset) {
            QualityPreset.FAST -> AdsrEnvelope(10.0, 20.0, 0.8, 15.0, sampleRate.toDouble())
            QualityPreset.BALANCED -> AdsrEnvelope(20.0, 35.0, 0.75, 25.0, sampleRate.toDouble())
            QualityPreset.HIGH_QUALITY -> AdsrEnvelope(30.0, 50.0, 0.7, 40.0, sampleRate.toDouble())
        }

        // 5. Generate samples
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envVal = envelope.getLevel(i, numSamples)

            var sampleValue = 0.0

            // Fricative consonant sound generation using filtered noise
            if (isFricative && (i < numSamples * 0.20 || i > numSamples * 0.80)) {
                val noise = random.nextDouble() * 2.0 - 1.0
                val filteredNoise = when (fricativeType) {
                    "s" -> {
                        // High pass filtered noise for sharp 's'
                        val hpFilter = BiquadFilter()
                        hpFilter.configureBandpass(7000.0, 4.0, sampleRate.toDouble())
                        hpFilter.process(noise) * 0.7
                    }
                    "sh" -> {
                        // Bandpass around 3.5kHz for 'sh'
                        val bpFilter = BiquadFilter()
                        bpFilter.configureBandpass(3500.0, 3.0, sampleRate.toDouble())
                        bpFilter.process(noise) * 0.65
                    }
                    "f", "th" -> {
                        // Soft broadband noise
                        val bpFilter = BiquadFilter()
                        bpFilter.configureBandpass(2500.0, 1.5, sampleRate.toDouble())
                        bpFilter.process(noise) * 0.3
                    }
                    "z" -> {
                        // Combined voice + high frequency noise
                        val f0 = baseFreq * (1.0 + 0.015 * sin(2.0 * PI * 6.0 * t))
                        val voice = sin(2.0 * PI * f0 * t)
                        val hpFilter = BiquadFilter()
                        hpFilter.configureBandpass(6500.0, 3.5, sampleRate.toDouble())
                        (0.4 * voice + 0.6 * hpFilter.process(noise)) * 0.5
                    }
                    else -> noise * 0.2
                }
                sampleValue = filteredNoise
            } else {
                // Voiced speech generation
                val vibrato = 1.0 + 0.02 * sin(2.0 * PI * 5.5 * t)
                val f0 = baseFreq * vibrato

                // Glottal source (Fundamental + 2nd + 3rd harmonics)
                val glottal = sin(2.0 * PI * f0 * t) +
                              0.45 * sin(2.0 * PI * (f0 * 2.0) * t) +
                              0.22 * sin(2.0 * PI * (f0 * 3.0) * t)

                if (preset == QualityPreset.FAST) {
                    // Simpler formulation for fast path
                    val formantResonance = sin(2.0 * PI * profile.f1 * t) * 0.45 +
                                           sin(2.0 * PI * profile.f2 * t) * 0.30
                    sampleValue = glottal * 0.40 + formantResonance * 0.60
                } else {
                    // Precise Formant Resonators using Biquad Filters in parallel
                    val r1 = filterF1.process(glottal)
                    val r2 = filterF2.process(glottal)
                    val r3 = if (preset == QualityPreset.HIGH_QUALITY) filterF3.process(glottal) else 0.0

                    sampleValue = r1 * 0.45 + r2 * 0.35 + r3 * 0.20
                }

                if (isNasal) {
                    sampleValue *= 0.70
                }
            }

            // 6. Harmonic exciter (adds warmth and brightness with optional 2nd/3rd order harmonics)
            if (preset == QualityPreset.HIGH_QUALITY) {
                val intensity = 0.15
                val h2 = sampleValue * sampleValue * sign(sampleValue)
                val h3 = sampleValue * sampleValue * sampleValue
                sampleValue = (sampleValue + intensity * (0.6 * h2 + 0.4 * h3)).coerceIn(-1.0, 1.0)
            }

            // Save sample scaled by envelope
            val outputGain = 0.38
            buffer[i] = (sampleValue * envVal * outputGain * 32767.0)
                .coerceIn(-32768.0, 32767.0)
                .toInt()
                .toShort()
        }

        // 7. Anti-click protection: 10ms ramp fade-in and fade-out
        val fadeSamples = (0.010 * sampleRate).toInt().coerceAtMost(buffer.size / 2)
        for (i in 0 until fadeSamples) {
            val factor = i.toDouble() / fadeSamples
            buffer[i] = (buffer[i] * factor).toInt().toShort()
            val endIdx = buffer.size - 1 - i
            buffer[endIdx] = (buffer[endIdx] * factor).toInt().toShort()
        }

        return buffer
    }
}
