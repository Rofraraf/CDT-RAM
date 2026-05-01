package com.example.clocktestdigital.ui.history

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun PatientHistoryScreen(
    patientCode: String,
    onPatientArchived: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var sessions by remember { mutableStateOf<List<TestSessionEntity>>(emptyList()) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(patientCode) {
        sessions = database.testSessionDao().getSessionsByPatient(patientCode)
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
            text = "Historial del paciente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = patientCode,
            fontSize = 15.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = {
                showArchiveDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Archivar paciente")
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                    HistorySessionCard(session = session)
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
}