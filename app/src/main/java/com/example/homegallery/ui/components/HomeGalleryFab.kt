package com.example.homegallery.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.homegallery.R

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
