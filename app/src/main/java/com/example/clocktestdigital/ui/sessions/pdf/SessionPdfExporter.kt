package com.example.clocktestdigital.ui.sessions.pdf

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.clocktestdigital.analysis.SessionExecutionMetrics
import com.example.clocktestdigital.analysis.reviewPauseCount
import com.example.clocktestdigital.analysis.reviewPressure
import com.example.clocktestdigital.analysis.reviewSpeed
import com.example.clocktestdigital.analysis.reviewStrokeCount
import com.example.clocktestdigital.data.local.TestSessionEntity
import com.example.clocktestdigital.ui.sessions.buildSessionReviewHoverObservation
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun buildSessionPdfFileName(
    session: TestSessionEntity
): String {
    val formatter = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
    val date = formatter.format(Date(session.testDateTime))

    val safePatientCode = session.patientCode.replace(Regex("[^A-Za-z0-9_-]"), "_")

    return "informe_${safePatientCode}_$date.pdf"
}

fun writeSessionPdf(
    outputStream: OutputStream,
    session: TestSessionEntity,
    executionMetrics: SessionExecutionMetrics,
    patientAlias: String? = null
) {
    val pdfDocument = PdfDocument()

    val pageWidth = 595
    val pageHeight = 842
    val margin = 42f
    val contentWidth = pageWidth - (margin * 2)

    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 21f
        color = Color.rgb(30, 41, 59)
        isAntiAlias = true
    }

    val subtitlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 10.5f
        color = Color.rgb(100, 116, 139)
        isAntiAlias = true
    }

    val sectionPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 13.5f
        color = Color.rgb(30, 41, 59)
        isAntiAlias = true
    }

    val labelPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 10.5f
        color = Color.rgb(71, 85, 105)
        isAntiAlias = true
    }

    val valuePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 10.5f
        color = Color.rgb(51, 65, 85)
        isAntiAlias = true
    }

    val smallPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 9.2f
        color = Color.rgb(100, 116, 139)
        isAntiAlias = true
    }

    val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.2f
        color = Color.rgb(203, 213, 225)
        isAntiAlias = true
    }

    val lightBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.rgb(248, 250, 252)
        isAntiAlias = true
    }

    var y = margin

    // Cabecera
    canvas.drawText("CDT | RAM", margin, y, titlePaint)
    y += 18f

    canvas.drawText(
        "Informe de sesión del Test del Reloj digitalizado mediante stylus",
        margin,
        y,
        subtitlePaint
    )

    canvas.drawText(
        "Generado: ${formatDate(System.currentTimeMillis())}",
        pageWidth - margin - 145f,
        y,
        subtitlePaint
    )

    y += 22f
    drawDivider(canvas, margin, y, contentWidth)
    y += 28f

    // Datos generales
    drawSectionTitle(canvas, "Datos de la sesión", margin, y, sectionPaint)
    y += 14f

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = 54f,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    val aliasText = patientAlias?.takeIf { it.isNotBlank() } ?: "No registrado"

    val generalRow1Y = y + 21f
    val generalRow2Y = y + 41f

    drawCompactInfoItem(
        canvas = canvas,
        label = "Paciente",
        value = session.patientCode,
        x = margin + 14f,
        y = generalRow1Y,
        valueOffset = 58f,
        maxValueWidth = 72f,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    drawCompactInfoItem(
        canvas = canvas,
        label = "Alias",
        value = aliasText,
        x = margin + 150f,
        y = generalRow1Y,
        valueOffset = 42f,
        maxValueWidth = 150f,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    drawCompactInfoItem(
        canvas = canvas,
        label = "Estado",
        value = if (session.isReviewed) "Revisada" else "Pendiente",
        x = margin + 356f,
        y = generalRow1Y,
        valueOffset = 54f,
        maxValueWidth = 86f,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    drawCompactInfoItem(
        canvas = canvas,
        label = "Fecha",
        value = formatDate(session.testDateTime),
        x = margin + 14f,
        y = generalRow2Y,
        valueOffset = 58f,
        maxValueWidth = 175f,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    drawCompactInfoItem(
        canvas = canvas,
        label = "Validez",
        value = formatValidity(session.isValidTest),
        x = margin + 356f,
        y = generalRow2Y,
        valueOffset = 54f,
        maxValueWidth = 86f,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    y += 76f

    // Dibujo + métricas principales
    drawSectionTitle(canvas, "Resultado y métricas principales", margin, y, sectionPaint)
    y += 18f

    val drawingX = margin
    val drawingY = y
    val drawingSize = 205f

    drawCanvasImageBox(
        canvas = canvas,
        imagePath = session.drawingImagePath,
        x = drawingX,
        y = drawingY,
        size = drawingSize,
        labelPaint = labelPaint,
        valuePaint = valuePaint,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    val metricsX = margin + drawingSize + 30f
    val metricsBoxWidth = contentWidth - drawingSize - 30f
    val metricRowWidth = metricsBoxWidth - 28f
    var metricsY = y + 16f

    drawRoundedBox(
        canvas = canvas,
        x = metricsX,
        y = y,
        width = metricsBoxWidth,
        height = drawingSize,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    canvas.drawText("Resumen rápido de métricas", metricsX + 14f, metricsY, sectionPaint)
    metricsY += 21f

    drawPdfMetricSummaryRow(
        canvas = canvas,
        title = "Trazos",
        value = "${session.strokeCount} realizados",
        level = reviewStrokeCount(session.strokeCount),
        x = metricsX + 14f,
        y = metricsY,
        width = metricRowWidth
    )

    metricsY += 39f

    drawPdfMetricSummaryRow(
        canvas = canvas,
        title = "Presión",
        value = "${String.Companion.format(Locale.getDefault(), "%.2f", session.averagePressure)} media relativa",
        level = reviewPressure(session.averagePressure),
        x = metricsX + 14f,
        y = metricsY,
        width = metricRowWidth
    )

    metricsY += 39f

    drawPdfMetricSummaryRow(
        canvas = canvas,
        title = "Velocidad",
        value = "${String.Companion.format(Locale.getDefault(), "%.1f mm/s", session.averageSpeedMmPerSec)} media",
        level = reviewSpeed(session.averageSpeedMmPerSec),
        x = metricsX + 14f,
        y = metricsY,
        width = metricRowWidth
    )

    metricsY += 39f

    drawPdfMetricSummaryRow(
        canvas = canvas,
        title = "Pausas",
        value = "${session.pauseCount} detectadas",
        level = reviewPauseCount(session.pauseCount),
        x = metricsX + 14f,
        y = metricsY,
        width = metricRowWidth
    )

    y += drawingSize + 26f

    // Lectura del comportamiento hover
    drawSectionTitle(canvas, "Lectura del comportamiento hover", margin, y, sectionPaint)
    y += 18f

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = 138f,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    val hoverObservationParts = buildSessionReviewHoverObservation(executionMetrics)
        .split("\n\n")

    val hoverSummary = hoverObservationParts.getOrNull(0)
        ?: "Sin observación hover disponible."

    val hoverReading = hoverObservationParts.getOrNull(1)
        ?: "No se dispone de lectura técnica adicional sobre el comportamiento hover."

    canvas.drawText(
        hoverSummary,
        margin + 16f,
        y + 22f,
        valuePaint
    )

    drawWrappedText(
        canvas = canvas,
        text = hoverReading,
        x = margin + 16f,
        y = y + 43f,
        maxWidth = contentWidth - 32f,
        paint = smallPaint,
        lineHeight = 11.5f
    )

    canvas.drawText(
        "Datos técnicos de soporte",
        margin + 16f,
        y + 93f,
        labelPaint
    )

    canvas.drawText(
        "Eventos: total ${executionMetrics.totalEventCount} · dibujo ${executionMetrics.drawingEventCount} · hover ${executionMetrics.hoverEventCount}",
        margin + 16f,
        y + 111f,
        smallPaint
    )

    canvas.drawText(
        "Segmentos: ${executionMetrics.hoverSegmentCount} · hover inicial ${formatMilliseconds(executionMetrics.hoverBeforeFirstDrawMs)} · media hover ${formatMilliseconds(executionMetrics.averageHoverSegmentTimeMs)}",
        margin + 16f,
        y + 126f,
        smallPaint
    )

    y += 158f

    // Observaciones
    drawSectionTitle(canvas, "Observaciones del profesional", margin, y, sectionPaint)
    y += 18f

    val notes = session.professionalNotes?.takeIf { it.isNotBlank() }
        ?: "Sin observaciones registradas."

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = 54f,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    drawWrappedText(
        canvas = canvas,
        text = notes,
        x = margin + 14f,
        y = y + 20f,
        maxWidth = contentWidth - 28f,
        paint = valuePaint,
        lineHeight = 14f
    )

    y += 74f

    // Aviso
    drawSectionTitle(canvas, "Aviso", margin, y, sectionPaint)
    y += 16f

    drawWrappedText(
        canvas = canvas,
        text = "Este informe procede de un prototipo académico de apoyo al registro y revisión del Test del Reloj. No emite diagnóstico automático y sus resultados deben ser interpretados por personal sanitario cualificado.",
        x = margin,
        y = y,
        maxWidth = contentWidth,
        paint = smallPaint,
        lineHeight = 12.5f
    )

    // Footer
    drawDivider(canvas, margin, pageHeight - 38f, contentWidth)

    canvas.drawText(
        "CDT | RAM · Prototipo académico · No diagnóstico automático",
        margin,
        pageHeight - 22f,
        smallPaint
    )

    pdfDocument.finishPage(page)

    pdfDocument.writeTo(outputStream)
    pdfDocument.close()
}

private fun ellipsizeText(
    text: String,
    maxWidth: Float,
    paint: Paint
): String {
    if (paint.measureText(text) <= maxWidth) {
        return text
    }

    val ellipsis = "…"
    var shortenedText = text

    while (
        shortenedText.isNotEmpty() &&
        paint.measureText(shortenedText + ellipsis) > maxWidth
    ) {
        shortenedText = shortenedText.dropLast(1)
    }

    return if (shortenedText.isBlank()) ellipsis else shortenedText + ellipsis
}


