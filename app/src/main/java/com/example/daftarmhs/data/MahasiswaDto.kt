package com.example.daftarmhs.data

data class MahasiswaDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String? = null
)
