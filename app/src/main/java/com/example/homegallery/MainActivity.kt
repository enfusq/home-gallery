package com.example.homegallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.homegallery.ui.HomeGalleryApp
import com.example.homegallery.ui.theme.HomeGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeGalleryTheme {
                HomeGalleryApp()
            }
        }
    }
}