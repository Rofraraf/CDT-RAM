package com.example.clocktestdigital.ui.history

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clocktestdigital.data.local.TestSessionEntity
import java.io.File

@Composable
fun DrawingComparisonCard(
    sessionA: TestSessionEntity,
    sessionB: TestSessionEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Dibujos comparados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Azul: Sesión A · Rojo: Sesión B",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DrawingPreview(
                    title = "Sesión A",
                    titleColor = Color(0xFF2563EB),
                    imagePath = sessionA.drawingImagePath,
                    modifier = Modifier.weight(1f)
                )

                DrawingPreview(
                    title = "Sesión B",
                    titleColor = Color(0xFFDC2626),
                    imagePath = sessionB.drawingImagePath,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DrawingPreview(
    title: String,
    titleColor: Color,
    imagePath: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor
        )

        if (imagePath.isNullOrBlank()) {
            EmptyDrawingPreview(text = "Sin imagen")
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE2E8F0)
                    )
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Dibujo final de $title",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                EmptyDrawingPreview(text = "No se pudo cargar")
            }
        }
    }
}

@Composable
private fun EmptyDrawingPreview(
    text: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE2E8F0)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(10.dp),
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
    }
}