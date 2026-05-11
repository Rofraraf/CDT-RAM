package com.example.clocktestdigital.ui.history

import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.sessions.SessionExecutionMetrics

data class PatientHistoryAnalysisItem(
    val session: TestSessionEntity,
    val executionMetrics: SessionExecutionMetrics
)

data class PatientHistorySummary(
    val totalSessions: Int,
    val reviewedSessions: Int,
    val pendingSessions: Int,
    val validSessions: Int,
    val invalidSessions: Int,
    val averageExecutionTimeSeconds: Float,
    val averageStrokeCount: Float,
    val averagePressure: Float,
    val averageSpeedMmPerSec: Float,
    val averagePauseCount: Float,
    val averagePauseTimeMs: Float,
    val averageHoverPercentage: Float,
    val averageHoverTimeMs: Float
)

data class PatientHistoryTrendPoint(
    val label: String,
    val session: TestSessionEntity,
    val pauseTimeSeconds: Float,
    val hoverPercentage: Float,
    val hoverTimeSeconds: Float,
    val executionTimeSeconds: Float,
    val strokeCount: Float
)

fun calculatePatientHistorySummary(
    items: List<PatientHistoryAnalysisItem>
): PatientHistorySummary {
    val totalSessions = items.size

    if (items.isEmpty()) {
        return PatientHistorySummary(
            totalSessions = 0,
            reviewedSessions = 0,
            pendingSessions = 0,
            validSessions = 0,
            invalidSessions = 0,
            averageExecutionTimeSeconds = 0f,
            averageStrokeCount = 0f,
            averagePressure = 0f,
            averageSpeedMmPerSec = 0f,
            averagePauseCount = 0f,
            averagePauseTimeMs = 0f,
            averageHoverPercentage = 0f,
            averageHoverTimeMs = 0f
        )
    }

    return PatientHistorySummary(
        totalSessions = totalSessions,
        reviewedSessions = items.count { it.session.isReviewed },
        pendingSessions = items.count { !it.session.isReviewed },
        validSessions = items.count { it.session.isValidTest == true },
        invalidSessions = items.count { it.session.isValidTest == false },
        averageExecutionTimeSeconds = items
            .map { it.session.executionTimeSeconds.toFloat() }
            .average()
            .toFloat(),
        averageStrokeCount = items
            .map { it.session.strokeCount.toFloat() }
            .average()
            .toFloat(),
        averagePressure = items
            .map { it.session.averagePressure.toFloat() }
            .average()
            .toFloat(),
        averageSpeedMmPerSec = items
            .map { it.session.averageSpeedMmPerSec.toFloat() }
            .average()
            .toFloat(),
        averagePauseCount = items
            .map { it.session.pauseCount.toFloat() }
            .average()
            .toFloat(),
        averagePauseTimeMs = items
            .map { it.session.totalPauseTimeMs.toFloat() }
            .average()
            .toFloat(),
        averageHoverPercentage = items
            .map { it.executionMetrics.hoverPercentageOfSession }
            .average()
            .toFloat(),
        averageHoverTimeMs = items
            .map { it.executionMetrics.totalHoverTimeMs.toFloat() }
            .average()
            .toFloat()
    )
}

fun buildPatientHistoryTrendPoints(
    items: List<PatientHistoryAnalysisItem>
): List<PatientHistoryTrendPoint> {
    return items
        .sortedBy { it.session.testDateTime }
        .mapIndexed { index, item ->
            PatientHistoryTrendPoint(
                label = "S${index + 1}",
                session = item.session,
                pauseTimeSeconds = item.session.totalPauseTimeMs / 1000f,
                hoverPercentage = item.executionMetrics.hoverPercentageOfSession,
                hoverTimeSeconds = item.executionMetrics.totalHoverTimeMs / 1000f,
                executionTimeSeconds = item.session.executionTimeSeconds.toFloat(),
                strokeCount = item.session.strokeCount.toFloat()
            )
        }
}