@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.homegallery.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homegallery.R
import com.example.homegallery.ui.screens.HomeScreen
import com.example.homegallery.ui.screens.ImageViewModel

@Composable
fun HomeGalleryApp() {

    Scaffold(
        bottomBar = { HomeGalleryBottomAppBar() },
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
fun HomeGalleryBottomAppBar(modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> }

    BottomAppBar(
        modifier = modifier
            .height(64.dp),
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