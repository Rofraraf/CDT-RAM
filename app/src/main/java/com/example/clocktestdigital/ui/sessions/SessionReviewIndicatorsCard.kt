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
import com.example.clocktestdigital.analysis.MetricInterpretation
import com.example.clocktestdigital.analysis.MetricReviewLevel

@Composable
fun SessionReviewIndicatorsCard(
    interpretations: List<MetricInterpretation>
) {
    val durationInterpretation = interpretations
        .firstOrNull { interpretation ->
            interpretation.title == "Duración total"
        }

    val highlightedInterpretations = interpretations
        .filter { interpretation ->
            interpretation.title in additionalReviewTitles &&
                    interpretation.level != MetricReviewLevel.LOW
        }

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
                text = "Análisis técnico de la sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Duración total y aspectos adicionales que pueden requerir revisión profesional.",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                lineHeight = 16.sp
            )

            durationInterpretation?.let { interpretation ->
                DurationContextBlock(
                    interpretation = interpretation
                )
            }

            highlightedInterpretations.forEach { interpretation ->
                MetricInterpretationItem(
                    interpretation = interpretation
                )
            }
        }
    }
}

@Composable
private fun DurationContextBlock(
    interpretation: MetricInterpretation
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Duración total",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Text(
                    text = interpretation.valueText,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
            }

            Text(
                text = interpretation.levelText,
                modifier = Modifier
                    .background(
                        color = interpretation.level.badgeColor(),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = interpretation.level.textColor()
            )
        }

        Text(
            text = interpretation.technicalReading,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun MetricInterpretationItem(
    interpretation: MetricInterpretation
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = interpretation.level.backgroundColor(),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = interpretation.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Text(
                    text = interpretation.valueText,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
            }

            Text(
                text = interpretation.levelText,
                modifier = Modifier
                    .background(
                        color = interpretation.level.badgeColor(),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = interpretation.level.textColor()
            )
        }

        Text(
            text = interpretation.technicalReading,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            lineHeight = 16.sp
        )
    }
}

private val additionalReviewTitles = setOf(
    "Número de trazos",
    "Número de pausas",
    "Tiempo total de pausas",
    "Velocidad media",
    "Presión media relativa"
)

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