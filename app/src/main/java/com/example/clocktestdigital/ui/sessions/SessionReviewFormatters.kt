package com.example.clocktestdigital.ui.sessions

import android.text.format.DateFormat
import com.example.clocktestdigital.data.local.TestSessionEntity
import java.util.Locale

fun formatSessionDate(
    session: TestSessionEntity
): String {
    return DateFormat
        .format("dd/MM/yyyy HH:mm", session.testDateTime)
        .toString()
}

fun formatExecutionTime(
    seconds: Int
): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format(
        Locale.getDefault(),
        "%02d:%02d",
        minutes,
        remainingSeconds
    )
}

fun formatMillisecondsAsSeconds(
    milliseconds: Long?
): String {
    if (milliseconds == null) return "No registrado"

    return String.format(
        Locale.getDefault(),
        "%.1f s",
        milliseconds / 1000f
    )
}

fun formatFloat(
    value: Float,
    decimals: Int = 1
): String {
    return "%.${decimals}f".format(Locale.getDefault(), value)
}

fun formatValidityState(
    isValidTest: Boolean?
): String {
    return when (isValidTest) {
        true -> "Válida"
        false -> "No válida"
        null -> "Sin valorar"
    }
}