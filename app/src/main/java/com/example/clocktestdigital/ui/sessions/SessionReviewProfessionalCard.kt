package com.example.clocktestdigital.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SessionReviewProfessionalCard(
    professionalNotes: String,
    onProfessionalNotesChange: (String) -> Unit,
    validityState: String?,
    onValidityStateChange: (String?) -> Unit,
    onSaveReview: () -> Unit,
    onGeneratePdf: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Valoración profesional",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Validez de la prueba",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = validityState == "VALID",
                    onClick = { onValidityStateChange("VALID") },
                    label = { Text("Válida") }
                )

                FilterChip(
                    selected = validityState == "INVALID",
                    onClick = { onValidityStateChange("INVALID") },
                    label = { Text("No válida") }
                )

                FilterChip(
                    selected = validityState == null,
                    onClick = { onValidityStateChange(null) },
                    label = { Text("Sin valorar") }
                )
            }

            OutlinedTextField(
                value = professionalNotes,
                onValueChange = onProfessionalNotesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Observaciones del profesional") },
                placeholder = {
                    Text("Añadir comentarios sobre ejecución, dudas, interrupciones o comportamiento observado.")
                },
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = onSaveReview,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar revisión")
            }

            OutlinedButton(
                onClick = onGeneratePdf,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generar informe PDF")
            }
        }
    }
}