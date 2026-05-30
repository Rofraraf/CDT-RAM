package com.example.clocktestdigital.ui.sessions

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.example.clocktestdigital.analysis.SessionExecutionMetrics
import com.example.clocktestdigital.analysis.buildSessionMetricInterpretations
import com.example.clocktestdigital.analysis.calculateSessionExecutionMetrics
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.components.AppHeader
import com.example.clocktestdigital.ui.sessions.pdf.buildSessionPdfFileName
import com.example.clocktestdigital.ui.sessions.pdf.writeSessionPdf
import kotlinx.coroutines.launch

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
    var patientAlias by remember { mutableStateOf<String?>(null) }

    var professionalNotes by remember { mutableStateOf("") }
    var validityState by remember { mutableStateOf<String?>(null) }

    var executionMetrics by remember { mutableStateOf(SessionExecutionMetrics()) }

    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri == null) {
            Toast.makeText(
                context,
                "Exportación cancelada",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        val currentSession = session

        if (currentSession == null) {
            Toast.makeText(
                context,
                "No se pudo generar el informe",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                writeSessionPdf(
                    outputStream = outputStream,
                    session = currentSession,
                    executionMetrics = executionMetrics,
                    patientAlias = patientAlias
                )
            }

            Toast.makeText(
                context,
                "Informe PDF generado correctamente",
                Toast.LENGTH_SHORT
            ).show()
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "Error al generar el informe PDF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    LaunchedEffect(sessionId) {
        val loadedSession = database.testSessionDao().getSessionById(sessionId)

        session = loadedSession

        if (loadedSession != null) {
            val loadedPatient = database.patientDao().getPatientByCode(
                loadedSession.patientCode
            )

            patientAlias = loadedPatient?.displayName

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

            SessionReviewDataCard(
                session = currentSession
            )

            Spacer(modifier = Modifier.height(16.dp))

            SessionReviewDrawingCard(
                imagePath = currentSession.drawingImagePath
            )

            Spacer(modifier = Modifier.height(16.dp))

            SessionReviewMetricsCard(
                session = currentSession
            )

            Spacer(modifier = Modifier.height(16.dp))

            SessionReviewIndicatorsCard(
                interpretations = buildSessionMetricInterpretations(
                    session = currentSession,
                    metrics = executionMetrics
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SessionReviewExecutionCard(
                metrics = executionMetrics
            )

            Spacer(modifier = Modifier.height(16.dp))

            SessionReviewProfessionalCard(
                professionalNotes = professionalNotes,
                onProfessionalNotesChange = { professionalNotes = it },
                validityState = validityState,
                onValidityStateChange = { validityState = it },
                onSaveReview = {
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
                onGeneratePdf = {
                    pdfExportLauncher.launch(
                        buildSessionPdfFileName(currentSession)
                    )
                }
            )
            }
        }
    }
