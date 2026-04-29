package com.example.homegallery.ui.screens.gallery

import com.example.homegallery.model.Image

data class GalleryActions(
    val onImageClicked: (Image) -> Unit,
    val onRetry: () -> Unit,
    val onRefresh: () -> Unit
)
