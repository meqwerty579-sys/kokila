package com.example.core.model

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ModelManifestRepository(private val context: Context) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(ModelManifest::class.java)

    fun getManifest(): ModelManifest? {
        return try {
            val jsonString = context.assets.open("model_manifest.json")
                .bufferedReader()
                .use { it.readText() }
            adapter.fromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
