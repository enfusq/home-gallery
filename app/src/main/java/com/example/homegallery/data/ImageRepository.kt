package com.example.homegallery.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.example.homegallery.model.Image
import com.example.homegallery.network.ImageApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ImageRepository {
    suspend fun getImages(): List<Image>
    suspend fun getImage(imageId: Int): Image
    suspend fun uploadImage(context: Context, imageUri: Uri): Image
    suspend fun deleteImage(imageId: Int)
    suspend fun downloadImageToGallery(image: Image): Result<Uri>
}

class NetworkImageRepository(
    private val imageApiService: ImageApiService,
    private val context: Context,
    private val imageLoader: ImageLoader
): ImageRepository {
    override suspend fun getImages(): List<Image> = imageApiService.getImages()

    override suspend fun getImage(imageId: Int): Image = imageApiService.getImage(imageId)

    override suspend fun uploadImage(context: Context, imageUri: Uri): Image {
        val takenAtString = getExifDateTime(context, imageUri)

        val filename = context.contentResolver
            .query(imageUri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            } ?: "image.jpg"

        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("Couldn't open InputStream for given URI")

        val bytes = inputStream.use { it.readBytes() }

        val requestFile = bytes.toRequestBody(
            context.contentResolver.getType(imageUri)?.toMediaTypeOrNull()
        )

        val body = MultipartBody.Part.createFormData(
            name = "image",
            filename = filename,
            body = requestFile
        )

        val takenAtPart = takenAtString.toRequestBody("text/plain".toMediaTypeOrNull())

        return imageApiService.uploadImage(body, takenAtPart)
    }

    override suspend fun deleteImage(imageId: Int) {
        Log.d("IMAGE", "Delete Called")
        imageApiService.deleteImage(imageId)
    }

    override suspend fun downloadImageToGallery(image: Image): Result<Uri> =
        withContext(Dispatchers.IO) {
            runCatching {
                Log.d("IMAGE", "Download Called")
                val request = ImageRequest.Builder(context)
                    .data(image.imagePath)
                    .allowHardware(false)
                    .build()

                val result = imageLoader.execute(request)
                if (result is ErrorResult) error(result.throwable.message ?: "Failed to load image")
                val bitmap = (result as SuccessResult).image.toBitmap()

                saveToGallery(bitmap, image)
            }
        }

    private fun saveToGallery(bitmap: Bitmap, image: Image): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(bitmap, image)
        } else {
            saveViaFileSystem(bitmap, image.originalName)
        }
    }

    private fun saveViaMediaStore(bitmap: Bitmap, image: Image): Uri {
        Log.d("IMAGE", "SaveViaMediaStore Called")
        val mimeType = when (image.originalName.substringAfterLast('.').lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }

        val compressFormat = when (image.originalName.substringAfterLast('.').lowercase()) {
            "png" -> Bitmap.CompressFormat.PNG
            "webp" -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, image.originalName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/HomeGallery")
            put(MediaStore.Images.Media.WIDTH, image.width)
            put(MediaStore.Images.Media.HEIGHT, image.height)
            put(MediaStore.Images.Media.DATE_TAKEN, image.takenAtUnix)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: error("Failed to create new MediaStore entry")

        resolver.openOutputStream(uri)!!.use { stream ->
            bitmap.compress(compressFormat, 100, stream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)

        return uri
    }

    private fun saveViaFileSystem(bitmap: Bitmap, fileName: String): Uri {
        Log.d("IMAGE", "SaveViaFileSystem Called")
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(path, fileName)

        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }

        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
        return Uri.fromFile(file)
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
            Log.e("EXIF", e.toString())
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}