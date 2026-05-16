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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ánalisis técnico de la sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Lectura orientativa de las métricas registradas. No constituye diagnóstico automático ni sustituye la valoración profesional.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 19.sp
            )

            interpretations.forEach { interpretation ->
                MetricInterpretationItem(
                    interpretation = interpretation
                )
            }
        }
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
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Text(
                    text = interpretation.valueText,
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )
            }

            Text(
                text = interpretation.levelText,
                modifier = Modifier
                    .background(
                        color = interpretation.level.backgroundColor(),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = interpretation.level.textColor()
            )
        }

        Text(
            text = interpretation.technicalReading,
            fontSize = 13.sp,
            color = Color(0xFF475569),
            lineHeight = 18.sp
        )


    }
}

private fun MetricReviewLevel.backgroundColor(): Color {
    return when (this) {
        MetricReviewLevel.LOW -> Color(0xFFE8F5E9)
        MetricReviewLevel.MODERATE -> Color(0xFFFFF7ED)
        MetricReviewLevel.HIGH -> Color(0xFFFEE2E2)
    }
}

private fun MetricReviewLevel.textColor(): Color {
    return when (this) {
        MetricReviewLevel.LOW -> Color(0xFF2E7D32)
        MetricReviewLevel.MODERATE -> Color(0xFFB45309)
        MetricReviewLevel.HIGH -> Color(0xFFB91C1C)
    }
}