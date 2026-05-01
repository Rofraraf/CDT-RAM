
package com.example.clocktestdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.clocktestdigital.ui.components.BottomNavBar
import com.example.clocktestdigital.ui.patients.PatientsScreen
import com.example.clocktestdigital.ui.test.TestScreen
import com.example.clocktestdigital.ui.history.PatientHistoryScreen
import com.example.clocktestdigital.ui.newpatient.NewPatientScreen
import com.example.clocktestdigital.ui.home.HomeScreen
import com.example.clocktestdigital.ui.patients.EditPatientScreen
import com.example.clocktestdigital.ui.sessions.SessionReviewScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTestTheme {
                var currentScreen by remember { mutableStateOf("home") }
                var selectedPatientCode by remember { mutableStateOf<String?>(null) }
                var selectedSessionId by remember { mutableStateOf<Long?>(null) }
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onNavigate = { screen ->
                                currentScreen = screen
                            }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentScreen) {
                            "home" -> HomeScreen(
                                selectedPatientCode = selectedPatientCode,
                                onGoToPatients = {
                                    currentScreen = "patients"
                                },
                                onGoToNewPatient = {
                                    currentScreen = "new_patient"
                                },
                                onGoToTest = {
                                    currentScreen = "test"
                                }
                            )

                            "patients" -> PatientsScreen(
                                onOpenHistory = { patientCode ->
                                    selectedPatientCode = patientCode
                                    currentScreen = "history"
                                }
                            )

                            "history" -> PatientHistoryScreen(
                                patientCode = selectedPatientCode ?: "PAC-001",
                                onPatientArchived = {
                                    selectedPatientCode = null
                                    currentScreen = "patients"
                                },
                                onOpenSessionReview = { sessionId ->
                                    selectedSessionId = sessionId
                                    currentScreen = "session_review"
                                }
                            )

                            "session_review" -> SessionReviewScreen(
                                sessionId = selectedSessionId ?: 0L,
                                onReviewSaved = {
                                    currentScreen = "history"
                                }
                            )

                            "edit_patient" -> EditPatientScreen(
                                patientCode = selectedPatientCode ?: "PAC-001",
                                onPatientUpdated = {
                                    currentScreen = "history"
                                }
                            )

                            "new_patient" -> NewPatientScreen(
                                onPatientSaved = { newPatientCode ->
                                    selectedPatientCode = newPatientCode
                                    currentScreen = "history"
                                }
                            )

                            else -> TestScreen(
                                patientCode = selectedPatientCode,
                                onPatientSelected = { patientCode ->
                                    selectedPatientCode = patientCode
                                }
                            )
                        }
                    }
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



