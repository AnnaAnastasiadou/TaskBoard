package com.example.taskboard.presentation.common

import android.view.View
import androidx.core.content.ContextCompat
import com.example.taskboard.R
import com.google.android.material.snackbar.Snackbar

fun showErrorMessage(rootView: View, message: String, isError: Boolean = true) {
    val backgroundColor = if (isError) android.R.color.holo_red_dark else android.R.color.darker_gray
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).apply {
        setBackgroundTint(ContextCompat.getColor(rootView.context, backgroundColor))
        setTextColor(ContextCompat.getColor(rootView.context, android.R.color.white))
        show()
    }
}