package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.clocktestdigital.data.local.TestSessionEntity
import java.util.Locale

const val HISTORY_ROWS_PER_PAGE = 24

fun drawSessionsTablePage(
    canvas: Canvas,
    sessions: List<TestSessionEntity>,
    x: Float,
    y: Float,
    width: Float,
    startIndex: Int,
    totalSessions: Int,
    labelPaint: Paint,
    valuePaint: Paint,
    smallPaint: Paint,
    borderPaint: Paint
): Float {
    if (sessions.isEmpty()) {
        canvas.drawText("No hay sesiones registradas.", x, y, valuePaint)
        return y + 18f
    }

    val rowHeight = 20f
    val tableHeight = rowHeight * (sessions.size + 1)

    val headerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = android.graphics.Color.rgb(241, 245, 249)
        isAntiAlias = true
    }

    val rowLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 0.8f
        color = android.graphics.Color.rgb(226, 232, 240)
        isAntiAlias = true
    }

    canvas.drawRect(RectF(x, y, x + width, y + tableHeight), borderPaint)
    canvas.drawRect(RectF(x, y, x + width, y + rowHeight), headerPaint)

    val colDate = x + 8f
    val colTime = x + 78f
    val colStrokes = x + 130f
    val colPressure = x + 178f
    val colSpeed = x + 238f
    val colPauses = x + 305f
    val colPauseTime = x + 355f
    val colState = x + 425f

    var currentY = y + 14f

    canvas.drawText("Fecha", colDate, currentY, labelPaint)
    canvas.drawText("Tiempo", colTime, currentY, labelPaint)
    canvas.drawText("Trazos", colStrokes, currentY, labelPaint)
    canvas.drawText("Presión", colPressure, currentY, labelPaint)
    canvas.drawText("Vel.", colSpeed, currentY, labelPaint)
    canvas.drawText("Pausas", colPauses, currentY, labelPaint)
    canvas.drawText("T. pausas", colPauseTime, currentY, labelPaint)
    canvas.drawText("Estado", colState, currentY, labelPaint)

    currentY += rowHeight

    sessions.forEach { session ->
        canvas.drawLine(x, currentY - 14f, x + width, currentY - 14f, rowLinePaint)

        canvas.drawText(formatShortDate(session.testDateTime), colDate, currentY, valuePaint)
        canvas.drawText(formatSeconds(session.executionTimeSeconds), colTime, currentY, valuePaint)
        canvas.drawText(session.strokeCount.toString(), colStrokes, currentY, valuePaint)

        canvas.drawText(
            String.format(Locale.getDefault(), "%.2f", session.averagePressure),
            colPressure,
            currentY,
            valuePaint
        )

        canvas.drawText(
            String.format(Locale.getDefault(), "%.1f", session.averageSpeedMmPerSec),
            colSpeed,
            currentY,
            valuePaint
        )

        canvas.drawText(session.pauseCount.toString(), colPauses, currentY, valuePaint)

        canvas.drawText(
            formatMilliseconds(session.totalPauseTimeMs),
            colPauseTime,
            currentY,
            valuePaint
        )

        canvas.drawText(
            if (session.isReviewed) "Revisada" else "Pendiente",
            colState,
            currentY,
            valuePaint
        )

        currentY += rowHeight
    }

    val endIndex = startIndex + sessions.size - 1

    canvas.drawText(
        "Sesiones $startIndex-$endIndex de $totalSessions.",
        x,
        y + tableHeight + 15f,
        smallPaint
    )

    return y + tableHeight + 30f
}

private fun formatMilliseconds(milliseconds: Long): String {
    return String.format(Locale.getDefault(), "%.1f s", milliseconds / 1000f)
}