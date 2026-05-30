package com.example.clocktestdigital.ui.sessions

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
fun SessionReviewDataCard(
    session: TestSessionEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Datos de la sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Paciente: ${session.patientCode}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Fecha de prueba: ${formatSessionDate(session)}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = if (session.isReviewed) {
                    "Estado de revisión: revisada"
                } else {
                    "Estado de revisión: pendiente"
                },
                color = if (session.isReviewed) {
                    Color(0xFF2E7D32)
                } else {
                    Color(0xFFB45309)
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Validez de la prueba: ${formatValidityState(session.isValidTest)}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}