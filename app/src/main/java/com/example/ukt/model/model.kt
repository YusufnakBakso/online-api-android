package com.example.ukt.model

data class Book(
    val id: Int,
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String,
    val cover_image: CoverImage,
    val uploaded_by: String,
    val coverImage: String,
    val publishedDate: String
)

data class CoverImage(
    val small: String,
    val medium: String,
    val large: String
)

data class BookResponse(
    val status: String,
    val message: String,
    val data: BookData,
)

data class BookData(
    val books: List<Book>,
    val total: Int,
    val page: Int,
    val limit: Int
)

data class SingleBookResponse(
    val status: String,
    val message: String,
    val data: Book
)

data class BookRequest(
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String
)

// Data class untuk request update book
data class UpdateBookRequest(
    val title: String,
    val author: String,
    val isbn: String?,
    val publisher: String?,
    val publishedDate: String?,
    val genre: String?,
    val language: String?,
    val description: String?
)

// Data class untuk response delete
data class DeleteResponse(
    val status: String,
    val message: String
)