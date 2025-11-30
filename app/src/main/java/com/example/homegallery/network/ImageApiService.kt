package com.example.homegallery.network

import com.example.homegallery.model.Image
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET

    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    interface ImageApiService {
        @GET("images")
        suspend fun getImages(): List<Image>
    }

    object ImageApi {
        val retrofitService: ImageApiService by lazy {
            retrofit.create(ImageApiService::class.java)
        }
    }
