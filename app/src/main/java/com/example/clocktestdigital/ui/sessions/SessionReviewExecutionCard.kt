package com.example.clocktestdigital.ui.sessions

import androidx.compose.foundation.background
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
import com.example.clocktestdigital.analysis.SessionExecutionMetrics

@Composable
fun SessionReviewExecutionCard(
    metrics: SessionExecutionMetrics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Datos técnicos registrados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Registro base de eventos y comportamiento hover.",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                lineHeight = 16.sp
            )

            TechnicalDataBlock(
                title = "Eventos registrados",
                items = listOf(
                    "Total" to metrics.totalEventCount.toString(),
                    "Dibujo" to metrics.drawingEventCount.toString(),
                    "Hover" to metrics.hoverEventCount.toString()
                )
            )

            TechnicalDataBlock(
                title = "Análisis hover",
                items = listOf(
                    "Proporción hover/sesión" to "${formatFloat(metrics.hoverPercentageOfSession, 1)} %",
                    "Tiempo total hover" to formatMillisecondsAsSeconds(metrics.totalHoverTimeMs),
                    "Hover antes del primer trazo" to formatMillisecondsAsSeconds(metrics.hoverBeforeFirstDrawMs),
                    "Segmentos hover" to metrics.hoverSegmentCount.toString(),
                    "Duración media hover" to formatMillisecondsAsSeconds(metrics.averageHoverSegmentTimeMs)
                )
            )

            HoverObservationBlock(
                observation = buildSessionReviewHoverObservation(metrics)
            )
        }
    }
}

@Composable
private fun TechnicalDataBlock(
    title: String,
    items: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        items.forEach { item ->
            TechnicalDataRow(
                label = item.first,
                value = item.second
            )
        }
    }
}

@Composable
private fun TechnicalDataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun HoverObservationBlock(
    observation: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Observación técnica sobre hover",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Text(
            text = observation,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            lineHeight = 16.sp
        )
    }
}