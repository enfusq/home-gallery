package com.example.homegallery.ui.screens.gallery

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.homegallery.model.Image

@Composable
fun GalleryMasonryLayout(
    modifier: Modifier = Modifier,
    images: List<Image>,
    actions: GalleryActions,
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy((4.dp)),
        contentPadding = PaddingValues(5.dp),
        content = {
            items(
                items = images,
                key = { it.id }
            ) { image ->
                Log.d("IMAGE", image.imagePath)
                Box(modifier = Modifier
                    .clickable { actions.onImageClicked(image) }
                ) {
                    AsyncImage(
                        model = image.imagePath,
                        contentDescription = image.originalName,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(image.width.toFloat() / image.height.toFloat())
                    )
                }
            }
        },
    )
}