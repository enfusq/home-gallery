package com.example.homegallery.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.example.homegallery.model.Image
import com.example.homegallery.network.ImageApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ImageRepository {
    suspend fun getImages(): List<Image>
    suspend fun uploadImage(context: Context, imageUri: Uri): Image
    suspend fun deleteImage(imageId: Int)
}

class NetworkImageRepository(
    private val imageApiService: ImageApiService
): ImageRepository {
    override suspend fun getImages(): List<Image> = imageApiService.getImages()

    override suspend fun uploadImage(context: Context, imageUri: Uri): Image {
        val takenAtString = getExifDateTime(context, imageUri)

        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("Couldn't open InputStream for given URI")

        val bytes = inputStream.use { it.readBytes() }

        val requestFile = bytes.toRequestBody(
            context.contentResolver.getType(imageUri)?.toMediaTypeOrNull()
        )

        val body = MultipartBody.Part.createFormData(
            name = "image",
            filename = "image.jpg",
            body = requestFile
        )

        val takenAtPart = takenAtString.toRequestBody("text/plain".toMediaTypeOrNull())

        return imageApiService.uploadImage(body, takenAtPart)
    }

    override suspend fun deleteImage(imageId: Int) {
        imageApiService.deleteImage(imageId)
    }

    private fun getExifDateTime(context: Context, imageUri: Uri): String {
        try {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val exifInterface = ExifInterface(inputStream)
                val dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)

                if (dateTime != null) {
                    val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = inputFormat.parse(dateTime)
                    if (date != null) {
                        return outputFormat.format(date)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EXIF", "Failed to read EXIF from image")
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}