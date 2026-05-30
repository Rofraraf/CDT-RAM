package com.example.clocktestdigital.ui.history

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.analysis.PatientHistoryAnalysisItem
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.PatientEntity
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.components.AppHeader
import com.example.clocktestdigital.ui.history.compare.CompareSessionsDialog
import com.example.clocktestdigital.ui.history.pdf.buildPatientHistoryPdfFileName
import com.example.clocktestdigital.ui.history.pdf.writePatientHistoryPdf
import com.example.clocktestdigital.ui.patients.EditPatientDialog
import com.example.clocktestdigital.analysis.calculateSessionExecutionMetrics
import kotlinx.coroutines.launch

@Composable
fun PatientHistoryScreen(
    patientCode: String,
    onPatientArchived: () -> Unit,
    onOpenSessionReview: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var patient by remember { mutableStateOf<PatientEntity?>(null) }
    var sessions by remember { mutableStateOf<List<TestSessionEntity>>(emptyList()) }
    var historyAnalysisItems by remember { mutableStateOf<List<PatientHistoryAnalysisItem>>(emptyList()) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showCompareDialog by remember { mutableStateOf(false) }

    val historyPdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri == null) {
            Toast.makeText(
                context,
                "Exportación cancelada",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        val currentPatient = patient

        if (currentPatient == null) {
            Toast.makeText(
                context,
                "No se pudo generar el informe del historial",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                writePatientHistoryPdf(
                    outputStream = outputStream,
                    patient = currentPatient,
                    analysisItems = historyAnalysisItems
                )
            }

            Toast.makeText(
                context,
                "Informe del historial generado correctamente",
                Toast.LENGTH_SHORT
            ).show()
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "Error al generar el informe del historial",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(patientCode) {
        patient = database.patientDao().getPatientByCode(patientCode)

        val loadedSessions = database.testSessionDao().getSessionsByPatient(patientCode)
        sessions = loadedSessions

        historyAnalysisItems = loadedSessions.map { session ->
            val events = database.inputEventDao().getEventsBySession(session.localId)

            PatientHistoryAnalysisItem(
                session = session,
                executionMetrics = calculateSessionExecutionMetrics(
                    events = events,
                    sessionDurationMs = session.totalSessionTimeMs
                        ?: (session.executionTimeSeconds * 1000L)
                )
            )
        }
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
            text = "Ficha del paciente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Datos básicos del paciente",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showEditDialog = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Editar paciente")
            }

            OutlinedButton(
                onClick = {
                    showArchiveDialog = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Archivar")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        PatientSummaryCard(
            patient = patient,
            patientCode = patientCode
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Sesiones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Historial y análisis de pruebas realizadas",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showCompareDialog = true
                },
                modifier = Modifier.weight(1f),
                enabled = sessions.size >= 2
            ) {
                Text("Comparar")
            }

            OutlinedButton(
                onClick = {
                    val currentPatient = patient

                    if (currentPatient != null) {
                        historyPdfExportLauncher.launch(
                            buildPatientHistoryPdfFileName(currentPatient)
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = patient != null
            ) {
                Text(
                    text = "Informe PDF",
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (sessions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Todavía no hay sesiones guardadas para este paciente.",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF6B7280),
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sessions.forEach { session ->
                    HistorySessionCard(
                        session = session,
                        onClick = {
                            onOpenSessionReview(session.localId)
                        }
                    )
                }
            }
        }
    }

    if (showArchiveDialog) {
        ArchivePatientDialog(
            patientCode = patientCode,
            onConfirm = {
                coroutineScope.launch {
                    val now = System.currentTimeMillis()

                    database.patientDao().archivePatient(
                        patientCode = patientCode,
                        archivedAt = now,
                        updatedAt = now
                    )

                    Toast.makeText(
                        context,
                        "Paciente archivado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    showArchiveDialog = false
                    onPatientArchived()
                }
            },
            onDismiss = {
                showArchiveDialog = false
            }
        )
    }
    if (showEditDialog && patient != null) {
        EditPatientDialog(
            patient = patient!!,
            onDismiss = {
                showEditDialog = false
            },
            onSave = { clinicalRecordId, displayName, birthYear, sex, clinicalNotes ->
                coroutineScope.launch {
                    val now = System.currentTimeMillis()

                    database.patientDao().updatePatient(
                        patientCode = patientCode,
                        clinicalRecordId = clinicalRecordId,
                        displayName = displayName,
                        birthYear = birthYear,
                        sex = sex,
                        clinicalNotes = clinicalNotes,
                        updatedAt = now
                    )

                    patient = database.patientDao().getPatientByCode(patientCode)

                    Toast.makeText(
                        context,
                        "Paciente actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    showEditDialog = false
                }
            }
        )
    }
    if (showCompareDialog) {
        CompareSessionsDialog(
            sessions = sessions,
            database = database,
            onDismiss = {
                showCompareDialog = false
            }
        )
    }
}
@Composable
private fun PatientSummaryCard(
    patient: PatientEntity?,
    patientCode: String
) {
    val displayName = patient?.displayName?.takeIf { it.isNotBlank() }
    val title = displayName ?: patientCode

    val initial = displayName
        ?.firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: patientCode.removePrefix("PAC-").firstOrNull()?.toString()
        ?: "P"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = Color(0xFFE8F0FE),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PatientInfoChip(text = patientCode)

                        patient?.clinicalRecordId
                            ?.takeIf { it.isNotBlank() }
                            ?.let { PatientInfoChip(text = it) }

                        patient?.birthYear?.let {
                            PatientInfoChip(text = "Nac. $it")
                        }
                    }

                    patient?.sex
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            PatientInfoChip(text = it)
                        }
                }
            }

            if (!patient?.clinicalNotes.isNullOrBlank()) {
                Text(
                    text = patient?.clinicalNotes.orEmpty(),
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun PatientInfoChip(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF334155)
    )
}