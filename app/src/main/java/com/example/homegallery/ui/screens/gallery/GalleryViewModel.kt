package com.example.homegallery.ui.screens.gallery

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.imageLoader
import com.example.homegallery.data.ImageRepository
import com.example.homegallery.data.NetworkImageRepository
import com.example.homegallery.network.ImageApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GalleryViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GalleryViewModel(
                    NetworkImageRepository(
                        imageApiService = ImageApi.retrofitService,
                        context = context.applicationContext,
                        imageLoader = context.applicationContext.imageLoader
                    )
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GalleryEvent>()
    val events: SharedFlow<GalleryEvent> = _events.asSharedFlow()

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(contentState = ContentState.Loading) }
            fetchImages()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchImages()
        }
    }

    private suspend fun fetchImages() {
        try {
            _uiState.update { it.copy(
                contentState = ContentState.Success(imageRepository.getImages()),
                isRefreshing = false
            )}
        } catch (e: Exception) {
            _uiState.update { it.copy(
                contentState = ContentState.Error(e.message ?: "Unknown error"),
                isRefreshing = false
            )}
        }
    }

    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                imageRepository.uploadImage(context, uri)
                loadImages()
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    _events.emit(GalleryEvent.DuplicateImage)
                } else {
                    _uiState.update { it.copy(contentState = ContentState.Error(e.message ?: "Unknown error")) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(contentState = ContentState.Error(e.message ?: "Unknown error")) }
            }
        }
    }

}