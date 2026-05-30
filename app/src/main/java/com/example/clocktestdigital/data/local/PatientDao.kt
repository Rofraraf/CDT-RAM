package com.example.clocktestdigital.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Query("SELECT * FROM patients WHERE isActive = 1 ORDER BY createdAt DESC")
    suspend fun getAllPatients(): List<PatientEntity>

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

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
    @Query("""
        UPDATE patients
        SET isActive = 0,
            archivedAt = :archivedAt,
            updatedAt = :updatedAt,
            syncStatus = 'PENDING'
        WHERE patientCode = :patientCode
    """)
    suspend fun archivePatient(
        patientCode: String,
        archivedAt: Long,
        updatedAt: Long
    )
    @Query("""
    SELECT * FROM patients
    WHERE isActive = 0
    ORDER BY archivedAt DESC
""")
    suspend fun getArchivedPatients(): List<PatientEntity>

    @Query("""
    UPDATE patients
    SET isActive = 1,
        archivedAt = NULL,
        updatedAt = :updatedAt,
        syncStatus = 'PENDING'
    WHERE patientCode = :patientCode
""")
    suspend fun restorePatient(
        patientCode: String,
        updatedAt: Long
    )
}