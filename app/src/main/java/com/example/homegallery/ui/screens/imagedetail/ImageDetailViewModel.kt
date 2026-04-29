package com.example.homegallery.ui.screens.imagedetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.imageLoader
import com.example.homegallery.data.ImageRepository
import com.example.homegallery.data.NetworkImageRepository
import com.example.homegallery.model.Image
import com.example.homegallery.network.ImageApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed interface ImageDetailUiState {
    object Loading : ImageDetailUiState
    data class Success(val image: Image) : ImageDetailUiState
    data class Error(val message: String) : ImageDetailUiState
    object Deleted : ImageDetailUiState
}

data class ImageDetailActions(
    val onDownload: () -> Unit,
    val onDelete: () -> Unit
)

class ImageDetailViewModel(
    private val imageId: Int,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImageDetailUiState>(ImageDetailUiState.Loading)
    val uiState: StateFlow<ImageDetailUiState> = _uiState.asStateFlow()

    private val _downloaded = MutableStateFlow(false)
    val downloaded: StateFlow<Boolean> = _downloaded.asStateFlow()

    init {
        loadImage()
    }

    private fun loadImage() {
        viewModelScope.launch {
            try {
                _uiState.value = ImageDetailUiState.Success(imageRepository.getImage(imageId))
            } catch (e: Exception) {
                _uiState.value = ImageDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun downloadImage() {
        val state = _uiState.value as? ImageDetailUiState.Success ?: return
        viewModelScope.launch {
            val result = imageRepository.downloadImageToGallery(state.image)
            if (result.isSuccess) _downloaded.value = true
        }
    }

    fun deleteImage() {
        viewModelScope.launch {
            try {
                imageRepository.deleteImage(imageId)
                _uiState.value = ImageDetailUiState.Deleted
            } catch (e: Exception) {
                _uiState.value = ImageDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun asActions() = ImageDetailActions(
        onDelete = ::deleteImage,
        onDownload = ::downloadImage
    )

    companion object {
        fun factory(imageId: Int, context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ImageDetailViewModel(
                    imageId = imageId,
                    imageRepository = NetworkImageRepository(
                        imageApiService = ImageApi.retrofitService,
                        context = context.applicationContext,
                        imageLoader = context.applicationContext.imageLoader
                    )
                )
            }
        }
    }
}
