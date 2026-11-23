package com.example.daftarmhs.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MahasiswaApi {
    @GET("users")
    suspend fun getUsers(): List<MahasiswaDto>

    @GET("users/{id}")
    suspend fun getDetail(@Path("id") id: Int): MahasiswaDto

    @GET("todos")
    suspend fun getTugas(@Query("userId") userId: Int): List<TugasDto>
}
