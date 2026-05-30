package com.example.clocktestdigital.ui.patients

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clocktestdigital.data.local.PatientEntity

@Composable
fun ArchivedPatientsDialog(
    archivedPatients: List<PatientEntity>,
    onDismiss: () -> Unit,
    onRestorePatient: (PatientEntity) -> Unit
) {
    var patientToRestore by remember { mutableStateOf<PatientEntity?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pacientes archivados",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (archivedPatients.isEmpty()) {
                Text(
                    text = "No hay pacientes archivados.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(
                        items = archivedPatients,
                        key = { it.patientCode }
                    ) { patient ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    patientToRestore = patient
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "${patient.patientCode} · ${patientArchivedLabel(patient)}",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Tocar para restaurar",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )

    patientToRestore?.let { patient ->
        AlertDialog(
            onDismissRequest = {
                patientToRestore = null
            },
            title = {
                Text("Restaurar paciente")
            },
            text = {
                Text(
                    text = "¿Deseas restaurar el paciente ${patient.patientCode}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        patientToRestore = null
                        onRestorePatient(patient)
                    }
                ) {
                    Text("Sí, restaurar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        patientToRestore = null
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

private fun patientArchivedLabel(patient: PatientEntity): String {
    val alias = patient.displayName?.takeIf { it.isNotBlank() }
    val clinicalRecord = patient.clinicalRecordId?.takeIf { it.isNotBlank() }

    return when {
        alias != null && clinicalRecord != null -> "$alias · HC: $clinicalRecord"
        alias != null -> alias
        clinicalRecord != null -> "HC: $clinicalRecord"
        else -> "Sin datos completados"
    }
}