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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.analysis.MetricReviewLevel
import com.example.clocktestdigital.analysis.reviewPauseCount
import com.example.clocktestdigital.analysis.reviewPressure
import com.example.clocktestdigital.analysis.reviewSpeed
import com.example.clocktestdigital.analysis.reviewStrokeCount
import com.example.clocktestdigital.data.local.TestSessionEntity

@Composable
fun SessionReviewMetricsCard(
    session: TestSessionEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen rápido de métricas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Principales valores registrados en la sesión.",
                color = Color(0xFF6B7280),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SessionReviewMetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Trazos",
                    value = session.strokeCount.toString(),
                    detail = "realizados",
                    level = reviewStrokeCount(session.strokeCount)
                )

                SessionReviewMetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Presión",
                    value = formatFloat(session.averagePressure, 2),
                    detail = "media relativa",
                    level = reviewPressure(session.averagePressure)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SessionReviewMetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Velocidad",
                    value = "${formatFloat(session.averageSpeedMmPerSec, 1)} mm/s",
                    detail = "media",
                    level = reviewSpeed(session.averageSpeedMmPerSec)
                )

                SessionReviewMetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Pausas",
                    value = session.pauseCount.toString(),
                    detail = "detectadas",
                    level = reviewPauseCount(session.pauseCount)
                )
            }
        }
    }
}

@Composable
private fun SessionReviewMetricBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    detail: String,
    level: MetricReviewLevel
) {
    Column(
        modifier = modifier
            .background(
                color = level.backgroundColor(),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )

            Text(
                text = level.labelText(),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = level.textColor(),
                modifier = Modifier
                    .background(
                        color = level.badgeColor(),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = detail,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

private fun MetricReviewLevel.labelText(): String {
    return when (this) {
        MetricReviewLevel.LOW -> "Sin destacar"
        MetricReviewLevel.MODERATE -> "Revisar"
        MetricReviewLevel.HIGH -> "Prioritario"
    }
}

private fun MetricReviewLevel.backgroundColor(): Color {
    return when (this) {
        MetricReviewLevel.LOW -> Color(0xFFF0FDF4)
        MetricReviewLevel.MODERATE -> Color(0xFFFFF7ED)
        MetricReviewLevel.HIGH -> Color(0xFFFEF2F2)
    }
}

private fun MetricReviewLevel.badgeColor(): Color {
    return when (this) {
        MetricReviewLevel.LOW -> Color(0xFFDCFCE7)
        MetricReviewLevel.MODERATE -> Color(0xFFFFEDD5)
        MetricReviewLevel.HIGH -> Color(0xFFFEE2E2)
    }
}

private fun MetricReviewLevel.textColor(): Color {
    return when (this) {
        MetricReviewLevel.LOW -> Color(0xFF166534)
        MetricReviewLevel.MODERATE -> Color(0xFFB45309)
        MetricReviewLevel.HIGH -> Color(0xFFB91C1C)
    }
}