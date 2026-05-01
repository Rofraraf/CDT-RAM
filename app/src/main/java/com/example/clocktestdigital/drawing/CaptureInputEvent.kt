package com.example.clocktestdigital.drawing

data class CapturedInputEvent(
    val eventType: String,
    val x: Float,
    val y: Float,
    val pressure: Float?,
    val eventTimeMs: Long,
    val relativeTimeMs: Long?,
    val strokeIndex: Int?,
    val isHoverEvent: Boolean = false
)