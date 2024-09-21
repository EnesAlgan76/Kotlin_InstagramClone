package com.example.kotlininstagramapp.utils

import java.util.concurrent.TimeUnit

fun getTimeAgo(millis: Long): String {
    val currentTime = System.currentTimeMillis()
    var diffInMillis = currentTime - millis
    if (diffInMillis < 0) diffInMillis = 0

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    val years = days / 365
    val months = days / 30

    return when {
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        years >= 1 -> if (years == 1L) "1 year ago" else "$years years ago"
        months >= 1 -> if (months == 1L) "1 month ago" else "$months months ago"
        else -> "$days days ago"
    }
}