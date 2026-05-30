package com.example.clocktestdigital.ui.patients

import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.PatientEntity
import com.example.clocktestdigital.ui.components.AppHeader
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun PatientsScreen(
    onOpenHistory: (String) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    var patients by remember { mutableStateOf<List<PatientEntity>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var showArchivedDialog by remember { mutableStateOf(false) }
    var archivedPatients by remember { mutableStateOf<List<PatientEntity>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                val csvContent = buildPatientsCsv(patients)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                }

                Toast.makeText(
                    context,
                    "CSV exportado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (exception: Exception) {
                Toast.makeText(
                    context,
                    "No se pudo exportar el CSV",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        patients = database.patientDao().getAllPatients()
    }

    val filteredPatients = patients.filter { patient ->
        val query = searchQuery.trim().lowercase()

        query.isEmpty() ||
                patient.patientCode.lowercase().contains(query) ||
                (patient.clinicalRecordId?.lowercase()?.contains(query) == true) ||
                (patient.displayName?.lowercase()?.contains(query) == true)
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pacientes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${patients.size} registrado${if (patients.size == 1) "" else "s"}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            archivedPatients = database.patientDao().getArchivedPatients()
                            showArchivedDialog = true
                        }
                    }
                ) {
                    Text("Archivados")
                }

                OutlinedButton(
                    onClick = {
                        showExportDialog = true
                    }
                ) {
                    Text("CSV")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar paciente") },
            placeholder = { Text("Código, historia clínica o alias") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        when {
            patients.isEmpty() -> {
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
            }

            filteredPatients.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "No se encontraron pacientes con ese criterio.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF6B7280),
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredPatients.forEachIndexed { index, patient ->
                        PatientListCard(
                            index = index + 1,
                            patient = patient,
                            onClick = {
                                onOpenHistory(patient.patientCode)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        ExportCsvDialog(
            onDismiss = {
                showExportDialog = false
            },
            onConfirmExport = {
                showExportDialog = false
                exportCsvLauncher.launch("pacientes_CDT_RAM.csv")
            }
        )
    }
    if (showArchivedDialog) {
        ArchivedPatientsDialog(
            archivedPatients = archivedPatients,
            onDismiss = {
                showArchivedDialog = false
            },
            onRestorePatient = { patient ->
                coroutineScope.launch {
                    val now = System.currentTimeMillis()

                    database.patientDao().restorePatient(
                        patientCode = patient.patientCode,
                        updatedAt = now
                    )

                    patients = database.patientDao().getAllPatients()
                    archivedPatients = database.patientDao().getArchivedPatients()
                    showArchivedDialog = false
                }
            }
        )
    }
}

