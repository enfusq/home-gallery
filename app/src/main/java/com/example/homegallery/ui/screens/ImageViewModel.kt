package com.example.homegallery.ui.screens

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
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

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    var imageUiState: ImageUiState by mutableStateOf(ImageUiState.Loading)
        private set

    private val imageRepository = NetworkImageRepository(ImageApi.retrofitService)

    init {
        getImages()
    }

    fun getImages() {
        viewModelScope.launch {
            imageUiState = ImageUiState.Loading
            imageUiState = try {
                ImageUiState.Success(imageRepository.getImages())
            } catch (e: HttpException) {
                ImageUiState.Error
            } catch (e: IOException) {
                ImageUiState.Error
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                imageRepository.uploadImage(application, imageUri)
                getImages()
            } catch (e: Exception) {
                imageUiState = ImageUiState.Error
            }
        }
    }
}