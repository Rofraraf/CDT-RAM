package com.example.clocktestdigital.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.ui.components.AppHeader

@Composable
fun HomeScreen(
    selectedPatientCode: String?,
    onGoToPatients: () -> Unit,
    onGoToNewPatient: () -> Unit,
    onGoToTest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        AppHeader()

        Spacer(modifier = Modifier.height(10.dp))

        ClockHero(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Test del Reloj",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 29.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Digital",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 30.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Captura con stylus, métricas de ejecución e historial de sesiones.",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 8.dp),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                title = "Captura",
                subtitle = "Stylus activo",
                type = FeatureIconType.STYLUS,
                modifier = Modifier.weight(1f)
            )

            FeatureCard(
                title = "Métricas",
                subtitle = "Pausas",
                type = FeatureIconType.METRICS,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                title = "Historial",
                subtitle = "Sesiones",
                type = FeatureIconType.HISTORY,
                modifier = Modifier.weight(1f)
            )

            FeatureCard(
                title = "Revisión",
                subtitle = "Informes",
                type = FeatureIconType.REVIEW,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ClockHero(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(156.dp),
        shape = RoundedCornerShape(42.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            val primary = Color(0xFF2563EB)
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.25f

            fun pointAt(angleDegrees: Float, length: Float): Offset {
                val radians = Math.toRadians(angleDegrees.toDouble())
                return Offset(
                    x = center.x + kotlin.math.cos(radians).toFloat() * length,
                    y = center.y + kotlin.math.sin(radians).toFloat() * length
                )
            }

            drawCircle(
                color = primary,
                radius = radius,
                center = center,
                style = Stroke(width = 5.dp.toPx())
            )

            val markLength = 6.dp.toPx()
            val markStroke = 3.dp.toPx()

            listOf(-90f, 0f, 90f, 180f).forEach { angle ->
                drawLine(
                    color = primary,
                    start = pointAt(angle, radius - markLength),
                    end = pointAt(angle, radius - markLength * 2.1f),
                    strokeWidth = markStroke,
                    cap = StrokeCap.Round
                )
            }

            // Hora solicitada en el Test del Reloj: 11:10.
            // Minutero hacia el 2.
            drawLine(
                color = primary,
                start = center,
                end = pointAt(-30f, radius * 0.78f),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Horaria ligeramente pasada de las 11.
            drawLine(
                color = primary,
                start = center,
                end = pointAt(-115f, radius * 0.52f),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = primary,
                radius = 4.5.dp.toPx(),
                center = center
            )
        }
    }
}

private enum class FeatureIconType {
    STYLUS,
    METRICS,
    HISTORY,
    REVIEW
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    type: FeatureIconType,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            FeatureIcon(type = type)
        }
    }
}

@Composable
private fun FeatureIcon(type: FeatureIconType) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = RoundedCornerShape(15.dp),
        color = Color(0xFFEAF2FF)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            val primary = Color(0xFF2563EB)
            val stroke = 2.4.dp.toPx()

            when (type) {
                FeatureIconType.STYLUS -> {
                    drawLine(
                        color = primary,
                        start = Offset(size.width * 0.25f, size.height * 0.78f),
                        end = Offset(size.width * 0.74f, size.height * 0.28f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = primary,
                        start = Offset(size.width * 0.62f, size.height * 0.23f),
                        end = Offset(size.width * 0.78f, size.height * 0.39f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = primary,
                        radius = 1.8.dp.toPx(),
                        center = Offset(size.width * 0.23f, size.height * 0.80f)
                    )
                }

                FeatureIconType.METRICS -> {
                    val barWidth = size.width * 0.16f

                    drawRoundRect(
                        color = primary,
                        topLeft = Offset(size.width * 0.18f, size.height * 0.55f),
                        size = Size(barWidth, size.height * 0.25f),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )

                    drawRoundRect(
                        color = primary,
                        topLeft = Offset(size.width * 0.42f, size.height * 0.35f),
                        size = Size(barWidth, size.height * 0.45f),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )

                    drawRoundRect(
                        color = primary,
                        topLeft = Offset(size.width * 0.66f, size.height * 0.20f),
                        size = Size(barWidth, size.height * 0.60f),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }

                FeatureIconType.HISTORY -> {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension * 0.34f

                    drawCircle(
                        color = primary,
                        radius = radius,
                        center = center,
                        style = Stroke(width = stroke)
                    )

                    drawLine(
                        color = primary,
                        start = center,
                        end = Offset(center.x, center.y - radius * 0.55f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = primary,
                        start = center,
                        end = Offset(center.x + radius * 0.45f, center.y + radius * 0.20f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }

                FeatureIconType.REVIEW -> {
                    drawRoundRect(
                        color = primary,
                        topLeft = Offset(size.width * 0.22f, size.height * 0.16f),
                        size = Size(size.width * 0.56f, size.height * 0.68f),
                        cornerRadius = CornerRadius(3.dp.toPx()),
                        style = Stroke(width = stroke)
                    )

                    drawLine(
                        color = primary,
                        start = Offset(size.width * 0.34f, size.height * 0.38f),
                        end = Offset(size.width * 0.66f, size.height * 0.38f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = primary,
                        start = Offset(size.width * 0.34f, size.height * 0.54f),
                        end = Offset(size.width * 0.60f, size.height * 0.54f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}