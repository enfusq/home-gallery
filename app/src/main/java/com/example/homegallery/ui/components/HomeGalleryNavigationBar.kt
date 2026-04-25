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