package com.example.ukt.request

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class LogoutRequest(
    val accessToken: String
)

data class AddBookRequest(
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String
)