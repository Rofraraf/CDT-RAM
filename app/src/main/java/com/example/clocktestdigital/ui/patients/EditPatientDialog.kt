package com.example.clocktestdigital.ui.patients

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clocktestdigital.data.local.PatientEntity
import com.example.clocktestdigital.ui.components.BirthYearDropdownField
import com.example.clocktestdigital.ui.components.PatientFormSection
import com.example.clocktestdigital.ui.components.SexDropdownField

@Composable
fun EditPatientDialog(
    patient: PatientEntity,
    onDismiss: () -> Unit,
    onSave: (
        clinicalRecordId: String?,
        displayName: String?,
        birthYear: Int?,
        sex: String?,
        clinicalNotes: String?
    ) -> Unit
) {
    var clinicalRecordId by remember { mutableStateOf(patient.clinicalRecordId.orEmpty()) }
    var displayName by remember { mutableStateOf(patient.displayName.orEmpty()) }
    var birthYear by remember { mutableStateOf(patient.birthYear?.toString().orEmpty()) }
    var sex by remember { mutableStateOf(patient.sex.orEmpty()) }
    var clinicalNotes by remember { mutableStateOf(patient.clinicalNotes.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Editar paciente")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                PatientFormSection(
                    title = "IDENTIFICACIÓN"
                ) {
                    OutlinedTextField(
                        value = patient.patientCode,
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
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Alias o nombre visible") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                PatientFormSection(
                    title = "DATOS BÁSICOS"
                ) {
                    BirthYearDropdownField(
                        value = birthYear,
                        onValueChange = { birthYear = it }
                    )

                    SexDropdownField(
                        value = sex,
                        onValueChange = { sex = it }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                PatientFormSection(
                    title = "OBSERVACIONES"
                ) {
                    OutlinedTextField(
                        value = clinicalNotes,
                        onValueChange = { clinicalNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Notas clínicas generales") },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        clinicalRecordId.trim().ifEmpty { null },
                        displayName.trim().ifEmpty { null },
                        birthYear.toIntOrNull(),
                        sex.trim().ifEmpty { null },
                        clinicalNotes.trim().ifEmpty { null }
                    )
                }
            ) {
                Text("Guardar cambios")
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