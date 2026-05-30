package com.example.clocktestdigital.ui.sessions.pdf

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

internal fun formatMilliseconds(milliseconds: Long?): String {
    if (milliseconds == null) return "No registrado"

    return String.format(Locale.getDefault(), "%.1f s", milliseconds / 1000f)
}

internal fun formatValidity(isValidTest: Boolean?): String {
    return when (isValidTest) {
        true -> "Válida"
        false -> "No válida"
        null -> "Sin valorar"
    }
}