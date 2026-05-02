package com.example.homegallery.ui.screens.imagedetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.homegallery.R
import com.example.homegallery.fakeImage
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Loading,
                downloaded = false,
                actions = ImageDetailActions(onDownload = {}, onDelete = {}),
                onNavigateBack = {}
            )
        }
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.loading))
            .assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Error("Image not found"),
                downloaded = false,
                actions = ImageDetailActions(onDownload = {}, onDelete = {}),
                onNavigateBack = {}
            )
        }
        composeTestRule.onNodeWithText("Image not found").assertIsDisplayed()
    }

    @Test
    fun successState_showsImageName() {
        val image = fakeImage(1)
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Success(image),
                downloaded = false,
                actions = ImageDetailActions(onDownload = {}, onDelete = {}),
                onNavigateBack = {}
            )
        }
        composeTestRule.onNodeWithText(image.originalName).assertIsDisplayed()
    }

    @Test
    fun successState_downloadButton_isEnabledWhenNotDownloaded() {
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Success(fakeImage()),
                downloaded = false,
                actions = ImageDetailActions(onDownload = {}, onDelete = {}),
                onNavigateBack = {}
            )
        }
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.download_icon))
            .assertIsEnabled()
    }

    @Test
    fun successState_downloadButton_isDisabledAfterDownload() {
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Success(fakeImage()),
                downloaded = true,
                actions = ImageDetailActions(onDownload = {}, onDelete = {}),
                onNavigateBack = {}
            )
        }
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.download_icon))
            .assertIsNotEnabled()
    }

    @Test
    fun successState_clickingDeleteButton_showsConfirmDialog() {
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Success(fakeImage()),
                downloaded = false,
                actions = ImageDetailActions(onDownload = {}, onDelete = {}),
                onNavigateBack = {}
            )
        }
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.delete_icon))
            .performClick()
        composeTestRule
            .onNodeWithText(context.getString(R.string.delete_confirm_title))
            .assertIsDisplayed()
    }

    @Test
    fun successState_confirmingDelete_callsOnDelete() {
        var deleteCalled = false
        composeTestRule.setContent {
            ImageDetailScreen(
                uiState = ImageDetailUiState.Success(fakeImage()),
                downloaded = false,
                actions = ImageDetailActions(onDownload = {}, onDelete = { deleteCalled = true }),
                onNavigateBack = {}
            )
        }
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.delete_icon))
            .performClick()
        composeTestRule
            .onNodeWithText(context.getString(R.string.delete))
            .performClick()
        assertTrue(deleteCalled)
    }
}
