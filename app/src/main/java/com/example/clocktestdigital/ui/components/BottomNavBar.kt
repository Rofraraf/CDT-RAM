package com.example.clocktestdigital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Inicio",
                    icon = Icons.Outlined.Home,
                    selected = currentScreen == "home",
                    onClick = { onNavigate("home") },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = "Pacientes",
                    icon = Icons.Outlined.Groups,
                    selected = currentScreen == "patients" ||
                            currentScreen == "history" ||
                            currentScreen == "session_review" ||
                            currentScreen == "compare_sessions",
                    onClick = { onNavigate("patients") },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = "Nuevo",
                    icon = Icons.Outlined.AddCircle,
                    selected = currentScreen == "new_patient",
                    onClick = { onNavigate("new_patient") },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = "Test",
                    icon = Icons.Outlined.Schedule,
                    selected = currentScreen == "test",
                    onClick = { onNavigate("test") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        Color(0xFFEAF2FF)
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(0xFF64748B)
    }

    Column(
        modifier = modifier
            .height(60.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = contentColor
        )

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor
        )
    }
}