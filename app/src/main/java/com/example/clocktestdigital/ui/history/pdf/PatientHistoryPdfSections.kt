package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Canvas
import android.graphics.Paint
import com.example.clocktestdigital.ui.history.PatientHistorySummary
import java.util.Locale

fun drawHistoryHeader(
    canvas: Canvas,
    pageWidth: Int,
    margin: Float,
    y: Float,
    contentWidth: Float,
    titlePaint: Paint,
    subtitlePaint: Paint
): Float {
    var currentY = y

    canvas.drawText("CDT | RAM", margin, currentY, titlePaint)
    currentY += 18f

    canvas.drawText(
        "Informe de historial del paciente",
        margin,
        currentY,
        subtitlePaint
    )

    canvas.drawText(
        "Generado: ${formatDate(System.currentTimeMillis())}",
        pageWidth - margin - 145f,
        currentY,
        subtitlePaint
    )

    currentY += 22f

    drawDivider(
        canvas = canvas,
        x = margin,
        y = currentY,
        width = contentWidth
    )

    currentY += 28f

    return currentY
}

fun drawPatientDataSection(
    canvas: Canvas,
    patient: com.example.clocktestdigital.data.local.PatientEntity,
    x: Float,
    y: Float,
    width: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    sectionPaint: Paint,
    labelPaint: Paint,
    valuePaint: Paint
): Float {
    var currentY = y

    canvas.drawText("Datos del paciente", x, currentY, sectionPaint)
    currentY += 16f

    drawBox(
        canvas = canvas,
        x = x,
        y = currentY,
        width = width,
        height = 88f,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    var leftY = currentY + 21f

    leftY = drawMetricRow(
        canvas,
        "Código",
        patient.patientCode,
        x + 16f,
        leftY,
        112f,
        labelPaint,
        valuePaint
    )

    leftY = drawMetricRow(
        canvas,
        "Alias",
        patient.displayName ?: "Sin completar",
        x + 16f,
        leftY,
        112f,
        labelPaint,
        valuePaint
    )

    drawMetricRow(
        canvas,
        "Historia clínica",
        patient.clinicalRecordId ?: "Sin asignar",
        x + 16f,
        leftY,
        112f,
        labelPaint,
        valuePaint
    )

    var rightY = currentY + 21f

    rightY = drawMetricRow(
        canvas,
        "Año nacimiento",
        patient.birthYear?.toString() ?: "Sin completar",
        x + 305f,
        rightY,
        105f,
        labelPaint,
        valuePaint
    )

    drawMetricRow(
        canvas,
        "Sexo",
        patient.sex ?: "Sin completar",
        x + 305f,
        rightY,
        105f,
        labelPaint,
        valuePaint
    )

    return currentY + 116f
}
fun drawGeneralSummarySection(
    canvas: Canvas,
    summary: PatientHistorySummary,
    x: Float,
    y: Float,
    width: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    sectionPaint: Paint,
    labelPaint: Paint,
    valuePaint: Paint
): Float {
    var currentY = y

    canvas.drawText("Resumen general", x, currentY, sectionPaint)
    currentY += 16f

    drawBox(
        canvas = canvas,
        x = x,
        y = currentY,
        width = width,
        height = 72f,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    var summaryLeftY = currentY + 21f

    summaryLeftY = drawMetricRow(
        canvas,
        "Total sesiones",
        summary.totalSessions.toString(),
        x + 16f,
        summaryLeftY,
        112f,
        labelPaint,
        valuePaint
    )

    summaryLeftY = drawMetricRow(
        canvas,
        "Revisadas",
        summary.reviewedSessions.toString(),
        x + 16f,
        summaryLeftY,
        112f,
        labelPaint,
        valuePaint
    )

    drawMetricRow(
        canvas,
        "Pendientes",
        summary.pendingSessions.toString(),
        x + 16f,
        summaryLeftY,
        112f,
        labelPaint,
        valuePaint
    )

    var summaryRightY = currentY + 21f

    summaryRightY = drawMetricRow(
        canvas,
        "Pruebas válidas",
        summary.validSessions.toString(),
        x + 305f,
        summaryRightY,
        105f,
        labelPaint,
        valuePaint
    )

    drawMetricRow(
        canvas,
        "No válidas",
        summary.invalidSessions.toString(),
        x + 305f,
        summaryRightY,
        105f,
        labelPaint,
        valuePaint
    )

    return currentY + 102f
}
fun drawHistoryMetricsSummarySection(
    canvas: Canvas,
    summary: PatientHistorySummary,
    x: Float,
    y: Float,
    width: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    sectionPaint: Paint,
    labelPaint: Paint,
    valuePaint: Paint
): Float {
    var currentY = y

    canvas.drawText("Resumen de métricas del historial", x, currentY, sectionPaint)
    currentY += 16f

    drawBox(
        canvas = canvas,
        x = x,
        y = currentY,
        width = width,
        height = 96f,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    var leftY = currentY + 22f

    leftY = drawMetricRow(
        canvas,
        "Duración media",
        formatAverageSeconds(summary.averageExecutionTimeSeconds),
        x + 16f,
        leftY,
        122f,
        labelPaint,
        valuePaint
    )

    leftY = drawMetricRow(
        canvas,
        "Trazos medios",
        String.format(Locale.getDefault(), "%.1f", summary.averageStrokeCount),
        x + 16f,
        leftY,
        122f,
        labelPaint,
        valuePaint
    )

    leftY = drawMetricRow(
        canvas,
        "Presión media",
        String.format(Locale.getDefault(), "%.2f", summary.averagePressure),
        x + 16f,
        leftY,
        122f,
        labelPaint,
        valuePaint
    )

    drawMetricRow(
        canvas,
        "Velocidad media",
        String.format(Locale.getDefault(), "%.1f mm/s", summary.averageSpeedMmPerSec),
        x + 16f,
        leftY,
        122f,
        labelPaint,
        valuePaint
    )

    var rightY = currentY + 22f

    rightY = drawMetricRow(
        canvas,
        "Pausas medias",
        String.format(Locale.getDefault(), "%.1f", summary.averagePauseCount),
        x + 300f,
        rightY,
        122f,
        labelPaint,
        valuePaint
    )

    rightY = drawMetricRow(
        canvas,
        "T. medio pausas",
        formatMillisecondsFromFloat(summary.averagePauseTimeMs),
        x + 300f,
        rightY,
        122f,
        labelPaint,
        valuePaint
    )

    rightY = drawMetricRow(
        canvas,
        "Hover medio",
        String.format(Locale.getDefault(), "%.1f %%", summary.averageHoverPercentage),
        x + 300f,
        rightY,
        122f,
        labelPaint,
        valuePaint
    )

    drawMetricRow(
        canvas,
        "T. medio hover",
        formatMillisecondsFromFloat(summary.averageHoverTimeMs),
        x + 300f,
        rightY,
        122f,
        labelPaint,
        valuePaint
    )

    return currentY + 126f
}