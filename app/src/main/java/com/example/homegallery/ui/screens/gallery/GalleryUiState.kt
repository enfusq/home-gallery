package com.example.homegallery.ui.screens.gallery

import com.example.homegallery.model.Image

data class GalleryUiState(
    val contentState: ContentState = ContentState.Loading,
    val isRefreshing: Boolean = false
)

sealed interface ContentState {
    object Loading : ContentState
    data class Success(val images: List<Image>) : ContentState
    data class Error(val message: String) : ContentState
}

sealed interface GalleryEvent {
    data object DuplicateImage : GalleryEvent
}