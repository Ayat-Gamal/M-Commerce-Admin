package com.example.m_commerce_admin.core.helpers
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatCreatedAt(createdAt: Int): String {
    return try {
        val formatter = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        val date = formatter.parse(createdAt.toString())
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        throw NumberFormatException()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatIsoDate(dateString: String): String {
    val instant = Instant.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
