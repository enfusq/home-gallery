package com.example.homegallery.ui.screens.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homegallery.model.Image
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun GalleryClassicLayout(
    images: List<Image>,
    actions: GalleryActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val groupedImages = remember(images) {
        images
            .sortedByDescending { it.takenAtUnix }
            .groupBy { it.takenAt.take(10) }
            .toSortedMap(compareByDescending { it })
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedImages.forEach { (dateString, imagesForDate) ->
            stickyHeader(key = dateString) {
                DateHeader(dateString = dateString)
            }
            item(key = "group_$dateString") {
                GalleryDateGroup(
                    images = imagesForDate,
                    onImageClicked = actions.onImageClicked
                )
            }
        }
    }
}

@Composable
private fun DateHeader(
    dateString: String,
    modifier: Modifier = Modifier
) {
    val formatted = remember(dateString) {
        runCatching {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        }.getOrDefault(dateString) // fallback to raw string if parsing fails
    }

    Text(
        text = formatted,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun GalleryDateGroup(
    images: List<Image>,
    onImageClicked: (Image) -> Unit,
    modifier: Modifier = Modifier
) {
    NonLazyGrid(
        columns = 5,
        items = images,
        modifier = modifier.padding(horizontal = 2.dp)
    ) { image ->
        GalleryImageCard(
            image = image,
            onClick = { onImageClicked(image) },
            modifier = Modifier
                .aspectRatio(1f)
                .padding(1.dp)
        )
    }
}

@Composable
private fun <T> NonLazyGrid(
    columns: Int,
    items: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.chunked(columns).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        content(item)
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}