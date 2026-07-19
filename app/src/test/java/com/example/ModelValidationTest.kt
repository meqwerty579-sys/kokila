package com.example

import com.example.core.model.ModelIntegrityValidator
import com.example.core.model.ModelManifestRepository
import com.example.core.download.ModelDownloadManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import kotlinx.coroutines.runBlocking
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ModelValidationTest {

    @Test
    fun testCalculateSha256ForNonExistentFile() {
        val file = File("non_existent_file.onnx")
        val sha = ModelIntegrityValidator.calculateSha256(file)
        assertEquals("", sha)
    }

    @Test
    fun testValidateModelFileReturnsFalseForEmptyFile() {
        val file = File.createTempFile("temp_model", ".onnx")
        file.deleteOnExit()
        val isValid = ModelIntegrityValidator.validateModelFile(file, "some_hash")
        assertFalse(isValid)
    }

    @Test
    fun testModelManifestParsing() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = ModelManifestRepository(context)
        val manifest = repo.getManifest()
        assertNotNull(manifest)
        assertFalse(manifest!!.models.isEmpty())
        
        val model = manifest.models.first()
        assertEquals("vits-en-gigaspeech", model.id)
        assertEquals("1.0.0", model.version)
        assertEquals("en", model.language)
        assertEquals(38, model.sizeMb)
    }

    @Test
    fun testInsecureUrlRejectedByDownloadManager() {
        val manager = ModelDownloadManager()
        val file = File("temp.onnx")
        var errorEmitted = false
        var errorMessage = ""

        runBlocking {
            manager.downloadFile("http://insecure-http-url.com/model.onnx", file).collect { state ->
                if (state is ModelDownloadManager.DownloadState.Error) {
                    errorEmitted = true
                    errorMessage = state.message
                }
            }
        }

        assertTrue(errorEmitted)
        assertTrue(errorMessage.contains("Insecure URL rejected"))
    }
}
