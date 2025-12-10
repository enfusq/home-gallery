package com.example.homegallery.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.homegallery.R
import com.example.homegallery.model.Image

@Composable
fun HomeScreen(
    imageUiState: ImageUiState,
    selectedImage: Image?,
    onImageClicked: (Image) -> Unit,
    onDismissImage: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding))
    {
        when (imageUiState) {
            is ImageUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
            is ImageUiState.Success -> ResultScreen(
                images = imageUiState.photos,
                onImageClicked = onImageClicked,
                modifier = modifier.fillMaxSize()
            )
            is ImageUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
        }

        if (selectedImage != null) {
            SingleImageScreen(
                image = selectedImage,
                onDismiss = onDismissImage
            )
        }
    }

}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ResultScreen(
    images: List<Image>,
    onImageClicked: (Image) -> Unit,
    modifier: Modifier = Modifier
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
                    .clickable { onImageClicked(image) }
                ) {
                    AsyncImage(
                        model = image.imagePath,
                        contentDescription = image.originalName,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        },
    )
}

@Composable
fun SingleImageScreen(
    image: Image,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = image.imagePath,
            contentDescription = image.originalName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable(enabled = false, onClick = {})
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}