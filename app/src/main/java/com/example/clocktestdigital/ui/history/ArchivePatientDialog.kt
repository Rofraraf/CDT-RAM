package com.example.clocktestdigital.ui.history

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ArchivePatientDialog(
    patientCode: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Archivar paciente")
        },
        text = {
            Text(
                "¿Quieres archivar el paciente $patientCode? " +
                        "Dejará de aparecer en la lista principal, pero sus sesiones se conservarán."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Archivar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}