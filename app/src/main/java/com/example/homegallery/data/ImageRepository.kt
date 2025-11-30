package com.example.homegallery.data

import com.example.homegallery.model.Image
import com.example.homegallery.network.ImageApiService

interface ImageRepository {
    suspend fun getImages(): List<Image>
}

class NetworkImageRepository(
    private val imageApiService: ImageApiService
): ImageRepository {
    override suspend fun getImages(): List<Image> = imageApiService.getImages()
}