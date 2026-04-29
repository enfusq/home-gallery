package com.example.homegallery.ui.screens.imagedetail

import androidx.compose.runtime.Composable
import com.example.homegallery.ui.components.ErrorContent
import com.example.homegallery.ui.components.LoadingContent

@Composable
fun ImageDetailScreen(
    uiState: ImageDetailUiState,
    downloaded: Boolean,
    actions: ImageDetailActions,
    onNavigateBack: () -> Unit
) {
    when (val state = uiState) {
        is ImageDetailUiState.Loading -> LoadingContent()
        is ImageDetailUiState.Error -> ErrorContent(message = state.message)
        is ImageDetailUiState.Deleted -> Unit
        is ImageDetailUiState.Success -> ImageDetailContent(
            image = state.image,
            downloaded = downloaded,
            onNavigateBack = onNavigateBack,
            actions = actions
        )
    }
}
