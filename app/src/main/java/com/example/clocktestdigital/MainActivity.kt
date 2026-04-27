
package com.example.clocktestdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.clocktestdigital.ui.patients.PatientsScreen
import com.example.clocktestdigital.ui.test.TestScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTestTheme {
                var currentScreen by remember { mutableStateOf("test") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "patients" -> PatientsScreen(
                            onBackToTest = {
                                currentScreen = "test"
                            }
                        )

                        else -> TestScreen(
                            onGoToPatients = {
                                currentScreen = "patients"
                            }
                        )
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



