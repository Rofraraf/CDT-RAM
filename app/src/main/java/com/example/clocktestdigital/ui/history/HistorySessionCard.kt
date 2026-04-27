package com.example.clocktestdigital.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.data.local.TestSessionEntity

@Composable
fun HistorySessionCard(
    session: TestSessionEntity
) {
    val dateText = android.text.format.DateFormat
        .format("dd/MM/yyyy HH:mm", session.testDateTime)
        .toString()

    val minutes = session.executionTimeSeconds / 60
    val seconds = session.executionTimeSeconds % 60
    val executionTimeText = String.format("%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = dateText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Tiempo: $executionTimeText",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "${session.strokeCount} trazos · " +
                        String.format("%.2f rel.", session.averagePressure) +
                        " · " +
                        String.format("%.1f mm/s", session.averageSpeedMmPerSec),
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "${session.pauseCount} pausas · " +
                        String.format("%.1f s", session.totalPauseTimeMs / 1000f),
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = if (session.isReviewed) "Revisada" else "Pendiente de revisión",
                color = if (session.isReviewed) Color(0xFF2E7D32) else Color(0xFFB45309),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}