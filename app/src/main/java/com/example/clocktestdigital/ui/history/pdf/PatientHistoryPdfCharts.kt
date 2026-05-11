package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Canvas
import android.graphics.Paint
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.history.PatientHistoryAnalysisItem
import com.example.clocktestdigital.ui.history.buildPatientHistoryTrendPoints
import java.util.Locale

fun drawHoverChart(
    canvas: Canvas,
    analysisItems: List<PatientHistoryAnalysisItem>,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    labelPaint: Paint,
    smallPaint: Paint
) {
    drawBox(canvas, x, y, width, height, backgroundPaint, borderPaint)

    val trendPoints = buildPatientHistoryTrendPoints(analysisItems)

    canvas.drawText(
        "Proporción de tiempo hover respecto a la sesión (%)",
        x + 16f,
        y + 18f,
        labelPaint
    )

    if (trendPoints.isEmpty()) {
        canvas.drawText(
            "No hay sesiones registradas para mostrar evolución hover.",
            x + 16f,
            y + 42f,
            smallPaint
        )
        return
    }

    val chartX = x + 42f
    val chartY = y + 34f
    val chartWidth = width - 72f
    val chartHeight = height - 68f

    val axisPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = android.graphics.Color.rgb(148, 163, 184)
        isAntiAlias = true
    }

    val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.4f
        color = android.graphics.Color.rgb(124, 58, 237)
        isAntiAlias = true
    }

    val pointPaint = Paint().apply {
        style = Paint.Style.FILL
        color = android.graphics.Color.rgb(124, 58, 237)
        isAntiAlias = true
    }

    canvas.drawLine(
        chartX,
        chartY + chartHeight,
        chartX + chartWidth,
        chartY + chartHeight,
        axisPaint
    )

    canvas.drawLine(
        chartX,
        chartY,
        chartX,
        chartY + chartHeight,
        axisPaint
    )

    val maxHoverPercentage = trendPoints.maxOfOrNull { it.hoverPercentage } ?: 0f
    val safeMaxHoverPercentage = if (maxHoverPercentage <= 0f) {
        100f
    } else {
        maxHoverPercentage.coerceAtLeast(10f)
    }

    canvas.drawText(
        String.format(Locale.getDefault(), "%.1f %%", safeMaxHoverPercentage),
        chartX + chartWidth - 42f,
        chartY - 4f,
        smallPaint
    )

    val points = trendPoints.mapIndexed { index, point ->
        val pointX = if (trendPoints.size == 1) {
            chartX + chartWidth / 2f
        } else {
            chartX + (index.toFloat() / (trendPoints.size - 1).toFloat()) * chartWidth
        }

        val ratio = point.hoverPercentage / safeMaxHoverPercentage
        val pointY = chartY + chartHeight - (ratio * chartHeight)

        pointX to pointY
    }

    for (index in 0 until points.size - 1) {
        val current = points[index]
        val next = points[index + 1]

        canvas.drawLine(
            current.first,
            current.second,
            next.first,
            next.second,
            linePaint
        )
    }

    points.forEachIndexed { index, point ->
        canvas.drawCircle(point.first, point.second, 4f, pointPaint)

        canvas.drawText(
            "S${index + 1}",
            point.first - 6f,
            chartY + chartHeight + 15f,
            smallPaint
        )
    }

    canvas.drawText(
        "Cada punto representa una sesión ordenada cronológicamente.",
        x + 16f,
        y + height - 12f,
        smallPaint
    )
}

fun drawPauseChart(
    canvas: Canvas,
    sessions: List<TestSessionEntity>,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    labelPaint: Paint,
    smallPaint: Paint
) {
    drawBox(canvas, x, y, width, height, backgroundPaint, borderPaint)

    val sortedSessions = sessions.sortedBy { it.testDateTime }

    canvas.drawText(
        "Tiempo de pausas acumulado por sesión",
        x + 16f,
        y + 18f,
        labelPaint
    )

    if (sortedSessions.isEmpty()) {
        canvas.drawText(
            "No hay sesiones registradas.",
            x + 16f,
            y + 42f,
            smallPaint
        )
        return
    }

    val chartX = x + 42f
    val chartY = y + 34f
    val chartWidth = width - 72f
    val chartHeight = height - 68f

    val axisPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = android.graphics.Color.rgb(148, 163, 184)
        isAntiAlias = true
    }

    val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.4f
        color = android.graphics.Color.rgb(37, 99, 235)
        isAntiAlias = true
    }

    val pointPaint = Paint().apply {
        style = Paint.Style.FILL
        color = android.graphics.Color.rgb(37, 99, 235)
        isAntiAlias = true
    }

    canvas.drawLine(
        chartX,
        chartY + chartHeight,
        chartX + chartWidth,
        chartY + chartHeight,
        axisPaint
    )

    canvas.drawLine(
        chartX,
        chartY,
        chartX,
        chartY + chartHeight,
        axisPaint
    )

    val maxPauseMs = sortedSessions.maxOfOrNull { it.totalPauseTimeMs } ?: 0L
    val safeMaxPauseMs = if (maxPauseMs <= 0L) 1000L else maxPauseMs

    canvas.drawText(
        String.format(Locale.getDefault(), "%.1f s", safeMaxPauseMs / 1000f),
        chartX + chartWidth - 36f,
        chartY - 4f,
        smallPaint
    )

    val points = sortedSessions.mapIndexed { index, session ->
        val pointX = if (sortedSessions.size == 1) {
            chartX + chartWidth / 2f
        } else {
            chartX + (index.toFloat() / (sortedSessions.size - 1).toFloat()) * chartWidth
        }

        val ratio = session.totalPauseTimeMs.toFloat() / safeMaxPauseMs.toFloat()
        val pointY = chartY + chartHeight - (ratio * chartHeight)

        pointX to pointY
    }

    for (index in 0 until points.size - 1) {
        val current = points[index]
        val next = points[index + 1]

        canvas.drawLine(
            current.first,
            current.second,
            next.first,
            next.second,
            linePaint
        )
    }

    points.forEachIndexed { index, point ->
        canvas.drawCircle(point.first, point.second, 4f, pointPaint)

        canvas.drawText(
            "S${index + 1}",
            point.first - 6f,
            chartY + chartHeight + 15f,
            smallPaint
        )
    }

    canvas.drawText(
        "Cada punto representa una sesión ordenada cronológicamente.",
        x + 16f,
        y + height - 12f,
        smallPaint
    )
}