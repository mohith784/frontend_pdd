package com.example.grocerymanagerwithai.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ✅ Replace with your current PC IP from ipconfig
    private const val BASE_URL = "https://qzg134r4-80.inc1.devtunnels.ms/appp/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
