package com.example.homegallery.ui.navigation

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Classic : Screen("classic")
    object Sync : Screen("sync")
    object ImageDetail : Screen("image_detail/{imageId}") {
        fun route(imageId: Int) = "image_detail/$imageId"
    }
}