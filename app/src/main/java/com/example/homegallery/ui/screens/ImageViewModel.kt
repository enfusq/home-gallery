package com.example.homegallery.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.network.HttpException
import com.example.homegallery.model.Image
import com.example.homegallery.network.ImageApi
import kotlinx.coroutines.launch
import okio.IOException

sealed interface ImageUiState {
    data class Success(val photos: List<Image>) : ImageUiState

    object Loading : ImageUiState
    object Error : ImageUiState
}

class ImageViewModel : ViewModel() {
    var imageUiState: ImageUiState by mutableStateOf(ImageUiState.Loading)
        private set

    init {
        getImages()
    }

    fun getImages() {
        viewModelScope.launch {
            imageUiState = ImageUiState.Loading
            imageUiState = try {
                val listResult = ImageApi.retrofitService.getImages()
                Log.d("API", listResult.toString())
                ImageUiState.Success(listResult)
            } catch (e: HttpException) {
                ImageUiState.Error
            }
        }
    }
}