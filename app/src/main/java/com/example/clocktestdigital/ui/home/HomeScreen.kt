package com.example.clocktestdigital.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.clocktestdigital.ui.components.AppHeader

@Composable
fun HomeScreen(
    selectedPatientCode: String?,
    onGoToPatients: () -> Unit,
    onGoToNewPatient: () -> Unit,
    onGoToTest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        AppHeader()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Inicio",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Gestión de pacientes y pruebas del Test del Reloj",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (selectedPatientCode != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Paciente activo",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = selectedPatientCode,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        HomeActionCard(
            title = "Pacientes",
            subtitle = "Consultar pacientes registrados e historial de sesiones",
            icon = "👥",
            onClick = onGoToPatients
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionCard(
            title = "Nuevo paciente",
            subtitle = "Registrar un nuevo paciente con código automático",
            icon = "➕",
            onClick = onGoToNewPatient
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionCard(
            title = "Realizar test",
            subtitle = if (selectedPatientCode != null) {
                "Iniciar prueba para $selectedPatientCode"
            } else {
                "Seleccionar paciente antes de iniciar la prueba"
            },
            icon = "✏️",
            onClick = onGoToTest
        )
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = icon,
                fontSize = 26.sp
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}