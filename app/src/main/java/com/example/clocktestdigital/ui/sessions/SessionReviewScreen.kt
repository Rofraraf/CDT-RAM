package com.example.clocktestdigital.ui.sessions

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.components.AppHeader
import kotlinx.coroutines.launch
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.io.File

@Composable
fun SessionReviewScreen(
    sessionId: Long,
    onReviewSaved: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var session by remember { mutableStateOf<TestSessionEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var professionalNotes by remember { mutableStateOf("") }
    var validityState by remember { mutableStateOf<String?>(null) }

    var executionMetrics by remember { mutableStateOf(SessionExecutionMetrics()) }

    LaunchedEffect(sessionId) {
        val loadedSession = database.testSessionDao().getSessionById(sessionId)

        session = loadedSession

        if (loadedSession != null) {
            professionalNotes = loadedSession.professionalNotes.orEmpty()
            validityState = when (loadedSession.isValidTest) {
                true -> "VALID"
                false -> "INVALID"
                null -> null
            }
            val inputEvents = database.inputEventDao().getEventsBySession(loadedSession.localId)

            executionMetrics = calculateSessionExecutionMetrics(
                events = inputEvents,
                sessionDurationMs = loadedSession.totalSessionTimeMs
                    ?: (loadedSession.executionTimeSeconds * 1000L)
            )
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        AppHeader()

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Revisión de sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Observaciones profesionales de la prueba",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (isLoading) {
            Text(
                text = "Cargando sesión...",
                color = Color(0xFF6B7280)
            )
        } else if (session == null) {
            Text(
                text = "No se ha encontrado la sesión seleccionada.",
                color = Color(0xFFB45309),
                fontWeight = FontWeight.SemiBold
            )
        } else {
            val currentSession = session!!

            SessionSummaryCard(session = currentSession)

            Spacer(modifier = Modifier.height(16.dp))

            DrawingImageCard(
                imagePath = currentSession.drawingImagePath
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputEventsSummaryCard(
                metrics = executionMetrics
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Valoración profesional",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Validez de la prueba",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = validityState == "VALID",
                            onClick = { validityState = "VALID" },
                            label = { Text("Válida") }
                        )

                        FilterChip(
                            selected = validityState == "INVALID",
                            onClick = { validityState = "INVALID" },
                            label = { Text("No válida") }
                        )

                        FilterChip(
                            selected = validityState == null,
                            onClick = { validityState = null },
                            label = { Text("Sin valorar") }
                        )
                    }

                    OutlinedTextField(
                        value = professionalNotes,
                        onValueChange = { professionalNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text("Observaciones clínicas") },
                        placeholder = {
                            Text("Añadir comentarios sobre ejecución, dudas, interrupciones o comportamiento observado.")
                        },
                        shape = RoundedCornerShape(16.dp)
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val now = System.currentTimeMillis()

                                val isValidTest = when (validityState) {
                                    "VALID" -> true
                                    "INVALID" -> false
                                    else -> null
                                }

                                database.testSessionDao().updateSessionReview(
                                    sessionId = currentSession.localId,
                                    professionalNotes = professionalNotes.trim().ifEmpty { null },
                                    isReviewed = true,
                                    reviewedAt = now,
                                    isValidTest = isValidTest,
                                    updatedAt = now
                                )

                                Toast.makeText(
                                    context,
                                    "Revisión guardada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onReviewSaved()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar revisión")
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionSummaryCard(
    session: TestSessionEntity
) {
    val dateText = DateFormat
        .format("dd/MM/yyyy HH:mm", session.testDateTime)
        .toString()

    val minutes = session.executionTimeSeconds / 60
    val seconds = session.executionTimeSeconds % 60
    val executionTimeText = String.format("%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen de la sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Fecha: $dateText",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Paciente: ${session.patientCode}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Tiempo de ejecución: $executionTimeText",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "${session.strokeCount} trazos · " +
                        String.format("%.2f presión rel.", session.averagePressure) +
                        " · " +
                        String.format("%.1f mm/s", session.averageSpeedMmPerSec),
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "${session.pauseCount} pausas · " +
                        String.format("%.1f s", session.totalPauseTimeMs / 1000f),
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            session.initialLatencyMs?.let { latency ->
                Text(
                    text = String.format("Latencia inicial: %.1f s", latency / 1000f),
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )
            }

            Text(
                text = if (session.isReviewed) "Estado: revisada" else "Estado: pendiente de revisión",
                color = if (session.isReviewed) Color(0xFF2E7D32) else Color(0xFFB45309),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

}
@Composable
private fun DrawingImageCard(
    imagePath: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Dibujo final",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (imagePath.isNullOrBlank()) {
                Text(
                    text = "No hay imagen guardada para esta sesión.",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )
            } else {
                val imageFile = File(imagePath)
                val bitmap = remember(imagePath) {
                    if (imageFile.exists()) {
                        BitmapFactory.decodeFile(imagePath)
                    } else {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Dibujo final del Test del Reloj",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "No se pudo cargar la imagen del dibujo.",
                        color = Color(0xFFB45309),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Composable
private fun InputEventsSummaryCard(
    metrics: SessionExecutionMetrics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Datos de ejecución",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Eventos registrados: ${metrics.totalEventCount}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Eventos de dibujo: ${metrics.drawingEventCount}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Eventos hover: ${metrics.hoverEventCount}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Segmentos hover: ${metrics.hoverSegmentCount}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Tiempo total en hover: ${formatSeconds(metrics.totalHoverTimeMs)}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Duración media de hover: ${formatSeconds(metrics.averageHoverSegmentTimeMs)}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Hover antes del primer trazo: ${formatSeconds(metrics.hoverBeforeFirstDrawMs)}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Text(
                text = "Proporción hover/sesión: ${String.format("%.1f", metrics.hoverPercentageOfSession)} %",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}

private fun formatSeconds(milliseconds: Long): String {
    return String.format("%.1f s", milliseconds / 1000f)
}