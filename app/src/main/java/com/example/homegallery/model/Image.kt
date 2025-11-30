package com.example.homegallery.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image (
    val id: Int,
    @SerialName(value = "image_path")
    val imagePath: String,
    @SerialName(value = "user_id")
    val userId: Int,
    @SerialName(value = "original_name")
    val originalName: String,
    @SerialName(value = "taken_at")
    val takenAt: String
)