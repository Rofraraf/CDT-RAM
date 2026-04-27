package com.example.clocktestdigital.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    val patientCode: String,

    val clinicalRecordId: String? = null,

    val displayName: String? = null,

    val birthYear: Int? = null,

    val sex: String? = null,

    val clinicalNotes: String? = null,

    val remoteId: String? = null,

    val syncStatus: String = "PENDING",

    val createdAt: Long,

    val updatedAt: Long
)
