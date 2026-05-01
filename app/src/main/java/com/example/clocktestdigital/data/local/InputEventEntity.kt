package com.example.clocktestdigital.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "input_events",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["patientCode"])
    ]
)
data class InputEventEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,

    // Sesión del test a la que pertenece el evento.
    val sessionId: Long,

    // Código pseudonimizado del paciente.
    val patientCode: String,

    // Tipo de evento: DRAW_DOWN, DRAW_MOVE, DRAW_UP, HOVER_ENTER, HOVER_MOVE, HOVER_EXIT.
    val eventType: String,

    // Coordenadas capturadas en el lienzo.
    val x: Float,
    val y: Float,

    // Presión relativa del stylus, si aplica.
    val pressure: Float? = null,

    // Tiempo del evento según MotionEvent.
    val eventTimeMs: Long,

    // Tiempo relativo desde el inicio del test, si está disponible.
    val relativeTimeMs: Long? = null,

    // Índice del trazo al que pertenece el punto.
    val strokeIndex: Int? = null,

    // Campo preparado para distinguir eventos generados antes/después de contacto.
    val isHoverEvent: Boolean = false,

    // Auditoría local.
    val createdAt: Long
)