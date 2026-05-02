package com.example.homegallery.ui.screens.gallery

import android.content.Context
import android.net.Uri
import com.example.homegallery.FakeImageRepository
import com.example.homegallery.MainDispatcherRule
import com.example.homegallery.fakeImage
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadImages - success - state becomes Success with image list`() = runTest {
        val images = listOf(fakeImage(1), fakeImage(2))
        val repo = FakeImageRepository().apply { this.images = images }

        val vm = GalleryViewModel(repo)

        val state = vm.uiState.value.contentState
        assertTrue(state is ContentState.Success)
        assertEquals(images, (state as ContentState.Success).images)
    }

    @Test
    fun `loadImages - failure - state becomes Error with message`() = runTest {
        val repo = FakeImageRepository().apply {
            getImagesException = RuntimeException("Network error")
        }

        val vm = GalleryViewModel(repo)

        val state = vm.uiState.value.contentState
        assertTrue(state is ContentState.Error)
        assertEquals("Network error", (state as ContentState.Error).message)
    }

    @Test
    fun `refresh - isRefreshing resets to false after completion`() = runTest {
        val repo = FakeImageRepository().apply { images = listOf(fakeImage(1)) }
        val vm = GalleryViewModel(repo)

        vm.refresh()

        assertFalse(vm.uiState.value.isRefreshing)
    }

    @Test
    fun `uploadImage - 409 conflict - emits DuplicateImage event`() = runTest {
        val response = Response.error<Any>(409, "".toResponseBody(null))
        val repo = FakeImageRepository().apply {
            images = listOf(fakeImage(1))
            uploadException = HttpException(response)
        }
        val vm = GalleryViewModel(repo)

        val events = mutableListOf<GalleryEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            vm.events.collect { events.add(it) }
        }

        vm.uploadImage(mockk<Context>(), mockk<Uri>())

        assertEquals(listOf(GalleryEvent.DuplicateImage), events)
    }

    @Test
    fun `uploadImage - success - reloads image list`() = runTest {
        val initial = listOf(fakeImage(1))
        val updated = listOf(fakeImage(1), fakeImage(2))
        val repo = FakeImageRepository().apply { images = initial }
        val vm = GalleryViewModel(repo)

        assertEquals(initial, (vm.uiState.value.contentState as ContentState.Success).images)

        repo.images = updated
        vm.uploadImage(mockk<Context>(), mockk<Uri>())

        assertEquals(updated, (vm.uiState.value.contentState as ContentState.Success).images)
    }
}
