package com.example.core.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelManifest(
    val models: List<ModelItem>
)

@JsonClass(generateAdapter = true)
data class ModelItem(
    val id: String,
    val version: String,
    val sha256: String,
    val language: String,
    val sizeMb: Int
)
