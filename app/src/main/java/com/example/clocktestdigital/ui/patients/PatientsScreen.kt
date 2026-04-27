package com.example.clocktestdigital.ui.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.PatientEntity

@Composable
fun PatientsScreen(
    onBackToTest: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    var patients by remember { mutableStateOf<List<PatientEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        patients = database.patientDao().getAllPatients()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Pacientes",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Pacientes registrados en el dispositivo",
            fontSize = 18.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onBackToTest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al test")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (patients.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Todavía no hay pacientes registrados.",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF6B7280),
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                patients.forEach { patient ->
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
                                text = patient.patientCode,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = patient.displayName ?: "Paciente sin datos completados",
                                color = Color(0xFF6B7280),
                                fontSize = 15.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Historia clínica: ${patient.clinicalRecordId ?: "sin asignar"}",
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Año nacimiento: ${patient.birthYear ?: "sin completar"}",
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Sexo: ${patient.sex ?: "sin completar"}",
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}