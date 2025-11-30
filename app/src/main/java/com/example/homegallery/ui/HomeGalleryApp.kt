package com.example.homegallery.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homegallery.ui.screens.HomeScreen
import com.example.homegallery.ui.screens.ImageViewModel

@Composable
fun HomeGalleryApp() {
    Scaffold(
        topBar = { HomeGalleryTopAppBar() },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val imageViewModel: ImageViewModel = viewModel()
            HomeScreen(
                imageUiState = imageViewModel.imageUiState,
                contentPadding = it
            )
        }
    }
}


@Composable
fun HomeGalleryTopAppBar(modifier: Modifier = Modifier) {

}