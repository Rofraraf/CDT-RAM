package com.example.clocktestdigital.ui.patients

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ExportCsvDialog(
    onDismiss: () -> Unit,
    onConfirmExport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Exportar CSV")
        },
        text = {
            Text("Se exportará el listado de pacientes en formato CSV.")
        },
        confirmButton = {
            Button(
                onClick = onConfirmExport
            ) {
                Text("Exportar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}