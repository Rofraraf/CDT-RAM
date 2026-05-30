package com.example.clocktestdigital.ui.history.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.analysis.SessionExecutionMetrics
import java.util.Locale

private data class ComparisonChartItem(
    val label: String,
    val valueA: Float,
    val valueB: Float,
    val suffix: String
)

@Composable
fun CompareChartCard(
    sessionA: TestSessionEntity,
    sessionB: TestSessionEntity,
    metricsA: SessionExecutionMetrics,
    metricsB: SessionExecutionMetrics
) {
    val items = listOf(
        ComparisonChartItem(
            label = "Duración",
            valueA = sessionA.executionTimeSeconds.toFloat(),
            valueB = sessionB.executionTimeSeconds.toFloat(),
            suffix = "s"
        ),
        ComparisonChartItem(
            label = "Trazos",
            valueA = sessionA.strokeCount.toFloat(),
            valueB = sessionB.strokeCount.toFloat(),
            suffix = ""
        ),
        ComparisonChartItem(
            label = "Velocidad",
            valueA = sessionA.averageSpeedMmPerSec,
            valueB = sessionB.averageSpeedMmPerSec,
            suffix = "mm/s"
        ),
        ComparisonChartItem(
            label = "Pausas",
            valueA = sessionA.pauseCount.toFloat(),
            valueB = sessionB.pauseCount.toFloat(),
            suffix = ""
        ),
        ComparisonChartItem(
            label = "Hover",
            valueA = metricsA.totalHoverTimeMs / 1000f,
            valueB = metricsB.totalHoverTimeMs / 1000f,
            suffix = "s"
        )
    )

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Companion.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.Companion.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen visual",
                fontSize = 18.sp,
                fontWeight = FontWeight.Companion.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Azul: Sesión A · Rojo: Sesión B",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            items.forEach { item ->
                ComparisonBarRow(item = item)
            }
        }
    }
}

@Composable
private fun ComparisonBarRow(
    item: ComparisonChartItem
) {
    val maxValue = maxOf(item.valueA, item.valueB, 0.01f)

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Companion.SemiBold,
                color = Color(0xFF334155)
            )

            Text(
                text = "${
                    formatChartValue(
                        item.valueA,
                        item.suffix
                    )
                } / ${formatChartValue(item.valueB, item.suffix)}",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        ComparisonSingleBar(
            label = "A",
            value = item.valueA,
            maxValue = maxValue,
            color = Color(0xFF2563EB)
        )

        ComparisonSingleBar(
            label = "B",
            value = item.valueB,
            maxValue = maxValue,
            color = Color(0xFFDC2626)
        )
    }
}

@Composable
private fun ComparisonSingleBar(
    label: String,
    value: Float,
    maxValue: Float,
    color: Color
) {
    val fraction = if (value <= 0f) {
        0f
    } else {
        (value / maxValue).coerceIn(0.05f, 1f)
    }

    Row(
        modifier = Modifier.Companion.fillMaxWidth(),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.Companion.width(18.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Companion.Bold,
            color = color
        )

        Box(
            modifier = Modifier.Companion
                .weight(1f)
                .height(8.dp)
                .background(
                    color = Color(0xFFE2E8F0),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                )
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier.Companion
                        .fillMaxWidth(fraction)
                        .height(8.dp)
                        .background(
                            color = color,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

private fun formatChartValue(
    value: Float,
    suffix: String
): String {
    return when (suffix) {
        "mm/s" -> String.Companion.format(Locale.getDefault(), "%.1f mm/s", value)
        "s" -> String.Companion.format(Locale.getDefault(), "%.1f s", value)
        else -> String.Companion.format(Locale.getDefault(), "%.0f", value)
    }
}