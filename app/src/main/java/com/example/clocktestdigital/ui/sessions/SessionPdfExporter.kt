package com.example.clocktestdigital.ui.sessions

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.clocktestdigital.data.local.TestSessionEntity
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
    executionMetrics: SessionExecutionMetrics
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
        color = android.graphics.Color.rgb(30, 41, 59)
        isAntiAlias = true
    }

    val subtitlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 10.5f
        color = android.graphics.Color.rgb(100, 116, 139)
        isAntiAlias = true
    }

    val sectionPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 13.5f
        color = android.graphics.Color.rgb(30, 41, 59)
        isAntiAlias = true
    }

    val labelPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 10.5f
        color = android.graphics.Color.rgb(71, 85, 105)
        isAntiAlias = true
    }

    val valuePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 10.5f
        color = android.graphics.Color.rgb(51, 65, 85)
        isAntiAlias = true
    }

    val smallPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 9.2f
        color = android.graphics.Color.rgb(100, 116, 139)
        isAntiAlias = true
    }

    val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.2f
        color = android.graphics.Color.rgb(203, 213, 225)
        isAntiAlias = true
    }

    val lightBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = android.graphics.Color.rgb(248, 250, 252)
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
    y += 16f

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = 78f,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    var generalY = y + 20f
    generalY = drawMetricRow(canvas, "Paciente", session.patientCode, margin + 16f, generalY, 130f, labelPaint, valuePaint)
    generalY = drawMetricRow(canvas, "Fecha de prueba", formatDate(session.testDateTime), margin + 16f, generalY, 130f, labelPaint, valuePaint)
    generalY = drawMetricRow(canvas, "Estado", if (session.isReviewed) "Revisada" else "Pendiente de revisión", margin + 300f, y + 20f, 90f, labelPaint, valuePaint)
    drawMetricRow(canvas, "Validez", formatValidity(session.isValidTest), margin + 300f, y + 37f, 90f, labelPaint, valuePaint)

    y += 104f

    // Dibujo + métricas principales
    drawSectionTitle(canvas, "Resultado y métricas principales", margin, y, sectionPaint)
    y += 18f

    val drawingX = margin
    val drawingY = y
    val drawingSize = 215f

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

    val metricsX = margin + 245f
    var metricsY = y + 16f

    drawRoundedBox(
        canvas = canvas,
        x = metricsX,
        y = y,
        width = contentWidth - 245f,
        height = drawingSize,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    canvas.drawText("Métricas", metricsX + 14f, metricsY, sectionPaint)
    metricsY += 22f

    metricsY = drawMetricRow(canvas, "Ejecución", formatSeconds(session.executionTimeSeconds), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    metricsY = drawMetricRow(canvas, "Tiempo total", formatMilliseconds(session.totalSessionTimeMs), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    metricsY = drawMetricRow(canvas, "Latencia inicial", formatMilliseconds(session.initialLatencyMs), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    metricsY = drawMetricRow(canvas, "Trazos", session.strokeCount.toString(), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    metricsY = drawMetricRow(canvas, "Presión media", String.format(Locale.getDefault(), "%.2f", session.averagePressure), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    metricsY = drawMetricRow(canvas, "Velocidad", String.format(Locale.getDefault(), "%.1f mm/s", session.averageSpeedMmPerSec), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    metricsY = drawMetricRow(canvas, "Pausas", session.pauseCount.toString(), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)
    drawMetricRow(canvas, "Tiempo pausas", formatMilliseconds(session.totalPauseTimeMs), metricsX + 14f, metricsY, 112f, labelPaint, valuePaint)

    y += drawingSize + 32f

    // Datos de ejecución y hover
    drawSectionTitle(canvas, "Datos de ejecución y hover", margin, y, sectionPaint)
    y += 18f

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = 112f,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

    var hoverLeftY = y + 20f
    hoverLeftY = drawMetricRow(canvas, "Eventos registrados", executionMetrics.totalEventCount.toString(), margin + 16f, hoverLeftY, 130f, labelPaint, valuePaint)
    hoverLeftY = drawMetricRow(canvas, "Eventos de dibujo", executionMetrics.drawingEventCount.toString(), margin + 16f, hoverLeftY, 130f, labelPaint, valuePaint)
    hoverLeftY = drawMetricRow(canvas, "Eventos hover", executionMetrics.hoverEventCount.toString(), margin + 16f, hoverLeftY, 130f, labelPaint, valuePaint)
    drawMetricRow(canvas, "Segmentos hover", executionMetrics.hoverSegmentCount.toString(), margin + 16f, hoverLeftY, 130f, labelPaint, valuePaint)

    var hoverRightY = y + 20f
    hoverRightY = drawMetricRow(canvas, "Tiempo hover", formatMilliseconds(executionMetrics.totalHoverTimeMs), margin + 300f, hoverRightY, 120f, labelPaint, valuePaint)
    hoverRightY = drawMetricRow(canvas, "Media hover", formatMilliseconds(executionMetrics.averageHoverSegmentTimeMs), margin + 300f, hoverRightY, 120f, labelPaint, valuePaint)
    hoverRightY = drawMetricRow(canvas, "Hover inicial", formatMilliseconds(executionMetrics.hoverBeforeFirstDrawMs), margin + 300f, hoverRightY, 120f, labelPaint, valuePaint)
    drawMetricRow(
        canvas,
        "Proporción",
        String.format(Locale.getDefault(), "%.1f %%", executionMetrics.hoverPercentageOfSession),
        margin + 300f,
        hoverRightY,
        120f,
        labelPaint,
        valuePaint
    )

    y += 140f

    // Observaciones
    drawSectionTitle(canvas, "Observaciones profesionales", margin, y, sectionPaint)
    y += 18f

    val notes = session.professionalNotes?.takeIf { it.isNotBlank() }
        ?: "Sin observaciones registradas."

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = 58f,
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

    y += 86f

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

private fun drawRoundedBox(
    canvas: android.graphics.Canvas,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    backgroundPaint: Paint,
    borderPaint: Paint
) {
    val rect = RectF(x, y, x + width, y + height)
    canvas.drawRoundRect(rect, 10f, 10f, backgroundPaint)
    canvas.drawRoundRect(rect, 10f, 10f, borderPaint)
}

private fun drawDivider(
    canvas: android.graphics.Canvas,
    x: Float,
    y: Float,
    width: Float
) {
    val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = android.graphics.Color.rgb(226, 232, 240)
        isAntiAlias = true
    }

    canvas.drawLine(x, y, x + width, y, paint)
}

private fun drawSectionTitle(
    canvas: android.graphics.Canvas,
    title: String,
    x: Float,
    y: Float,
    paint: Paint
) {
    canvas.drawText(title, x, y, paint)
}

private fun drawMetricRow(
    canvas: android.graphics.Canvas,
    label: String,
    value: String,
    x: Float,
    y: Float,
    valueOffset: Float,
    labelPaint: Paint,
    valuePaint: Paint
): Float {
    canvas.drawText("$label:", x, y, labelPaint)
    canvas.drawText(value, x + valueOffset, y, valuePaint)

    return y + 16f
}

private fun drawCanvasImageBox(
    canvas: android.graphics.Canvas,
    imagePath: String?,
    x: Float,
    y: Float,
    size: Float,
    labelPaint: Paint,
    valuePaint: Paint,
    backgroundPaint: Paint,
    borderPaint: Paint
) {
    drawRoundedBox(
        canvas = canvas,
        x = x,
        y = y,
        width = size,
        height = size,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    canvas.drawText("Lienzo digital", x + 12f, y + 18f, labelPaint)

    val imageArea = RectF(
        x + 14f,
        y + 28f,
        x + size - 14f,
        y + size - 14f
    )

    if (imagePath.isNullOrBlank()) {
        canvas.drawText("Sin imagen guardada", imageArea.left, imageArea.top + 24f, valuePaint)
        return
    }

    val bitmap = BitmapFactory.decodeFile(imagePath)

    if (bitmap == null) {
        canvas.drawText("No se pudo cargar el dibujo", imageArea.left, imageArea.top + 24f, valuePaint)
        return
    }

    canvas.drawBitmap(bitmap, null, imageArea, null)
}

private fun drawWrappedText(
    canvas: android.graphics.Canvas,
    text: String,
    x: Float,
    y: Float,
    maxWidth: Float,
    paint: Paint,
    lineHeight: Float
): Float {
    val words = text.split(" ")
    var currentLine = ""
    var currentY = y

    words.forEach { word ->
        val testLine = if (currentLine.isBlank()) word else "$currentLine $word"

        if (paint.measureText(testLine) <= maxWidth) {
            currentLine = testLine
        } else {
            canvas.drawText(currentLine, x, currentY, paint)
            currentY += lineHeight
            currentLine = word
        }
    }

    if (currentLine.isNotBlank()) {
        canvas.drawText(currentLine, x, currentY, paint)
        currentY += lineHeight
    }

    return currentY
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

private fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

private fun formatMilliseconds(milliseconds: Long?): String {
    if (milliseconds == null) return "No registrado"

    return String.format(Locale.getDefault(), "%.1f s", milliseconds / 1000f)
}

private fun formatValidity(isValidTest: Boolean?): String {
    return when (isValidTest) {
        true -> "Válida"
        false -> "No válida"
        null -> "Sin valorar"
    }
}