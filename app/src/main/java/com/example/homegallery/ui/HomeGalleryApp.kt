@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.homegallery.ui

import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homegallery.R
import com.example.homegallery.ui.screens.HomeScreen
import com.example.homegallery.ui.screens.ImageViewModel

@Composable
fun HomeGalleryApp() {
    val imageViewModel: ImageViewModel = viewModel()

    Scaffold(
        bottomBar = {
            HomeGalleryBottomAppBar(
                onImageSelected = { uri ->
                    imageViewModel.uploadImage(uri)
                }
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen(
                imageUiState = imageViewModel.imageUiState,
                selectedImage = imageViewModel.selectedImage,
                onImageClicked = { imageViewModel.onImageClicked(it) },
                onDismissImage = { imageViewModel.onDismissImage() },
                onDeleteImage = { imageViewModel.deleteImage(it) },
                onDownloadImage = {imageViewModel.downloadImage(it)},
                downloadSuccess = imageViewModel.downloadSuccess,
                contentPadding = it
            )
        }
    }
}

@Composable
fun HomeGalleryBottomAppBar(
    modifier: Modifier = Modifier,
    onImageSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let(onImageSelected)
    }

    BottomAppBar(
        modifier = modifier
            .height(56.dp),
        actions = {
            IconButton(onClick = { launcher.launch("image/*") }) {
                Icon(
                    painter = painterResource(R.drawable.add_24dp),
                    contentDescription = stringResource(R.string.add_icon)
                )
            }
        }
    )
}