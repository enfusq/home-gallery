package com.example.homegallery.ui.screens.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homegallery.ui.components.ErrorContent
import com.example.homegallery.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    uiState: GalleryUiState,
    actions: GalleryActions,
    style: GalleryStyle,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = actions.onRefresh,
        modifier = modifier
    ) {
        when (val content = uiState.contentState) {
            is ContentState.Loading -> LoadingContent()
            is ContentState.Error -> ErrorContent(
                message = content.message,
                onRetry = actions.onRetry
            )
            is ContentState.Success -> when (style) {
                GalleryStyle.Masonry -> GalleryMasonryLayout(
                    images = content.images,
                    actions = actions
                )
                GalleryStyle.Classic -> GalleryClassicLayout(
                    images = content.images,
                    actions = actions,
                    contentPadding = contentPadding
                )
            }
        }
    }
}
