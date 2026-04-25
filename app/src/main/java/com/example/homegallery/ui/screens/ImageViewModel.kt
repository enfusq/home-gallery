package com.example.homegallery.ui.screens

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.network.HttpException
import com.example.homegallery.data.NetworkImageRepository
import com.example.homegallery.model.Image
import com.example.homegallery.network.ImageApi
import kotlinx.coroutines.launch
import okio.IOException

sealed interface ImageUiState {
    data class Success(val photos: List<Image>) : ImageUiState

    object Loading : ImageUiState
    object Error : ImageUiState
}

sealed class UploadResult {
    data object Success : UploadResult()
    data object AlreadyExists : UploadResult()
    data class Error(val message: String) : UploadResult()
}

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    var imageUiState: ImageUiState by mutableStateOf(ImageUiState.Loading)
        private set

    var selectedImage by mutableStateOf<Image?>(null)
        private set

    var downloadSuccess by mutableStateOf(false)
        private set

    private val imageLoader = ImageLoader.Builder(application).build()

    private val imageRepository = NetworkImageRepository(
        imageApiService = ImageApi.retrofitService,
        context = application,
        imageLoader = imageLoader
    )

    init {
        getImages()
    }

    fun onImageClicked(image: Image) {
        selectedImage = image
    }

    fun onDismissImage() {
        selectedImage = null
    }

    fun getImages() {
        viewModelScope.launch {
            imageUiState = ImageUiState.Loading
            imageUiState = try {
                ImageUiState.Success(imageRepository.getImages())
            } catch (e: HttpException) {
                Log.e("API", e.toString())
                ImageUiState.Error
            } catch (e: IOException) {
                Log.e("API", e.toString())
                ImageUiState.Error
            } catch (e: Exception) {
                Log.e("API", e.toString())
                ImageUiState.Error
            }
        }
    }

    fun uploadImage(imageUri: Uri, onResult: (UploadResult) -> Unit) {
        viewModelScope.launch {
            try {
                imageRepository.uploadImage(application, imageUri)
                getImages()
                onResult(UploadResult.Success)
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 409) {
                    onResult(UploadResult.AlreadyExists)
                } else {
                    Log.e("API", "HTTP Error: ${e.code()}")
                    onResult(UploadResult.Error("Server error: ${e.code()}"))
                }
            } catch (e: Exception) {
                Log.e("API", e.toString())
                onResult(UploadResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    fun deleteImage(image: Image) {
        viewModelScope.launch {
            try {
                imageRepository.deleteImage(image.id)
                onDismissImage()
                getImages()
            } catch (e: Exception) {
                Log.e("API", e.toString())
                imageUiState = ImageUiState.Error
            }
        }
    }

    fun downloadImage(image: Image) {
        viewModelScope.launch {
            try {
                imageRepository.downloadImageToGallery(image)
                    .onSuccess { downloadSuccess = true}
                    .onFailure { imageUiState = ImageUiState.Error }

            } catch (e: Exception) {
                Log.e("API", e.toString())
                imageUiState = ImageUiState.Error
            }
        }
    }
}