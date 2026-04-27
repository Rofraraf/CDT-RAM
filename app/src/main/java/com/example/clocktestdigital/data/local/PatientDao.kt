package com.example.clocktestdigital.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Query("SELECT * FROM patients ORDER BY createdAt DESC")
    suspend fun getAllPatients(): List<PatientEntity>

    @Query("SELECT * FROM patients WHERE patientCode = :patientCode LIMIT 1")
    suspend fun getPatientByCode(patientCode: String): PatientEntity?

    @Query("""
        UPDATE patients
        SET clinicalRecordId = :clinicalRecordId,
            displayName = :displayName,
            birthYear = :birthYear,
            sex = :sex,
            clinicalNotes = :clinicalNotes,
            updatedAt = :updatedAt
        WHERE patientCode = :patientCode
    """)
    suspend fun updatePatient(
        patientCode: String,
        clinicalRecordId: String?,
        displayName: String?,
        birthYear: Int?,
        sex: String?,
        clinicalNotes: String?,
        updatedAt: Long
    )
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int
}