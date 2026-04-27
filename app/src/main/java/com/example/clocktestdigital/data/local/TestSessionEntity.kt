package com.example.clocktestdigital.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_sessions")
data class TestSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,

    // Identificador futuro si la sesión se sincroniza con un servidor externo.
    val remoteId: String? = null,

    // De momento usamos un código pseudonimizado.
    val patientCode: String,

    // Fecha y hora de realización del test.
    val testDateTime: Long,

    // Métricas temporales.
    val executionTimeSeconds: Int,
    val initialLatencyMs: Long?,
    val totalSessionTimeMs: Long?,

    // Métricas del trazado.
    val strokeCount: Int,
    val averagePressure: Float,
    val averageSpeedMmPerSec: Float,
    val pauseCount: Int,
    val totalPauseTimeMs: Long,

    // Campos de revisión profesional.
    val professionalNotes: String? = null,
    val isReviewed: Boolean = false,
    val reviewedAt: Long? = null,
    val isValidTest: Boolean? = null,

    // Estado de sincronización futura.
    val syncStatus: String = "PENDING",

    // Auditoría local.
    val createdAt: Long,
    val updatedAt: Long
)