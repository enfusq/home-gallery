@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.homegallery.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homegallery.R
import com.example.homegallery.ui.screens.HomeScreen
import com.example.homegallery.ui.viewmodels.ImageViewModel
import com.example.homegallery.ui.viewmodels.UploadResult
import kotlinx.coroutines.launch

@Composable
fun HomeGalleryApp() {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val imageViewModel: ImageViewModel = viewModel()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            HomeGalleryFab(
                onImageSelected = { uri ->
                    imageViewModel.uploadImage(uri) { result ->
                        when (result) {
                            is UploadResult.Success -> {
                                scope.launch { snackBarHostState.showSnackbar("Image uploaded successfully") }
                            }
                            is UploadResult.AlreadyExists -> {
                                scope.launch { snackBarHostState.showSnackbar("Image already exists") }
                            }
                            is UploadResult.Error -> {
                                scope.launch { snackBarHostState.showSnackbar(result.message) }
                            }
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding),
        ) {
            HomeScreen(
                imageUiState = imageViewModel.imageUiState,
                selectedImage = imageViewModel.selectedImage,
                onImageClicked = { imageViewModel.onImageClicked(it) },
                onDismissImage = { imageViewModel.onDismissImage() },
                onDeleteImage = { imageViewModel.deleteImage(it) },
                onDownloadImage = {imageViewModel.downloadImage(it)},
                downloadSuccess = imageViewModel.downloadSuccess,
                contentPadding = innerPadding
            )
        }
    }
}

@Composable
fun HomeGalleryFab(
    modifier: Modifier = Modifier,
    onImageSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let(onImageSelected)
    }

    FloatingActionButton(onClick = { launcher.launch("image/*") }) {
        Icon(
            painter = painterResource(R.drawable.add_24dp),
            contentDescription = stringResource(R.string.add_icon)
        )
    }
}