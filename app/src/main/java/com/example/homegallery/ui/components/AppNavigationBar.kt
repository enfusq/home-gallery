package com.example.homegallery.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.homegallery.R
import com.example.homegallery.ui.navigation.Screen

@Composable
fun HomeGalleryNavigationBar(
    currentRoute: String?,
    onRouteSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentRoute == Screen.Gallery.route,
            onClick = { onRouteSelected(Screen.Gallery.route) },
            label = { Text(stringResource(R.string.gallery)) },
            icon = {
                Icon(
                    painter = painterResource(
                        if (currentRoute == Screen.Gallery.route) R.drawable.auto_awesome_mosaic_filled_24dp
                        else R.drawable.auto_awesome_mosaic_24dp
                    ),
                    contentDescription = stringResource(R.string.gallery)
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Classic.route,
            onClick = { onRouteSelected(Screen.Classic.route) },
            label = { Text(stringResource(R.string.classic)) },
            icon = {
                Icon(
                    painter = painterResource(
                        if (currentRoute == Screen.Classic.route) R.drawable.photo_library_filled_24dp
                        else R.drawable.photo_library_24dp
                    ),
                    contentDescription = stringResource(R.string.classic)
                )
            }
        )
//        NavigationBarItem(
//            selected = currentRoute == Screen.Sync.route,
//            onClick = { onRouteSelected(Screen.Sync.route) },
//            label = { Text(stringResource(R.string.sync)) },
//            icon = {
//                Icon(
//                    painter = painterResource(R.drawable.sync_24dp),
//                    contentDescription = stringResource(R.string.sync)
//                )
//            }
//        )
    }
}