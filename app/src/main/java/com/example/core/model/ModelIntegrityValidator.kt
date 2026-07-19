package com.example.core.model

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object ModelIntegrityValidator {

    fun calculateSha256(file: File): String {
        if (!file.exists()) return ""
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead = fis.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = fis.read(buffer)
                }
            }
            val hashBytes = digest.digest()
            val sb = StringBuilder()
            for (b in hashBytes) {
                sb.append(String.format("%02x", b))
            }
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun validateModelFile(file: File, expectedSha256: String): Boolean {
        if (!file.exists() || file.length() == 0L) return false
        val actualSha = calculateSha256(file)
        return actualSha.equals(expectedSha256, ignoreCase = true)
    }

    fun validateModelBundle(
        modelFile: File,
        tokensFile: File,
        lexiconFile: File,
        expectedOnnxSha256: String
    ): Boolean {
        if (!validateModelFile(modelFile, expectedOnnxSha256)) return false
        if (!tokensFile.exists() || tokensFile.length() == 0L) return false
        if (!lexiconFile.exists() || lexiconFile.length() == 0L) return false
        return true
    }
}
