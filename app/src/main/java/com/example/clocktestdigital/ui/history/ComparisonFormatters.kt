package com.example.clocktestdigital.ui.history

import android.text.format.DateFormat
import com.example.clocktestdigital.data.local.TestSessionEntity
import java.util.Locale

fun formatSessionLabel(session: TestSessionEntity): String {
    val date = DateFormat
        .format("dd/MM HH:mm", session.testDateTime)
        .toString()

    return date
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

fun formatMilliseconds(milliseconds: Long): String {
    return String.format(Locale.getDefault(), "%.1f s", milliseconds / 1000f)
}