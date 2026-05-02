package com.example.homegallery

import android.content.Context
import android.net.Uri
import com.example.homegallery.data.ImageRepository
import com.example.homegallery.model.Image
import io.mockk.mockk

class FakeImageRepository : ImageRepository {
    var images: List<Image> = emptyList()
    var imageToReturn: Image? = null
    var getImagesException: Exception? = null
    var getImageException: Exception? = null
    var uploadException: Exception? = null
    var deleteException: Exception? = null
    var downloadSuccess = true

    override suspend fun getImages(): List<Image> {
        getImagesException?.let { throw it }
        return images
    }

    override suspend fun getImage(imageId: Int): Image {
        getImageException?.let { throw it }
        return imageToReturn ?: images.first { it.id == imageId }
    }

    override suspend fun uploadImage(context: Context, imageUri: Uri): Image {
        uploadException?.let { throw it }
        return imageToReturn ?: images.first()
    }

    override suspend fun deleteImage(imageId: Int) {
        deleteException?.let { throw it }
    }

    override suspend fun downloadImageToGallery(image: Image): Result<Uri> =
        if (downloadSuccess) Result.success(mockk(relaxed = true))
        else Result.failure(RuntimeException("Download failed"))
}

fun fakeImage(id: Int = 1) = Image(
    id = id,
    imagePath = "http://example.com/image$id.jpg",
    userId = 1,
    originalName = "image$id.jpg",
    takenAt = "2024-01-01 00:00:00",
    takenAtUnix = 1704067200L,
    width = 100,
    height = 100
)
