package com.example.homegallery.ui.screens.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.homegallery.model.Image

@Composable
fun GalleryImageCard(
    modifier: Modifier,
    image: Image,
    onClick: (Image) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(image) }
    ) {
        AsyncImage(
            model = image.imagePath,
            contentDescription = image.originalName,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
    }
}