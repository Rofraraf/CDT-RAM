package com.example.clocktestdigital.ui.test

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.width
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clocktestdigital.data.files.saveDrawingBitmapToInternalStorage
import com.example.clocktestdigital.data.local.AppDatabase
import com.example.clocktestdigital.data.local.InputEventEntity
import com.example.clocktestdigital.data.local.PatientEntity
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.drawing.DrawingCanvasView
import com.example.clocktestdigital.ui.components.MetricCard
import kotlinx.coroutines.launch

@Composable
fun TestScreen(
    patientCode: String?,
    onPatientSelected: (String) -> Unit
) {
    var canvasView by remember { mutableStateOf<DrawingCanvasView?>(null) }
    var strokeCount by remember { mutableIntStateOf(0) }
    var averagePressure by remember { mutableStateOf(0f) }
    var averageSpeed by remember { mutableStateOf(0f) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    var selectedPatientCode by remember { mutableStateOf(patientCode) }
    var availablePatients by remember { mutableStateOf<List<PatientEntity>>(emptyList()) }

    var totalPauseTimeMs by remember { mutableStateOf(0L) }
    var pauseCount by remember { mutableIntStateOf(0) }

    var isRunning by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }

    var testStartTime by remember { mutableStateOf<Long?>(null) }
    var firstTouchTime by remember { mutableStateOf<Long?>(null) }
    var endTime by remember {mutableStateOf<Long?>(null) }

    var initialLatencyMs by remember { mutableStateOf<Long?>(null) }
    var totalSessionTimeMs by remember { mutableStateOf<Long?>(null) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds++
        }
    }

    LaunchedEffect(Unit) {
        val existingPatient = database.patientDao().getPatientByCode("PAC-001")

        if (existingPatient == null) {
            val now = System.currentTimeMillis()

            database.patientDao().insertPatient(
                PatientEntity(
                    patientCode = "PAC-001",
                    clinicalRecordId = null,
                    displayName = null,
                    birthYear = null,
                    sex = null,
                    clinicalNotes = null,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }

        availablePatients = database.patientDao().getAllPatients()
    }
    LaunchedEffect(patientCode) {
        selectedPatientCode = patientCode
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val formattedPauseTime = String.format("%.1f s", totalPauseTimeMs / 1000f)
    val formattedPauses = "$pauseCount · $formattedPauseTime"

    val selectedPatientText = selectedPatientCode ?: "Seleccione un paciente"
    val hasSelectedPatient = selectedPatientCode != null

    var savedSessions by remember { mutableStateOf<List<TestSessionEntity>>(emptyList()) }
    var showSavedSessions by remember { mutableStateOf(false) }
    fun startCleanTestAttempt() {
        val startTime = android.os.SystemClock.uptimeMillis()

        canvasView?.clearCanvas()
        canvasView?.clearCapturedInputEvents()

        strokeCount = 0
        averagePressure = 0f
        averageSpeed = 0f
        elapsedSeconds = 0
        totalPauseTimeMs = 0L
        pauseCount = 0

        hasStarted = true
        isRunning = true

        testStartTime = startTime
        firstTouchTime = null
        endTime = null
        initialLatencyMs = null
        totalSessionTimeMs = null

        canvasView?.isTestActive = true
        canvasView?.testStartTimeMs = startTime
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "CDT | RAM",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Captura digital con stylus",
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formattedTime,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tiempo",
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        PatientInlineSelector(
            patients = availablePatients,
            selectedPatientCode = selectedPatientCode,
            onPatientSelected = { patientCode ->
                selectedPatientCode = patientCode
                onPatientSelected(patientCode)
            },
            onCreateNewPatient = {
                coroutineScope.launch {
                    val now = System.currentTimeMillis()
                    val nextNumber = database.patientDao().getPatientCount() + 1
                    val newPatientCode = String.format("PAC-%03d", nextNumber)

                    database.patientDao().insertPatient(
                        PatientEntity(
                            patientCode = newPatientCode,
                            clinicalRecordId = null,
                            displayName = null,
                            birthYear = null,
                            sex = null,
                            clinicalNotes = null,
                            createdAt = now,
                            updatedAt = now
                        )
                    )

                    availablePatients = database.patientDao().getAllPatients()
                    selectedPatientCode = newPatientCode
                    onPatientSelected(newPatientCode)
                }
            }
        )

        if (!hasSelectedPatient) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seleccione un paciente antes de iniciar el test.",
                color = Color(0xFFB45309),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7F5))
        ) {
            Text(
                text = "Pida al paciente que dibuje un reloj con todos los números y que marque las 11:10. Use un stylus para capturar presión y velocidad del trazo.",
                modifier = Modifier.padding(14.dp),
                fontSize = 16.sp,
                color = Color(0xFF23403B)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                startCleanTestAttempt()
            },
            enabled = !hasStarted && hasSelectedPatient,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Iniciar test")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AndroidView(
                factory = { context ->
                    DrawingCanvasView(context).also { view ->
                        canvasView = view
                        view.isTestActive = hasStarted

                        view.onStrokeCountChanged = { newCount ->
                            strokeCount = newCount
                        }
                        view.onAveragePressureChanged = { newAveragePressure ->
                            averagePressure = newAveragePressure
                        }
                        view.onAverageSpeedChanged = { newAverageSpeed ->
                            averageSpeed = newAverageSpeed
                        }
                        view.onFirstTouchDetected = { firstTouchEventTime ->
                            if (firstTouchTime == null && testStartTime != null) {
                                firstTouchTime = firstTouchEventTime
                                initialLatencyMs = firstTouchEventTime - testStartTime!!
                                isRunning = true
                            }

                        }
                        view.onTotalPauseTimeChanged = { newTotalPauseTime ->
                            totalPauseTimeMs = newTotalPauseTime
                        }

                        view.onPauseCountChanged = { newPauseCount ->
                            pauseCount = newPauseCount
                        }
                    }
                },
                update = { view ->
                    canvasView = view
                    view.isTestActive = hasStarted

                    view.onStrokeCountChanged = { newCount ->
                        strokeCount = newCount
                    }
                    view.onAveragePressureChanged = { newAveragePressure ->
                        averagePressure = newAveragePressure
                    }
                    view.onAverageSpeedChanged = { newAverageSpeed ->
                        averageSpeed = newAverageSpeed
                    }
                    view.onFirstTouchDetected = { firstTouchEventTime ->
                        if (firstTouchTime == null && testStartTime != null) {
                            firstTouchTime = firstTouchEventTime
                            initialLatencyMs = firstTouchEventTime - testStartTime!!
                            isRunning = true
                        }
                    }
                    view.onTotalPauseTimeChanged = { newTotalPauseTime ->
                        totalPauseTimeMs = newTotalPauseTime
                    }

                    view.onPauseCountChanged = { newPauseCount ->
                        pauseCount = newPauseCount
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showRestartDialog = true
                },
                enabled = hasStarted && endTime == null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reiniciar test")
            }

            Button(
                onClick = {
                    endTime = android.os.SystemClock.uptimeMillis()

                    totalSessionTimeMs = if (testStartTime != null) {
                        endTime!! - testStartTime!!
                    } else {
                        null
                    }

                    isRunning = false
                    canvasView?.isTestActive = false

                    val now = System.currentTimeMillis()

                    val selectedCode = selectedPatientCode ?: return@Button

                    val drawingImagePath = canvasView?.exportToBitmap()?.let { bitmap ->
                        saveDrawingBitmapToInternalStorage(
                            context = context,
                            bitmap = bitmap,
                            patientCode = selectedCode,
                            timestamp = now
                        )
                    }

                    val session = TestSessionEntity(
                        patientCode = selectedCode,
                        testDateTime = now,
                        executionTimeSeconds = elapsedSeconds,
                        initialLatencyMs = initialLatencyMs,
                        totalSessionTimeMs = totalSessionTimeMs,
                        strokeCount = strokeCount,
                        averagePressure = averagePressure,
                        averageSpeedMmPerSec = averageSpeed,
                        pauseCount = pauseCount,
                        totalPauseTimeMs = totalPauseTimeMs,
                        drawingImagePath = drawingImagePath,
                        createdAt = now,
                        updatedAt = now
                    )

                    coroutineScope.launch {
                        val sessionId = database.testSessionDao().insertSession(session)

                        val inputEvents = canvasView
                            ?.getCapturedInputEvents()
                            ?.map { event ->
                                InputEventEntity(
                                    sessionId = sessionId,
                                    patientCode = selectedCode,
                                    eventType = event.eventType,
                                    x = event.x,
                                    y = event.y,
                                    pressure = event.pressure,
                                    eventTimeMs = event.eventTimeMs,
                                    relativeTimeMs = event.relativeTimeMs,
                                    strokeIndex = event.strokeIndex,
                                    isHoverEvent = event.isHoverEvent,
                                    createdAt = System.currentTimeMillis()
                                )
                            }
                            .orEmpty()

                        if (inputEvents.isNotEmpty()) {
                            database.inputEventDao().insertEvents(inputEvents)
                        }

                        showSaveDialog = true
                    }
                },
                enabled = hasStarted && endTime == null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Finalizar test")
            }
        }

        if (showSaveDialog) {
            Dialog(
                onDismissRequest = { showSaveDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    modifier = Modifier.width(300.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Sesión guardada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF23403B)
                        )

                        Button(
                            onClick = { showSaveDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }

        if (showRestartDialog) {
            Dialog(
                onDismissRequest = { showRestartDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    modifier = Modifier.width(320.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Reiniciar prueba",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF23403B)
                        )

                        Text(
                            text = "Se borrará el dibujo y los datos registrados en este intento antes de guardarlo.",
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF4B5563)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRestartDialog = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    showRestartDialog = false
                                    startCleanTestAttempt()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reiniciar")l
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "MÉTRICAS EN TIEMPO REAL",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Trazos", strokeCount.toString(), Modifier.weight(1f))
                MetricCard("Presión", String.format("%.2f rel.", averagePressure), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Velocidad", String.format("%.1f mm/s", averageSpeed), Modifier.weight(1f))
                MetricCard("Pausas", formattedPauses, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

}