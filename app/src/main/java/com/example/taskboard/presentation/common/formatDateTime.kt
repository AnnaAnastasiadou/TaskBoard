package com.example.taskboard.presentation.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val date = Date(timestamp)
    return formatter.format(date)
}