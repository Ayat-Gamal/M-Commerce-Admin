package com.example.m_commerce_admin.core.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatIsoDate(dateString: String): String {

    val instant = Instant.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
