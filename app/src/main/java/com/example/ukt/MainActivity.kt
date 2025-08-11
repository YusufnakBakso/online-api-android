package com.example.ukt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ukt.adapter.BookAdapter
import com.example.ukt.api.ApiClient
import com.example.ukt.model.Book
import com.example.ukt.request.LogoutRequest
import com.example.ukt.request.RefreshTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var logoutButton: Button
    private lateinit var addBookButton: Button
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var bookAdapter: BookAdapter


    private val handler = Handler(Looper.getMainLooper())
    private val checkTokenInterval: Long = TimeUnit.MINUTES.toMillis(5)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText)
        logoutButton = findViewById(R.id.logoutButton)
        addBookButton = findViewById(R.id.addBookButton)
        booksRecyclerView = findViewById(R.id.booksRecyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Get user information from SharedPreferences
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val email = prefs.getString("email", "")

        if (!email.isNullOrEmpty()) {
            welcomeText.text = "Welcome, $email!"
            welcomeText.visibility = View.VISIBLE
        }

        // Set up RecyclerView
        setupRecyclerView()

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            fetchBooks()
        }

        // Set up logout button
        logoutButton.setOnClickListener {
            logout()
        }

        // Set up add book button
        addBookButton.setOnClickListener {
            val intent = Intent(this@MainActivity, addBook::class.java)
            startActivity(intent)
        }

        // Fetch books initially
        fetchBooks()

        // Start token checker
        startTokenChecker()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(emptyList()) { book ->
            // Navigate to book detail screen
            val intent = Intent(this@MainActivity, detailactivity::class.java)
            intent.putExtra("BOOK_ID", book.id)
            startActivity(intent)
        }

        booksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = bookAdapter
        }
    }

    private fun fetchBooks() {
        swipeRefreshLayout.isRefreshing = true

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)

        // Add Bearer token format if accessToken is not null
        val authHeader = if (accessToken != null) "Bearer $accessToken" else null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.authService.getBooks(page = 1, limit = 100, token = authHeader)

                withContext(Dispatchers.Main) {
                    swipeRefreshLayout.isRefreshing = false

                    if (response.isSuccessful && response.body() != null) {
                        val bookResponse = response.body()!!
                        val books = bookResponse.data.books
                        bookAdapter.updateBooks(books)

                        // Show toast message if successful
                        Toast.makeText(
                            this@MainActivity,
                            "Loaded ${books.size} books",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (!errorBody.isNullOrEmpty()) {
                            try {
                                JSONObject(errorBody).optString("message", "Failed to load books")
                            } catch (e: Exception) {
                                "Failed to load books"
                            }
                        } else {
                            "Failed to load books"
                        }
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun logout() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val refreshToken = prefs.getString("refreshToken", "") ?: ""
        val accessToken = prefs.getString("accessToken", "") ?: ""

        if (refreshToken.isEmpty()) {
            Toast.makeText(this@MainActivity, "Token is required for logout", Toast.LENGTH_SHORT).show()
            clearUserDataAndRedirect()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Use proper authorization header with Bearer token
                val authHeader = "Bearer $accessToken"
                val response = ApiClient.authService.logout(LogoutRequest(refreshToken), authHeader)

                runOnUiThread {
                    if (response.isSuccessful && response.body() != null) {
                        val message = response.body()?.message ?: "Logout successful"
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        clearUserDataAndRedirect()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (!errorBody.isNullOrEmpty()) {
                            try {
                                JSONObject(errorBody).optString("error", response.message())
                            } catch (e: Exception) {
                                response.message()
                            }
                        } else {
                            response.message()
                        }
                        Toast.makeText(this@MainActivity, "Logout failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        // Even if logout fails on the server, clear the local data and redirect
                        clearUserDataAndRedirect()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error during logout: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    // Even if an exception occurs, clear the local data and redirect
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
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
                        Toast.makeText(this@MainActivity, "Token refresh failed", Toast.LENGTH_SHORT).show()
                        clearUserDataAndRedirect()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Token refresh error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    clearUserDataAndRedirect()
                }
            }
        }
    }
}