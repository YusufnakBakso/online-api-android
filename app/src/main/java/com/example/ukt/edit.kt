package com.example.ukt

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ukt.api.ApiClient
import com.example.ukt.model.UpdateBookRequest
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class edit : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var saveButton: ImageButton
    private lateinit var editCoverImageView: ImageView
    private lateinit var editTitleEditText: TextInputEditText
    private lateinit var editAuthorEditText: TextInputEditText
    private lateinit var editIsbnEditText: TextInputEditText
    private lateinit var editPublisherEditText: TextInputEditText
    private lateinit var editPublishedDateEditText: TextInputEditText
    private lateinit var editGenreEditText: TextInputEditText
    private lateinit var editLanguageEditText: TextInputEditText
    private lateinit var editDescriptionEditText: TextInputEditText
    private lateinit var loadingView: View
    private lateinit var contentScrollView: View

    private var bookId: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        initViews()
        setupButtons()
        loadBookData()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        editCoverImageView = findViewById(R.id.editCoverImageView)
        editTitleEditText = findViewById(R.id.editTitleEditText)
        editAuthorEditText = findViewById(R.id.editAuthorEditText)
        editIsbnEditText = findViewById(R.id.editIsbnEditText)
        editPublisherEditText = findViewById(R.id.editPublisherEditText)
        editPublishedDateEditText = findViewById(R.id.editPublishedDateEditText)
        editGenreEditText = findViewById(R.id.editGenreEditText)
        editLanguageEditText = findViewById(R.id.editLanguageEditText)
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText)
        loadingView = findViewById(R.id.loadingView)
        contentScrollView = findViewById(R.id.contentScrollView)
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveBookChanges()
        }

        // Date picker for published date
        editPublishedDateEditText.setOnClickListener {
            showDatePicker()
        }
    }

    private fun loadBookData() {
        // Get book data from intent
        bookId = intent.getStringExtra("BOOK_ID")

        // Populate fields with existing data
        editTitleEditText.setText(intent.getStringExtra("BOOK_TITLE"))
        editAuthorEditText.setText(intent.getStringExtra("BOOK_AUTHOR"))
        editIsbnEditText.setText(intent.getStringExtra("BOOK_ISBN"))
        editPublisherEditText.setText(intent.getStringExtra("BOOK_PUBLISHER"))
        editPublishedDateEditText.setText(intent.getStringExtra("BOOK_PUBLISHED_DATE"))
        editGenreEditText.setText(intent.getStringExtra("BOOK_GENRE"))
        editLanguageEditText.setText(intent.getStringExtra("BOOK_LANGUAGE"))
        editDescriptionEditText.setText(intent.getStringExtra("BOOK_DESCRIPTION"))

        // Load cover image
        val coverUrl = intent.getStringExtra("BOOK_COVER_URL")
        if (!coverUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_book_placeholder)
                .into(editCoverImageView)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Parse current date if exists
        val currentDate = editPublishedDateEditText.text.toString()
        if (currentDate.isNotEmpty()) {
            try {
                val date = displayDateFormat.parse(currentDate)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val formattedDate = displayDateFormat.format(calendar.time)
                editPublishedDateEditText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveBookChanges() {
        if (!validateInput()) return

        if (bookId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid book ID", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        val authHeader = accessToken?.let { "Bearer $it" }

        // Convert display date to API format
        val publishedDate = editPublishedDateEditText.text.toString().let { dateStr ->
            if (dateStr.isNotEmpty()) {
                try {
                    val date = displayDateFormat.parse(dateStr)
                    if (date != null) dateFormat.format(date) else null
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        val updateRequest = UpdateBookRequest(
            title = editTitleEditText.text.toString().trim(),
            author = editAuthorEditText.text.toString().trim(),
            isbn = editIsbnEditText.text.toString().trim().ifEmpty { null },
            publisher = editPublisherEditText.text.toString().trim().ifEmpty { null },
            publishedDate = publishedDate,
            genre = editGenreEditText.text.toString().trim().ifEmpty { null },
            language = editLanguageEditText.text.toString().trim().ifEmpty { null },
            description = editDescriptionEditText.text.toString().trim().ifEmpty { null }
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.authService.updateBook(bookId!!, updateRequest, authHeader)

                withContext(Dispatchers.Main) {
                    showLoading(false)

                    when {
                        response.isSuccessful -> {
                            Toast.makeText(
                                this@edit,
                                "Book updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Set result to indicate book was updated
                            setResult(RESULT_OK)
                            finish()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(
                                this@edit,
                                "Book not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 403 -> {
                            Toast.makeText(
                                this@edit,
                                "Permission denied. You can only edit your own books.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        response.code() == 400 -> {
                            val errorMessage = try {
                                JSONObject(response.errorBody()?.string()).optString("message", "Invalid data")
                            } catch (e: Exception) {
                                "Invalid data"
                            }
                            Toast.makeText(this@edit, errorMessage, Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            val errorMessage = try {
                                JSONObject(response.errorBody()?.string()).optString("message", "Failed to update book")
                            } catch (e: Exception) {
                                "Failed to update book"
                            }
                            Toast.makeText(this@edit, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@edit,
                        "Network error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val title = editTitleEditText.text.toString().trim()
        val author = editAuthorEditText.text.toString().trim()

        return when {
            title.isEmpty() -> {
                editTitleEditText.error = "Title is required"
                editTitleEditText.requestFocus()
                false
            }
            author.isEmpty() -> {
                editAuthorEditText.error = "Author is required"
                editAuthorEditText.requestFocus()
                false
            }
            else -> {
                // Clear any existing errors
                editTitleEditText.error = null
                editAuthorEditText.error = null
                true
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
        contentScrollView.visibility = if (show) View.GONE else View.VISIBLE

        // Disable save button during loading
        saveButton.isEnabled = !show
        saveButton.alpha = if (show) 0.5f else 1.0f
    }
}