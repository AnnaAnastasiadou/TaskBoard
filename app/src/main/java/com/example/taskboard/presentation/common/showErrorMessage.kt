package com.example.taskboard.presentation.common

import android.view.View
import androidx.core.content.ContextCompat
import com.example.taskboard.R
import com.google.android.material.snackbar.Snackbar

fun showErrorMessage(rootView: View, message: String) {
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).apply {
        setBackgroundTint(ContextCompat.getColor(rootView.context, android.R.color.holo_red_dark))
        setTextColor(ContextCompat.getColor(rootView.context, android.R.color.white))
        show()
    }
}