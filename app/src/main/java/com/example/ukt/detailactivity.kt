package com.example.ukt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ukt.api.ApiClient
import com.example.ukt.model.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class detailactivity : AppCompatActivity() {

    private lateinit var detailCoverImageView: ImageView
    private lateinit var detailTitleTextView: TextView
    private lateinit var detailAuthorTextView: TextView
    private lateinit var detailIsbnTextView: TextView
    private lateinit var detailPublisherTextView: TextView
    private lateinit var detailPublishedDateTextView: TextView
    private lateinit var detailGenreTextView: TextView
    private lateinit var detailLanguageTextView: TextView
    private lateinit var detailDescriptionTextView: TextView
    private lateinit var closeButton: Button
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var loadingView: View

    private var bookId: String? = null
    private var currentBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailactivity)

        initViews()
        handleBookId()
        setupButtons()
        fetchBookDetails()
    }

    override fun onResume() {
        super.onResume()
        // Refresh book details when returning from edit activity
        fetchBookDetails()
    }

    private fun handleBookId() {
        // Try to get as Integer first
        val intId = intent.getIntExtra("BOOK_ID", -1)
        bookId = if (intId != -1) {
            intId.toString()
        } else {
            // If failed, try to get as String
            intent.getStringExtra("BOOK_ID")
        }

        if (bookId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid book ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initViews() {
        detailCoverImageView = findViewById(R.id.detailCoverImageView)
        detailTitleTextView = findViewById(R.id.detailTitleTextView)
        detailAuthorTextView = findViewById(R.id.detailAuthorTextView)
        detailIsbnTextView = findViewById(R.id.detailIsbnTextView)
        detailPublisherTextView = findViewById(R.id.detailPublisherTextView)
        detailPublishedDateTextView = findViewById(R.id.detailPublishedDateTextView)
        detailGenreTextView = findViewById(R.id.detailGenreTextView)
        detailLanguageTextView = findViewById(R.id.detailLanguageTextView)
        detailDescriptionTextView = findViewById(R.id.detailDescriptionTextView)
        closeButton = findViewById(R.id.closeButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)
        loadingView = findViewById(R.id.loadingView)
    }

    private fun setupButtons() {
        closeButton.setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            editBook()
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun fetchBookDetails() {
        if (bookId.isNullOrEmpty()) return

        showLoading(true)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        val authHeader = accessToken?.let { "Bearer $it" }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.authService.getBookById(bookId!!, authHeader)

                withContext(Dispatchers.Main) {
                    showLoading(false)

                    when {
                        response.isSuccessful && response.body() != null -> {
                            val book = response.body()!!.data
                            currentBook = book
                            displayBookDetails(book)
                        }
                        else -> handleError(response.errorBody()?.string())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@detailactivity,
                        "Network error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun displayBookDetails(book: Book) {
        detailTitleTextView.text = book.title
        detailAuthorTextView.text = book.author
        detailIsbnTextView.text = book.isbn ?: "N/A"
        detailPublisherTextView.text = book.publisher ?: "N/A"
        detailPublishedDateTextView.text = book.publishedDate ?: "N/A"
        detailGenreTextView.text = book.genre ?: "N/A"
        detailLanguageTextView.text = book.language ?: "N/A"
        detailDescriptionTextView.text = book.description ?: "No description available"

        book.cover_image?.medium?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_book_placeholder)
                .into(detailCoverImageView)
        } ?: run {
            detailCoverImageView.setImageResource(R.drawable.ic_book_placeholder)
        }
    }

    private fun editBook() {
        currentBook?.let { book ->
            // Navigate to edit activity
            val intent = Intent(this, edit::class.java).apply {
                putExtra("BOOK_ID", bookId)
                putExtra("BOOK_TITLE", book.title)
                putExtra("BOOK_AUTHOR", book.author)
                putExtra("BOOK_ISBN", book.isbn)
                putExtra("BOOK_PUBLISHER", book.publisher)
                putExtra("BOOK_PUBLISHED_DATE", book.publishedDate)
                putExtra("BOOK_GENRE", book.genre)
                putExtra("BOOK_LANGUAGE", book.language)
                putExtra("BOOK_DESCRIPTION", book.description)
                putExtra("BOOK_COVER_URL", book.cover_image?.medium)
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "Book data not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmation() {
        currentBook?.let { book ->
            AlertDialog.Builder(this)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete \"${book.title}\"?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    deleteBook()
                }
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_delete)
                .show()
        }
    }

    private fun deleteBook() {
        if (bookId.isNullOrEmpty()) return

        showLoading(true)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        val authHeader = accessToken?.let { "Bearer $it" }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.authService.deleteBook(bookId!!, authHeader)

                withContext(Dispatchers.Main) {
                    showLoading(false)

                    when {
                        response.isSuccessful -> {
                            Toast.makeText(
                                this@detailactivity,
                                "Book deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Set result to indicate book was deleted
                            setResult(RESULT_OK, Intent().apply {
                                putExtra("BOOK_DELETED", true)
                                putExtra("DELETED_BOOK_ID", bookId)
                            })
                            finish()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(
                                this@detailactivity,
                                "Book not found",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        response.code() == 403 -> {
                            Toast.makeText(
                                this@detailactivity,
                                "Permission denied. You can only delete your own books.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            val errorMessage = try {
                                JSONObject(response.errorBody()?.string()).optString("message", "Failed to delete book")
                            } catch (e: Exception) {
                                "Failed to delete book"
                            }
                            Toast.makeText(this@detailactivity, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@detailactivity,
                        "Network error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun handleError(errorBody: String?) {
        val errorMessage = try {
            JSONObject(errorBody).optString("message", "Failed to load details")
        } catch (e: Exception) {
            "Failed to load details"
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
        val visibility = if (show) View.GONE else View.VISIBLE

        listOf(
            editButton,
            deleteButton,
            detailCoverImageView,
            detailTitleTextView,
            detailAuthorTextView,
            findViewById<View>(R.id.detailsTable),
            findViewById<View>(R.id.descriptionLabel),
            findViewById<View>(R.id.descriptionScrollView),
            closeButton
        ).forEach { it.visibility = visibility }
    }
}