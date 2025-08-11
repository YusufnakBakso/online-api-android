package com.example.ukt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ukt.api.ApiClient
import com.example.ukt.request.RefreshTokenRequest
import com.example.ukt.request.AddBookRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class addBook : AppCompatActivity() {
    private lateinit var addTextView: TextView
    private lateinit var isbnEditText: EditText
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var publisherEditText: EditText
    private lateinit var publishedDateEditText: EditText
    private lateinit var genreEditText: EditText
    private lateinit var languageEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var addButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private val checkTokenInterval: Long = TimeUnit.MINUTES.toMillis(5)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        // Initialize views
        addTextView = findViewById(R.id.add)
        isbnEditText = findViewById(R.id.isbnEditText)
        titleEditText = findViewById(R.id.titleEditText)
        authorEditText = findViewById(R.id.authorEditText)
        publisherEditText = findViewById(R.id.publisherEditText)
        publishedDateEditText = findViewById(R.id.publishedDateEditText)
        genreEditText = findViewById(R.id.genreEditText)
        languageEditText = findViewById(R.id.languageEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        cancelButton = findViewById(R.id.cancelButton)
        addButton = findViewById(R.id.addButton)

        // Set up button listeners
        cancelButton.setOnClickListener {
            finish() // Close this activity and return to MainActivity
        }

        addButton.setOnClickListener {
            addNewBook()
        }

        // Start token checker
        startTokenChecker()
    }

    private fun addNewBook() {
        // Get input values
        val isbn = isbnEditText.text.toString().trim()
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val publisher = publisherEditText.text.toString().trim()
        val publishedDate = publishedDateEditText.text.toString().trim()
        val genre = genreEditText.text.toString().trim()
        val language = languageEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        // Validate required fields
        when {
            isbn.isEmpty() -> {
                isbnEditText.error = "ISBN is required"
                isbnEditText.requestFocus()
                return
            }
            title.isEmpty() -> {
                titleEditText.error = "Title is required"
                titleEditText.requestFocus()
                return
            }
            author.isEmpty() -> {
                authorEditText.error = "Author is required"
                authorEditText.requestFocus()
                return
            }
            publisher.isEmpty() -> {
                publisherEditText.error = "Publisher is required"
                publisherEditText.requestFocus()
                return
            }
            publishedDate.isEmpty() -> {
                publishedDateEditText.error = "Published date is required"
                publishedDateEditText.requestFocus()
                return
            }
            genre.isEmpty() -> {
                genreEditText.error = "Genre is required"
                genreEditText.requestFocus()
                return
            }
            language.isEmpty() -> {
                languageEditText.error = "Language is required"
                languageEditText.requestFocus()
                return
            }
            description.isEmpty() -> {
                descriptionEditText.error = "Description is required"
                descriptionEditText.requestFocus()
                return
            }
        }

        // Disable add button to prevent duplicate submissions
        addButton.isEnabled = false
        addButton.text = "Adding..."

        // Get access token from SharedPreferences
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)

        if (accessToken == null) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_SHORT).show()
            clearUserDataAndRedirect()
            return
        }

        // Add Bearer token format
        val authHeader = "Bearer $accessToken"

        // Create book request object
        val bookRequest = AddBookRequest(
            isbn = isbn,
            title = title,
            author = author,
            publisher = publisher,
            published_date = publishedDate,
            genre = genre,
            language = language,
            description = description
        )

        // Make API call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.authService.addBook(bookRequest, authHeader)

                withContext(Dispatchers.Main) {
                    // Re-enable add button
                    addButton.isEnabled = true
                    addButton.text = "Add Book"

                    if (response.isSuccessful && response.body() != null) {
                        val bookResponse = response.body()!!
                        Toast.makeText(
                            this@addBook,
                            bookResponse.message ?: "Book added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Clear all input fields
                        clearInputFields()

                        // Go back to MainActivity with a flag to refresh the book list
                        val intent = Intent(this@addBook, MainActivity::class.java)
                        intent.putExtra("refresh_books", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (!errorBody.isNullOrEmpty()) {
                            try {
                                val errorObj = JSONObject(errorBody)
                                errorObj.optString("message", "Failed to add book")
                            } catch (e: Exception) {
                                "Failed to add book"
                            }
                        } else {
                            "Failed to add book"
                        }
                        Toast.makeText(this@addBook, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Re-enable add button
                    addButton.isEnabled = true
                    addButton.text = "Add Book"

                    Toast.makeText(
                        this@addBook,
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun clearInputFields() {
        isbnEditText.setText("")
        titleEditText.setText("")
        authorEditText.setText("")
        publisherEditText.setText("")
        publishedDateEditText.setText("")
        genreEditText.setText("")
        languageEditText.setText("")
        descriptionEditText.setText("")
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
                val obj = JSONObject(payload)
                val expMs = obj.getLong("exp") * 1000
                System.currentTimeMillis() > expMs
            } else true
        } catch (e: Exception) {
            true
        }
    }

    private fun startTokenChecker() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkAndRefreshToken()
                handler.postDelayed(this, checkTokenInterval)
            }
        }, checkTokenInterval)
    }

    private fun checkAndRefreshToken() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        val refreshToken = prefs.getString("refreshToken", null)

        if (accessToken != null && isTokenExpired(accessToken)) {
            if (refreshToken != null && !isTokenExpired(refreshToken)) {
                refreshAccessToken(refreshToken)
            } else {
                clearUserDataAndRedirect()
            }
        }
    }

    private fun refreshAccessToken(refreshToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.authService.refreshToken(RefreshTokenRequest(refreshToken))

                if (response.isSuccessful && response.body() != null) {
                    val newAccessToken = response.body()!!.accessToken

                    // Update token in SharedPreferences
                    val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    prefs.edit().putString("accessToken", newAccessToken).apply()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@addBook, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                        clearUserDataAndRedirect()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@addBook, "Session error. Please login again.", Toast.LENGTH_SHORT).show()
                    clearUserDataAndRedirect()
                }
            }
        }
    }

    private fun clearUserDataAndRedirect() {
        // Clear all user data
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Stop token checker
        handler.removeCallbacksAndMessages(null)

        // Navigate to Login
        val intent = Intent(this@addBook, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up handler to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}