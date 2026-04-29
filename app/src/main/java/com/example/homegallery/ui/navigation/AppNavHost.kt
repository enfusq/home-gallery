package com.example.homegallery.ui.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.homegallery.R
import com.example.homegallery.ui.screens.gallery.GalleryEvent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.homegallery.ui.components.HomeGalleryFab
import com.example.homegallery.ui.screens.gallery.GalleryActions
import com.example.homegallery.ui.screens.gallery.GalleryScreen
import com.example.homegallery.ui.screens.gallery.GalleryStyle
import com.example.homegallery.ui.screens.gallery.GalleryViewModel
import com.example.homegallery.ui.screens.imagedetail.ImageDetailScreen
import com.example.homegallery.ui.screens.imagedetail.ImageDetailUiState
import com.example.homegallery.ui.screens.imagedetail.ImageDetailViewModel

private const val KEY_IMAGE_DELETED = "imageDeleted"

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {

    val context = LocalContext.current
    val galleryViewModel: GalleryViewModel = viewModel(factory = GalleryViewModel.factory(context))
    val galleryActions = GalleryActions(
        onImageClicked = { image -> navController.navigate(Screen.ImageDetail.route(image.id)) },
        onRetry = galleryViewModel::loadImages,
        onRefresh = galleryViewModel::refresh
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val showFab = currentBackStackEntry?.destination?.route != Screen.ImageDetail.route

    val snackbarHostState = remember { SnackbarHostState() }
    val duplicateImageMessage = stringResource(R.string.image_already_exists)

    LaunchedEffect(galleryViewModel) {
        galleryViewModel.events.collect { event ->
            when (event) {
                is GalleryEvent.DuplicateImage -> snackbarHostState.showSnackbar(duplicateImageMessage)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (showFab) {
                HomeGalleryFab(
                    onImageSelected = { uri -> galleryViewModel.uploadImage(context, uri) }
                )
            }
        },
    ) { _ ->
        NavHost(navController, Screen.Gallery.route, Modifier) {
            composable(Screen.Gallery.route) { backStackEntry ->
                val imageDeleted by backStackEntry.savedStateHandle
                    .getStateFlow(KEY_IMAGE_DELETED, false)
                    .collectAsStateWithLifecycle()

                LaunchedEffect(imageDeleted) {
                    if (imageDeleted) {
                        galleryViewModel.loadImages()
                        backStackEntry.savedStateHandle[KEY_IMAGE_DELETED] = false
                    }
                }

                val uiState by galleryViewModel.uiState.collectAsStateWithLifecycle()
                GalleryScreen(uiState, galleryActions, GalleryStyle.Masonry)
            }
            composable(Screen.Classic.route) { backStackEntry ->
                val imageDeleted by backStackEntry.savedStateHandle
                    .getStateFlow(KEY_IMAGE_DELETED, false)
                    .collectAsStateWithLifecycle()

                LaunchedEffect(imageDeleted) {
                    if (imageDeleted) {
                        galleryViewModel.loadImages()
                        backStackEntry.savedStateHandle[KEY_IMAGE_DELETED] = false
                    }
                }

                val uiState by galleryViewModel.uiState.collectAsStateWithLifecycle()
                GalleryScreen(uiState, galleryActions, GalleryStyle.Classic)
            }
            composable(
                route = Screen.ImageDetail.route,
                arguments = listOf(navArgument("imageId") { type = NavType.IntType })
            ) { backStackEntry ->
                val imageId = backStackEntry.arguments!!.getInt("imageId")
                val viewModel: ImageDetailViewModel = viewModel(
                    key = imageId.toString(),
                    factory = ImageDetailViewModel.factory(imageId, context)
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val downloaded by viewModel.downloaded.collectAsStateWithLifecycle()
                val actions = viewModel.asActions()

                LaunchedEffect(uiState) {
                    if (uiState is ImageDetailUiState.Deleted) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(KEY_IMAGE_DELETED, true)
                        navController.popBackStack()
                    }
                }

                ImageDetailScreen(
                    uiState = uiState,
                    downloaded = downloaded,
                    actions = actions,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
