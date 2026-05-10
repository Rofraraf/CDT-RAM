package com.example.clocktestdigital.ui.sessions

import com.example.clocktestdigital.data.local.InputEventEntity

data class SessionExecutionMetrics(
    val totalEventCount: Int = 0,
    val drawingEventCount: Int = 0,
    val hoverEventCount: Int = 0,
    val hoverSegmentCount: Int = 0,
    val totalHoverTimeMs: Long = 0,
    val averageHoverSegmentTimeMs: Long = 0,
    val hoverBeforeFirstDrawMs: Long = 0,
    val hoverPercentageOfSession: Float = 0f
)

fun calculateSessionExecutionMetrics(
    events: List<InputEventEntity>,
    sessionDurationMs: Long?
): SessionExecutionMetrics {
    val sortedEvents = events.sortedBy { it.eventTimeMs }

    val hoverEvents = sortedEvents.filter { it.isHoverEvent }
    val drawingEvents = sortedEvents.filter { !it.isHoverEvent }

    val hoverSegments = buildHoverSegments(hoverEvents)
    val totalHoverTimeMs = hoverSegments.sumOf { segment ->
        (segment.second - segment.first).coerceAtLeast(0L)
    }

    val averageHoverSegmentTimeMs = if (hoverSegments.isNotEmpty()) {
        totalHoverTimeMs / hoverSegments.size
    } else {
        0L
    }

    val firstDrawDownTime = sortedEvents
        .firstOrNull { it.eventType == "DRAW_DOWN" }
        ?.eventTimeMs

    val hoverBeforeFirstDrawMs = if (firstDrawDownTime != null) {
        val hoverBeforeDraw = hoverEvents.filter { it.eventTimeMs <= firstDrawDownTime }

        if (hoverBeforeDraw.isNotEmpty()) {
            (firstDrawDownTime - hoverBeforeDraw.first().eventTimeMs).coerceAtLeast(0L)
        } else {
            0L
        }
    } else {
        0L
    }

    val durationMs = sessionDurationMs
        ?: calculateDurationFromEvents(sortedEvents)
        ?: 0L

    val hoverPercentage = if (durationMs > 0) {
        ((totalHoverTimeMs.toFloat() / durationMs.toFloat()) * 100f)
            .coerceIn(0f, 100f)
    } else {
        0f
    }

    return SessionExecutionMetrics(
        totalEventCount = sortedEvents.size,
        drawingEventCount = drawingEvents.size,
        hoverEventCount = hoverEvents.size,
        hoverSegmentCount = hoverSegments.size,
        totalHoverTimeMs = totalHoverTimeMs,
        averageHoverSegmentTimeMs = averageHoverSegmentTimeMs,
        hoverBeforeFirstDrawMs = hoverBeforeFirstDrawMs,
        hoverPercentageOfSession = hoverPercentage
    )
}

private fun buildHoverSegments(
    hoverEvents: List<InputEventEntity>
): List<Pair<Long, Long>> {
    if (hoverEvents.isEmpty()) return emptyList()

    val sortedHoverEvents = hoverEvents.sortedBy { it.eventTimeMs }
    val segments = mutableListOf<Pair<Long, Long>>()

    var segmentStart: Long? = null
    var previousTime: Long? = null

    sortedHoverEvents.forEach { event ->
        val currentTime = event.eventTimeMs
        val gap = previousTime?.let { currentTime - it } ?: 0L

        val shouldStartNewSegment =
            segmentStart == null ||
                    event.eventType == "HOVER_ENTER" ||
                    gap > 750L

        if (shouldStartNewSegment) {
            if (segmentStart != null && previousTime != null && previousTime!! > segmentStart!!) {
                segments.add(segmentStart!! to previousTime!!)
            }

            segmentStart = currentTime
        }

        previousTime = currentTime

        if (event.eventType == "HOVER_EXIT") {
            val start = segmentStart

            if (start != null && currentTime > start) {
                segments.add(start to currentTime)
            }

            segmentStart = null
            previousTime = null
        }
    }

    if (segmentStart != null && previousTime != null && previousTime!! > segmentStart!!) {
        segments.add(segmentStart!! to previousTime!!)
    }

    return segments
}

private fun calculateDurationFromEvents(
    events: List<InputEventEntity>
): Long? {
    if (events.isEmpty()) return null

    val first = events.first().eventTimeMs
    val last = events.last().eventTimeMs

    return (last - first).coerceAtLeast(0L)
}