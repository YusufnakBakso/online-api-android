package com.example.ukt.response

import com.example.ukt.model.Book

data class LoginResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

data class RegisterResponse(
    val userId: Int,
    val message: String
)

data class RefreshTokenResponse(
    val message: String,
    val accessToken: String
)

data class LogoutResponse(
    val message: String

)
data class BookAddResponse(
    val status: String,
    val message: String,
    val data: BookAddData
)

data class BookAddData(
    val id: String,
    val title: String,
    val author: String
)
// Response data classes
data class BookDetailResponse(
    val status: String,
    val message: String,
    val data: Book
)