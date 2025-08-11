package com.example.ukt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ukt.R
import com.example.ukt.model.Book
import com.squareup.picasso.Picasso

class BookAdapter(
    private var books: List<Book> = emptyList(),
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    fun updateBooks(newBooks: List<Book>) {
        this.books = newBooks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener { onItemClick(book) }
    }

    override fun getItemCount(): Int = books.size

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.coverImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
        private val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)

        fun bind(book: Book) {
            titleTextView.text = book.title
            authorTextView.text = book.author
            genreTextView.text = book.genre

            // Load image with Picasso
            if (book.cover_image.medium.isNotEmpty()) {
                Picasso.get()
                    .load(book.cover_image.medium)
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(coverImageView)
            } else {
                coverImageView.setImageResource(R.drawable.book_placeholder)
            }
        }
    }
}