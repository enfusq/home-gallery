package com.example.homegallery.ui.screens.gallery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.homegallery.R
import com.example.homegallery.fakeImage
import com.example.homegallery.model.Image
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(contentState = ContentState.Loading),
                actions = GalleryActions(onImageClicked = {}, onRetry = {}, onRefresh = {}),
                style = GalleryStyle.Masonry
            )
        }
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.loading))
            .assertIsDisplayed()
    }

    @Test
    fun errorState_showsMessageAndRetryButton() {
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(contentState = ContentState.Error("Something went wrong")),
                actions = GalleryActions(onImageClicked = {}, onRetry = {}, onRefresh = {}),
                style = GalleryStyle.Masonry
            )
        }
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.retry)).assertIsDisplayed()
    }

    @Test
    fun errorState_clickingRetry_callsOnRetry() {
        var retryCalled = false
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(contentState = ContentState.Error("Error")),
                actions = GalleryActions(
                    onImageClicked = {},
                    onRetry = { retryCalled = true },
                    onRefresh = {}
                ),
                style = GalleryStyle.Masonry
            )
        }
        composeTestRule.onNodeWithText(context.getString(R.string.retry)).performClick()
        assertTrue(retryCalled)
    }

    @Test
    fun successState_displaysAllImages() {
        val images = listOf(fakeImage(1), fakeImage(2))
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(contentState = ContentState.Success(images)),
                actions = GalleryActions(onImageClicked = {}, onRetry = {}, onRefresh = {}),
                style = GalleryStyle.Masonry
            )
        }
        images.forEach { image ->
            composeTestRule.onNodeWithContentDescription(image.originalName).assertExists()
        }
    }

    @Test
    fun successState_clickingImage_invokesOnImageClicked() {
        val image = fakeImage(1)
        var clickedImage: Image? = null
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(contentState = ContentState.Success(listOf(image))),
                actions = GalleryActions(
                    onImageClicked = { clickedImage = it },
                    onRetry = {},
                    onRefresh = {}
                ),
                style = GalleryStyle.Masonry
            )
        }
        composeTestRule.onNodeWithContentDescription(image.originalName).performClick()
        assertEquals(image, clickedImage)
    }
}
