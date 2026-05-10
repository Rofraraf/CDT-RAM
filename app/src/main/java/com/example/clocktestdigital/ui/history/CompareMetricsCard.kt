package com.example.clocktestdigital.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.clocktestdigital.ui.sessions.SessionExecutionMetrics
import java.util.Locale

@Composable
fun CompareMetricsCard(
    sessionA: TestSessionEntity,
    sessionB: TestSessionEntity,
    metricsA: SessionExecutionMetrics,
    metricsB: SessionExecutionMetrics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Métricas comparadas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ComparisonRow(
                label = "",
                valueA = "Sesión A",
                valueB = "Sesión B",
                isHeader = true
            )

            ComparisonRow(
                label = "Duración",
                valueA = formatTime(sessionA.executionTimeSeconds),
                valueB = formatTime(sessionB.executionTimeSeconds)
            )

            ComparisonRow(
                label = "Trazos",
                valueA = sessionA.strokeCount.toString(),
                valueB = sessionB.strokeCount.toString()
            )

            ComparisonRow(
                label = "Presión media",
                valueA = String.format(Locale.getDefault(), "%.2f", sessionA.averagePressure),
                valueB = String.format(Locale.getDefault(), "%.2f", sessionB.averagePressure)
            )

            ComparisonRow(
                label = "Velocidad media",
                valueA = String.format(Locale.getDefault(), "%.1f mm/s", sessionA.averageSpeedMmPerSec),
                valueB = String.format(Locale.getDefault(), "%.1f mm/s", sessionB.averageSpeedMmPerSec)
            )

            ComparisonRow(
                label = "Pausas",
                valueA = sessionA.pauseCount.toString(),
                valueB = sessionB.pauseCount.toString()
            )

            ComparisonRow(
                label = "Tiempo pausas",
                valueA = formatMilliseconds(sessionA.totalPauseTimeMs),
                valueB = formatMilliseconds(sessionB.totalPauseTimeMs)
            )

            ComparisonRow(
                label = "Eventos dibujo",
                valueA = metricsA.drawingEventCount.toString(),
                valueB = metricsB.drawingEventCount.toString()
            )

            ComparisonRow(
                label = "Eventos hover",
                valueA = metricsA.hoverEventCount.toString(),
                valueB = metricsB.hoverEventCount.toString()
            )

            ComparisonRow(
                label = "Tiempo hover",
                valueA = formatMilliseconds(metricsA.totalHoverTimeMs),
                valueB = formatMilliseconds(metricsB.totalHoverTimeMs)
            )

            ComparisonRow(
                label = "Proporción hover",
                valueA = String.format(Locale.getDefault(), "%.1f %%", metricsA.hoverPercentageOfSession),
                valueB = String.format(Locale.getDefault(), "%.1f %%", metricsB.hoverPercentageOfSession)
            )
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    valueA: String,
    valueB: String,
    isHeader: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1.25f),
            fontSize = if (isHeader) 12.sp else 13.sp,
            fontWeight = if (isHeader) FontWeight.SemiBold else FontWeight.Normal,
            color = Color(0xFF64748B)
        )

        Text(
            text = valueA,
            modifier = Modifier.weight(1f),
            fontSize = if (isHeader) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2563EB)
        )

        Text(
            text = valueB,
            modifier = Modifier.weight(1f),
            fontSize = if (isHeader) 12.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFDC2626)
        )
    }
}