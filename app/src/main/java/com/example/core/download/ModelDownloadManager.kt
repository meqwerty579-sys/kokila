package com.example.core.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ModelDownloadManager(private val okHttpClient: OkHttpClient = OkHttpClient()) {

    sealed class DownloadState {
        data class Progress(val percentage: Int, val bytesDownloaded: Long, val totalBytes: Long) : DownloadState()
        data class Success(val file: File) : DownloadState()
        data class Error(val message: String) : DownloadState()
    }

    fun downloadFile(url: String, destinationFile: File): Flow<DownloadState> = flow {
        if (!url.startsWith("https://", ignoreCase = true)) {
            emit(DownloadState.Error("Insecure URL rejected: Only HTTPS is allowed."))
            return@flow
        }

        var success = false
        var retryCount = 0
        val maxRetries = 3
        var errorMessage = ""

        while (!success && retryCount < maxRetries) {
            try {
                val startByte = if (destinationFile.exists()) destinationFile.length() else 0L
                val requestBuilder = Request.Builder().url(url)
                
                if (startByte > 0) {
                    requestBuilder.addHeader("Range", "bytes=$startByte-")
                }

                val request = requestBuilder.build()
                val response = okHttpClient.newCall(request).execute()

                if (!response.isSuccessful) {
                    if (response.code == 416) {
                        response.close()
                        success = true
                        emit(DownloadState.Success(destinationFile))
                        return@flow
                    }
                    throw IOException("HTTP error: ${response.code} ${response.message}")
                }

                val responseBody = response.body ?: throw IOException("Empty response body")
                val totalLength = responseBody.contentLength() + startByte
                
                val append = startByte > 0 && response.code == 206
                
                FileOutputStream(destinationFile, append).use { output ->
                    val input = responseBody.byteStream()
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var bytesDownloaded = if (append) startByte else 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        bytesDownloaded += bytesRead
                        
                        val progress = if (totalLength > 0) {
                            ((bytesDownloaded * 100) / totalLength).toInt()
                        } else {
                            0
                        }
                        emit(DownloadState.Progress(progress, bytesDownloaded, totalLength))
                    }
                    output.flush()
                }
                
                response.close()
                success = true
                emit(DownloadState.Success(destinationFile))
                
            } catch (e: Exception) {
                retryCount++
                errorMessage = e.localizedMessage ?: "Unknown download error"
                if (retryCount >= maxRetries) {
                    emit(DownloadState.Error("Download failed after $maxRetries retries. Last error: $errorMessage"))
                } else {
                    delay(1000L * retryCount)
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
