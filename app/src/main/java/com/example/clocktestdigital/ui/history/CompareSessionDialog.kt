package com.example.clocktestdigital.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.sessions.SessionExecutionMetrics
import com.example.clocktestdigital.ui.sessions.calculateSessionExecutionMetrics

@Composable
fun CompareSessionsDialog(
    sessions: List<TestSessionEntity>,
    database: AppDatabase,
    onDismiss: () -> Unit
) {
    var selectedSessionA by remember { mutableStateOf<TestSessionEntity?>(null) }
    var selectedSessionB by remember { mutableStateOf<TestSessionEntity?>(null) }

    var metricsA by remember { mutableStateOf(SessionExecutionMetrics()) }
    var metricsB by remember { mutableStateOf(SessionExecutionMetrics()) }

    LaunchedEffect(selectedSessionA?.localId) {
        val session = selectedSessionA

        if (session != null) {
            val events = database.inputEventDao().getEventsBySession(session.localId)

            metricsA = calculateSessionExecutionMetrics(
                events = events,
                sessionDurationMs = session.totalSessionTimeMs
                    ?: (session.executionTimeSeconds * 1000L)
            )
        }
    }

    LaunchedEffect(selectedSessionB?.localId) {
        val session = selectedSessionB

        if (session != null) {
            val events = database.inputEventDao().getEventsBySession(session.localId)

            metricsB = calculateSessionExecutionMetrics(
                events = events,
                sessionDurationMs = session.totalSessionTimeMs
                    ?: (session.executionTimeSeconds * 1000L)
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 640.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Comparar sesiones",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (sessions.size < 2) {
                    Text(
                        text = "Este paciente necesita al menos dos sesiones para poder compararlas.",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SessionSelectorField(
                            label = "Sesión A",
                            labelColor = Color(0xFF2563EB),
                            sessions = sessions,
                            selectedSession = selectedSessionA,
                            onSessionSelected = { selectedSessionA = it },
                            modifier = Modifier.weight(1f)
                        )

                        SessionSelectorField(
                            label = "Sesión B",
                            labelColor = Color(0xFFDC2626),
                            sessions = sessions,
                            selectedSession = selectedSessionB,
                            onSessionSelected = { selectedSessionB = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    when {
                        selectedSessionA == null || selectedSessionB == null -> {
                            Text(
                                text = "Selecciona dos sesiones del mismo paciente para comparar sus métricas.",
                                color = Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }

                        selectedSessionA?.localId == selectedSessionB?.localId -> {
                            Text(
                                text = "Selecciona dos sesiones distintas.",
                                color = Color(0xFFB45309),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        else -> {
                            DrawingComparisonCard(
                                sessionA = selectedSessionA!!,
                                sessionB = selectedSessionB!!
                            )

                            ComparisonChartCard(
                                sessionA = selectedSessionA!!,
                                sessionB = selectedSessionB!!,
                                metricsA = metricsA,
                                metricsB = metricsB
                            )

                            CompareMetricsCard(
                                sessionA = selectedSessionA!!,
                                sessionB = selectedSessionB!!,
                                metricsA = metricsA,
                                metricsB = metricsB
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

