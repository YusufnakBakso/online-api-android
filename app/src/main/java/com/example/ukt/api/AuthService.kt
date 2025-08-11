package com.example.ukt.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.ukt.model.BookResponse
import com.example.ukt.model.SingleBookResponse
import com.example.ukt.model.BookRequest
import com.example.ukt.request.AddBookRequest
import com.example.ukt.request.LoginRequest
import com.example.ukt.request.LogoutRequest
import com.example.ukt.request.RefreshTokenRequest
import com.example.ukt.request.RegisterRequest
import com.example.ukt.response.LoginResponse
import com.example.ukt.response.LogoutResponse
import com.example.ukt.response.RegisterResponse
import com.example.ukt.response.RefreshTokenResponse
import com.example.ukt.response.BookAddResponse
import com.example.ukt.response.BookDetailResponse
import com.example.ukt.model.UpdateBookRequest
import com.example.ukt.model.DeleteResponse

interface AuthService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @POST("logout")
    suspend fun logout(
        @Body request: LogoutRequest,
        @Header("Authorization") token: String? = null
    ): Response<LogoutResponse>

    // Book endpoints
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100,
        @Header("Authorization") token: String? = null
    ): Response<BookResponse>

    @GET("books/{id}")
    suspend fun getBookById(
        @Path("id") id: String,
        @Header("Authorization") token: String?
    ): Response<BookDetailResponse>


    @POST("books")
    suspend fun addBook(
        @Body request: AddBookRequest,
        @Header("Authorization") token: String
    ): Response<BookAddResponse>

    // Update/Edit book
    @PUT("books/{id}")
    suspend fun updateBook(
        @Path("id") bookId: String,
        @Body request: UpdateBookRequest, // Changed from BookRequest
        @Header("Authorization") authHeader: String?
    ): Response<BookResponse>


    // Delete book
    @DELETE("books/{id}")
    suspend fun deleteBook(
        @Path("id") bookId: String,
        @Header("Authorization") authHeader: String?
    ): Response<DeleteResponse>
}
