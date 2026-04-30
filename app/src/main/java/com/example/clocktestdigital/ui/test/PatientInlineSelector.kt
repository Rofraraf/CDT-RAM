package com.example.clocktestdigital.ui.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clocktestdigital.data.local.PatientEntity

@Composable
fun PatientInlineSelector(
    patients: List<PatientEntity>,
    selectedPatientCode: String?,
    onPatientSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    fun patientMainText(patient: PatientEntity): String {
        return patient.patientCode
    }

    fun patientSecondaryText(patient: PatientEntity): String {
        return when {
            !patient.displayName.isNullOrBlank() -> patient.displayName
            !patient.clinicalRecordId.isNullOrBlank() -> patient.clinicalRecordId
            else -> "Sin datos completados"
        }
    }

    fun patientFieldText(patient: PatientEntity): String {
        return if (!patient.displayName.isNullOrBlank()) {
            "${patient.patientCode} · ${patient.displayName}"
        } else {
            patient.patientCode
        }
    }

    val selectedPatient = remember(selectedPatientCode, patients) {
        patients.firstOrNull { it.patientCode == selectedPatientCode }
    }

    val selectedLabel = selectedPatient?.let { patientFieldText(it) } ?: ""

    LaunchedEffect(selectedPatientCode, patients, expanded) {
        if (!expanded) {
            query = selectedLabel
        }
    }

    val filteredPatients = remember(query, patients, selectedLabel) {
        val text = query.trim()

        if (text.isBlank() || text == selectedLabel) {
            patients
        } else {
            patients.filter { patient ->
                patient.patientCode.contains(text, ignoreCase = true) ||
                        (patient.clinicalRecordId?.contains(text, ignoreCase = true) == true) ||
                        (patient.displayName?.contains(text, ignoreCase = true) == true)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        expanded = true
                    }
                },
            singleLine = true,
            label = { Text("Seleccionar paciente") },
            placeholder = { Text("Código, historia clínica o alias") }
        )

        AnimatedVisibility(visible = expanded) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp,
                shadowElevation = 4.dp
            ) {
                if (filteredPatients.isEmpty()) {
                    Text(
                        text = "No se encontraron pacientes",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                    ) {
                        itemsIndexed(
                            items = filteredPatients,
                            key = { _, patient -> patient.patientCode }
                        ) { index, patient ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        query = patientFieldText(patient)
                                        expanded = false
                                        onPatientSelected(patient.patientCode)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = patientMainText(patient),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = patientSecondaryText(patient),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            if (index < filteredPatients.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}