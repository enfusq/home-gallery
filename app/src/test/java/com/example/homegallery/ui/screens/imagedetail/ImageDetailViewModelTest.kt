package com.example.homegallery.ui.screens.imagedetail

import com.example.homegallery.FakeImageRepository
import com.example.homegallery.MainDispatcherRule
import com.example.homegallery.fakeImage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ImageDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init - success - state becomes Success with correct image`() = runTest {
        val image = fakeImage(42)
        val repo = FakeImageRepository().apply { images = listOf(image) }

        val vm = ImageDetailViewModel(imageId = 42, imageRepository = repo)

        val state = vm.uiState.value
        assertTrue(state is ImageDetailUiState.Success)
        assertEquals(image, (state as ImageDetailUiState.Success).image)
    }

    @Test
    fun `init - failure - state becomes Error with message`() = runTest {
        val repo = FakeImageRepository().apply {
            getImageException = RuntimeException("Not found")
        }

        val vm = ImageDetailViewModel(imageId = 1, imageRepository = repo)

        val state = vm.uiState.value
        assertTrue(state is ImageDetailUiState.Error)
        assertEquals("Not found", (state as ImageDetailUiState.Error).message)
    }

    @Test
    fun `deleteImage - success - state becomes Deleted`() = runTest {
        val repo = FakeImageRepository().apply { images = listOf(fakeImage(1)) }
        val vm = ImageDetailViewModel(imageId = 1, imageRepository = repo)

        vm.deleteImage()

        assertTrue(vm.uiState.value is ImageDetailUiState.Deleted)
    }

    @Test
    fun `deleteImage - failure - state becomes Error`() = runTest {
        val repo = FakeImageRepository().apply {
            images = listOf(fakeImage(1))
            deleteException = RuntimeException("Server error")
        }
        val vm = ImageDetailViewModel(imageId = 1, imageRepository = repo)

        vm.deleteImage()

        val state = vm.uiState.value
        assertTrue(state is ImageDetailUiState.Error)
        assertEquals("Server error", (state as ImageDetailUiState.Error).message)
    }

    @Test
    fun `downloadImage - success - downloaded becomes true`() = runTest {
        val repo = FakeImageRepository().apply {
            images = listOf(fakeImage(1))
            downloadSuccess = true
        }
        val vm = ImageDetailViewModel(imageId = 1, imageRepository = repo)

        vm.downloadImage()

        assertTrue(vm.downloaded.value)
    }

    @Test
    fun `downloadImage - failure - downloaded stays false`() = runTest {
        val repo = FakeImageRepository().apply {
            images = listOf(fakeImage(1))
            downloadSuccess = false
        }
        val vm = ImageDetailViewModel(imageId = 1, imageRepository = repo)

        vm.downloadImage()

        assertFalse(vm.downloaded.value)
    }
}
