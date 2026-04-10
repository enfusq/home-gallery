package com.example.homegallery.network

import com.example.homegallery.model.Image
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

        @GET("images/{id}")
        suspend fun getImage(@Path("id") imageId: Int): Image

        @Multipart
        @POST("images")
        suspend fun uploadImage(
            @Part image: MultipartBody.Part,
            @Part("taken_at") takenAt: RequestBody
        ): Image

        @DELETE("images/{id}")
        suspend fun deleteImage(@Path("id") imageId: Int)
    }

    object ImageApi {
        val retrofitService: ImageApiService by lazy {
            retrofit.create(ImageApiService::class.java)
        }
    }
