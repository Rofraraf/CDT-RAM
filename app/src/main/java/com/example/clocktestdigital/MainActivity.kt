/*package com.example.clocktestdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.clocktestdigital.ui.theme.ClockTestDigitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClockTestDigitalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClockTestDigitalTheme {
        Greeting("Android")
    }
}
*/

package com.example.clocktestdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestScreen()
                }
            }
        }
    }
}

private val AppColors = lightColorScheme(
    primary = Color(0xFF2F6FED),
    background = Color(0xFFF7F9FC),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1C2430),
    onSurface = Color(0xFF1C2430)
)

@Composable
fun ClockTestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        content = content
    )
}

@Composable
fun TestScreen() {
    var canvasView by remember { mutableStateOf<DrawingCanvasView?>(null) }
    var strokeCount by remember { mutableIntStateOf(0) }
    var averagePressure by remember { mutableStateOf(0f) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }

    var testStartTime by remember { mutableStateOf<Long?>(null) }
    var firstTouchTime by remember { mutableStateOf<Long?>(null) }
    var endTime by remember {mutableStateOf<Long?>(null) }

    var initialLatencyMs by remember { mutableStateOf<Long?>(null) }
    var totalSessionTimeMs by remember { mutableStateOf<Long?>(null) }



    LaunchedEffect(isRunning) {
        while (isRunning) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds++
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

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
                    fontSize = 18.sp,
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

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Seleccionar paciente") },
            placeholder = { Text("PAC-001") },
            readOnly = true,
            singleLine = true
        )

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
                canvasView?.clearCanvas()

                hasStarted = true
                isRunning = false
                elapsedSeconds = 0

                testStartTime = android.os.SystemClock.uptimeMillis()
                firstTouchTime = null
                endTime = null
                initialLatencyMs = null
                totalSessionTimeMs = null

                canvasView?.isTestActive = true
            },
            enabled = !hasStarted,
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

                        view.onFirstTouchDetected = { firstTouchEventTime ->
                            if (firstTouchTime == null && testStartTime != null) {
                                firstTouchTime = firstTouchEventTime
                                initialLatencyMs = firstTouchEventTime - testStartTime!!
                                isRunning = true
                            }

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

                    view.onFirstTouchDetected = { firstTouchEventTime ->
                        if (firstTouchTime == null && testStartTime != null) {
                            firstTouchTime = firstTouchEventTime
                            initialLatencyMs = firstTouchEventTime - testStartTime!!
                            isRunning = true
                        }
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
                    canvasView?.clearCanvas()

                    isRunning = false
                    elapsedSeconds = 0

                    testStartTime = android.os.SystemClock.uptimeMillis()
                    firstTouchTime = null
                    endTime = null
                    initialLatencyMs = null
                    totalSessionTimeMs = null

                    canvasView?.isTestActive = true
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MÉTRICAS EN TIEMPO REAL",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Trazos", strokeCount.toString(), Modifier.weight(1f))
                MetricCard("Presión", String.format("%.2f", averagePressure), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Velocidad", "--", Modifier.weight(1f))
                MetricCard("Pausas", "--", Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color(0xFF6B7280),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}