package com.example.data.repository

import com.example.VoiceModelInfo
import com.example.core.model.ModelIntegrityValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface ModelRepository {
    fun getVoiceModelsFlow(): StateFlow<List<VoiceModelInfo>>
    fun setVoiceModels(models: List<VoiceModelInfo>)
    fun getExtractionStatusFlow(): StateFlow<Map<String, Boolean>>
    fun isModelExtracted(fileName: String): Boolean
    fun setModelExtracted(fileName: String, extracted: Boolean)
    fun validateInstalledModel(modelPath: String): Boolean
    fun validateModelWithSha(modelPath: String, expectedSha256: String): Boolean
    fun resolveModelFilePath(voiceId: String, internalFilesDir: File): File?
}

@Singleton
class ModelRepositoryImpl @Inject constructor() : ModelRepository {
    
    private val _voiceModels = MutableStateFlow(
        listOf(
            VoiceModelInfo("v1", "Piper US - Amy (Low)", "Acoustic VITS (English)", "8.2 MB", "Active", "en_US", "Compact feminine voice optimized for mobile edge runtimes."),
            VoiceModelInfo("v2", "Piper US - Ryan (Medium)", "Acoustic VITS (English)", "14.5 MB", "Standby", "en_US", "Expressive male speaker utilizing mid-density weights."),
            VoiceModelInfo("v3", "Kokila Telugu Voice Beta", "Swecha Fine-tuned (Telugu)", "11.8 MB", "Active", "te_IN", "Regional regional fine-tuned model utilizing the Swecha corpus."),
            VoiceModelInfo("v4", "Kokoro Premium HD Voice", "HD Mel-Acoustic Multi", "24.1 MB", "Downloadable", "en_US", "Studio-grade quality neural narrator. Requires higher CPU core capacity."),
            VoiceModelInfo("v5", "GGML Automotive Edition", "NPU Optimized VITS", "6.4 MB", "Downloadable", "en_US", "Optimized with int8 quantization specifically for car dashboards.")
        )
    )

    private val _extractionStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    override fun getVoiceModelsFlow(): StateFlow<List<VoiceModelInfo>> = _voiceModels.asStateFlow()

    override fun setVoiceModels(models: List<VoiceModelInfo>) {
        _voiceModels.value = models
    }

    override fun getExtractionStatusFlow(): StateFlow<Map<String, Boolean>> = _extractionStatus.asStateFlow()

    override fun isModelExtracted(fileName: String): Boolean {
        return _extractionStatus.value[fileName] ?: false
    }

    override fun setModelExtracted(fileName: String, extracted: Boolean) {
        val current = _extractionStatus.value.toMutableMap()
        current[fileName] = extracted
        _extractionStatus.value = current
    }

    override fun validateInstalledModel(modelPath: String): Boolean {
        val file = File(modelPath)
        return file.exists() && file.length() > 0
    }

    override fun validateModelWithSha(modelPath: String, expectedSha256: String): Boolean {
        val file = File(modelPath)
        return ModelIntegrityValidator.validateModelFile(file, expectedSha256)
    }

    override fun resolveModelFilePath(voiceId: String, internalFilesDir: File): File? {
        // Priority order: Bundled Model -> Imported Model -> Optional HTTPS Download
        
        // 1. Check Bundled Model (extracted/shipped to internal storage under models/)
        val bundledFile = File(internalFilesDir, "models/bundled_$voiceId.onnx")
        if (bundledFile.exists() && bundledFile.length() > 0) {
            return bundledFile
        }

        // 2. Check Imported Model (imported to internal storage under imports/)
        val importedFile = File(internalFilesDir, "imports/$voiceId.onnx")
        if (importedFile.exists() && importedFile.length() > 0) {
            return importedFile
        }

        // 3. Check Optional Downloaded Model (downloaded to internal storage under downloads/)
        val downloadedFile = File(internalFilesDir, "downloads/$voiceId.onnx")
        if (downloadedFile.exists() && downloadedFile.length() > 0) {
            return downloadedFile
        }

        return null
    }
}
