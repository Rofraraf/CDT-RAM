package com.example.clocktestdigital.ui.history.compare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.clocktestdigital.data.local.TestSessionEntity

@Composable
fun CompareSessionSelectorField(
    label: String,
    labelColor: Color,
    sessions: List<TestSessionEntity>,
    selectedSession: TestSessionEntity?,
    onSessionSelected: (TestSessionEntity) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Companion.SemiBold,
                color = labelColor
            )

            Spacer(modifier = Modifier.Companion.height(6.dp))

            Surface(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable { expanded = true },
                shape = RoundedCornerShape(12.dp),
                color = Color.Companion.White,
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFCBD5E1)
                )
            ) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = selectedSession?.let { formatSessionLabel(it) } ?: "Seleccionar",
                        fontSize = 13.sp,
                        color = if (selectedSession == null) {
                            Color(0xFF64748B)
                        } else {
                            Color(0xFF111827)
                        },
                        maxLines = 1,
                        modifier = Modifier.Companion.weight(1f)
                    )

                    Text(
                        text = "▾",
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            sessions.forEach { session ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = formatSessionLabel(session),
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onSessionSelected(session)
                        expanded = false
                    }
                )
            }
        }
    }
}