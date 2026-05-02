package com.example.homegallery

import com.example.homegallery.model.Image

fun fakeImage(id: Int = 1) = Image(
    id = id,
    imagePath = "http://example.com/image$id.jpg",
    userId = 1,
    originalName = "image$id.jpg",
    takenAt = "2024-01-01 00:00:00",
    takenAtUnix = 1704067200L,
    width = 100,
    height = 100
)
