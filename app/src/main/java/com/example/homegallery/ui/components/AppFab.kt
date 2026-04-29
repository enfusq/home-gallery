package com.example.homegallery.ui.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.homegallery.R

@Composable
fun HomeGalleryFab(
    modifier: Modifier = Modifier,
    onImageSelected: (Uri) -> Unit
) {
    val intent = remember {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let(onImageSelected)
        }
    }

    FloatingActionButton(
        modifier = modifier,
        onClick = { launcher.launch(intent) }
    ) {
        Icon(
            painter = painterResource(R.drawable.add_24dp),
            contentDescription = stringResource(R.string.add_icon)
        )
    }
}
