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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
        bottomBar = {
            HomeGalleryNavigationBar(
                selectedTab = 0,
                onTabSelected = { }
            )
        },
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

@Composable
fun HomeGalleryNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            label = { Text(stringResource(R.string.gallery)) },
            icon = {
                Icon(
                    painter = painterResource(
                        if (selectedTab == 0) R.drawable.auto_awesome_mosaic_filled_24dp
                        else R.drawable.auto_awesome_mosaic_24dp
                    ),
                    contentDescription = stringResource(R.string.gallery)
                )
            }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            label = { Text(stringResource(R.string.classic)) },
            icon = {
                Icon(
                    painter = painterResource(
                        if (selectedTab == 1) R.drawable.photo_library_filled_24dp
                        else R.drawable.photo_library_24dp
                    ),
                    contentDescription = stringResource(R.string.classic)
                )
            }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            label = { Text(stringResource(R.string.sync)) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.sync_24dp),
                    contentDescription = stringResource(R.string.sync)
                )
            }
        )
    }
}