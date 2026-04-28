package com.example.clocktestdigital.ui.patients

import com.example.clocktestdigital.data.local.PatientEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun buildPatientsCsv(
    patients: List<PatientEntity>
): String {
    val header = listOf(
        "patientCode",
        "clinicalRecordId",
        "displayName",
        "birthYear",
        "sex",
        "createdAt",
        "updatedAt"
    )

    return buildString {
        appendLine(header.joinToString(";"))

        patients.forEach { patient ->
            val row = listOf(
                patient.patientCode,
                patient.clinicalRecordId.orEmpty(),
                patient.displayName.orEmpty(),
                patient.birthYear?.toString().orEmpty(),
                patient.sex.orEmpty(),
                formatDate(patient.createdAt),
                formatDate(patient.updatedAt)
            )

            appendLine(row.joinToString(";") { escapeCsvValue(it) })
        }
    }
}

private fun escapeCsvValue(value: String): String {
    val escaped = value.replace("\"", "\"\"")

    return if (
        escaped.contains(";") ||
        escaped.contains("\"") ||
        escaped.contains("\n") ||
        escaped.contains("\r")
    ) {
        "\"$escaped\""
    } else {
        escaped
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}