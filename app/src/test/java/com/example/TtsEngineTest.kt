package com.example

import android.content.Context
import android.speech.tts.SynthesisCallback
import android.speech.tts.SynthesisRequest
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

class TestKokilaTtsService : KokilaTtsService() {
    fun testOnIsLanguageAvailable(lang: String?, country: String?, variant: String?): Int {
        return onIsLanguageAvailable(lang, country, variant)
    }

    fun testOnGetLanguage(): Array<String> {
        return onGetLanguage()
    }

    fun testOnSynthesizeText(request: SynthesisRequest?, callback: SynthesisCallback?) {
        onSynthesizeText(request, callback)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TtsEngineTest {

    private lateinit var context: Context
    private lateinit var service: TestKokilaTtsService
    private val nativeEngine = NativeTtsEngine.getInstance()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Instantiate service using Robolectric
        service = Robolectric.setupService(TestKokilaTtsService::class.java)
    }

    @Test
    fun testLanguageSupportValidation() {
        // Test system-level support queries for English and Telugu
        val enStatus = service.testOnIsLanguageAvailable("eng", "USA", "")
        assertTrue(
            "English language support check failed",
            enStatus == android.speech.tts.TextToSpeech.LANG_COUNTRY_AVAILABLE
        )

        val teStatus = service.testOnIsLanguageAvailable("tel", "IND", "")
        assertTrue(
            "Telugu language support check failed",
            teStatus == android.speech.tts.TextToSpeech.LANG_COUNTRY_AVAILABLE
        )

        val esStatus = service.testOnIsLanguageAvailable("spa", "ESP", "")
        assertTrue(
            "Unsupported languages must return LANG_NOT_SUPPORTED",
            esStatus == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
        )
    }

    @Test
    fun testGetLanguageProfile() {
        val currentLang = service.testOnGetLanguage()
        assertNotNull("Service must return current language profile", currentLang)
        assertEquals("Primary language must default to English", "eng", currentLang[0])
    }

    @Test
    fun testNativeEngineLifecycleManager() {
        // Since we are running in local JVM context where native .so binaries are unloaded,
        // we verify that the NativeTtsEngine reports unitialized safe state gracefully.
        assertFalse("Native engine should not report initialized on mock environment", nativeEngine.isInitialized)
        
        // Ensure calling safe stopping methods doesn't trigger native crashes or memory leaks
        nativeEngine.stop()
        nativeEngine.destroy()
        
        assertFalse("Destroying an uninitialized engine must preserve safe state", nativeEngine.isInitialized)
    }

    @Test
    fun testSynthesisRequestFallbackFlow() {
        var isStarted = false
        var isDone = false
        var dataWritten = 0

        // Custom synthesis callback to inspect streaming output buffers
        val testCallback = object : MockSynthesisCallback() {
            override fun start(sampleRateInHz: Int, format: Int, channelCount: Int): Int {
                isStarted = true
                return android.speech.tts.TextToSpeech.SUCCESS
            }

            override fun audioAvailable(buffer: ByteArray?, offset: Int, length: Int): Int {
                if (buffer != null) {
                    dataWritten += length
                }
                return android.speech.tts.TextToSpeech.SUCCESS
            }

            override fun done(): Int {
                isDone = true
                return android.speech.tts.TextToSpeech.SUCCESS
            }
        }

        // Send a mock utterance to the synthesis service
        val request = SynthesisRequest("Hello system voice testing framework", android.os.Bundle())
        service.testOnSynthesizeText(request, testCallback)

        // Wait brief ms to allow ThreadPool queue processing
        Thread.sleep(200)

        assertTrue("Audio streaming channel must initialize", isStarted)
        assertTrue("Synthesizer data must write samples to the stream", dataWritten > 0)
        assertTrue("Streaming pipeline must complete successfully with done() callback", isDone)
    }
}

/**
 * Custom base abstraction of SynthesisCallback for testing compatibility.
 */
abstract class MockSynthesisCallback : SynthesisCallback {
    override fun getMaxBufferSize(): Int = 8192
    override fun start(sampleRateInHz: Int, format: Int, channelCount: Int): Int = android.speech.tts.TextToSpeech.SUCCESS
    override fun audioAvailable(buffer: ByteArray?, offset: Int, length: Int): Int = android.speech.tts.TextToSpeech.SUCCESS
    override fun done(): Int = android.speech.tts.TextToSpeech.SUCCESS
    override fun error() {}
    override fun error(errorCode: Int) {}
    override fun hasStarted(): Boolean = true
    override fun hasFinished(): Boolean = true
}
