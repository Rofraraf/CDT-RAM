package com.example.clocktestdigital.ui.patients

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun EditPatientScreen(
    patientCode: String,
    onPatientUpdated: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var clinicalRecordId by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var clinicalNotes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(patientCode) {
        val patient = database.patientDao().getPatientByCode(patientCode)

        if (patient != null) {
            clinicalRecordId = patient.clinicalRecordId.orEmpty()
            displayName = patient.displayName.orEmpty()
            birthYear = patient.birthYear?.toString().orEmpty()
            sex = patient.sex.orEmpty()
            clinicalNotes = patient.clinicalNotes.orEmpty()
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        AppHeader()

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Editar paciente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = patientCode,
            fontSize = 15.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (isLoading) {
            Text(
                text = "Cargando datos del paciente...",
                color = Color(0xFF6B7280)
            )
        } else {
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
                    OutlinedTextField(
                        value = patientCode,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Código del paciente") },
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = clinicalRecordId,
                        onValueChange = { clinicalRecordId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ID historia clínica") },
                        placeholder = { Text("Ej. HC-000145") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Alias o nombre visible") },
                        placeholder = { Text("Ej. Paciente 002") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = birthYear,
                        onValueChange = {
                            birthYear = it.filter { char -> char.isDigit() }.take(4)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Año de nacimiento") },
                        placeholder = { Text("Ej. 1953") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = sex,
                        onValueChange = { sex = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Sexo") },
                        placeholder = { Text("Ej. Masculino / Femenino") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = clinicalNotes,
                        onValueChange = { clinicalNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Notas clínicas generales") },
                        placeholder = { Text("Observaciones generales del paciente") },
                        shape = RoundedCornerShape(16.dp)
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val now = System.currentTimeMillis()

                                database.patientDao().updatePatient(
                                    patientCode = patientCode,
                                    clinicalRecordId = clinicalRecordId.trim().ifEmpty { null },
                                    displayName = displayName.trim().ifEmpty { null },
                                    birthYear = birthYear.toIntOrNull(),
                                    sex = sex.trim().ifEmpty { null },
                                    clinicalNotes = clinicalNotes.trim().ifEmpty { null },
                                    updatedAt = now
                                )

                                Toast.makeText(
                                    context,
                                    "Paciente actualizado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onPatientUpdated()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }
                }
            }
        }
    }
}