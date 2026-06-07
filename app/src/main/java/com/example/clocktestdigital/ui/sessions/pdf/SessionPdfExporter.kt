package com.example.clocktestdigital.ui.sessions.pdf

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.clocktestdigital.analysis.MetricInterpretation
import com.example.clocktestdigital.analysis.MetricReviewLevel
import com.example.clocktestdigital.analysis.SessionExecutionMetrics
import com.example.clocktestdigital.analysis.buildSessionMetricInterpretations
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
    val usableBottom = pageHeight - 58f

    var pageNumber = 1
    var page = pdfDocument.startPage(
        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    )
    var canvas = page.canvas

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

    fun drawFooter() {
        drawDivider(canvas, margin, pageHeight - 38f, contentWidth)

        canvas.drawText(
            "CDT | RAM · Prototipo académico · No diagnóstico automático",
            margin,
            pageHeight - 22f,
            smallPaint
        )

        canvas.drawText(
            "Página $pageNumber",
            pageWidth - margin - 42f,
            pageHeight - 22f,
            smallPaint
        )
    }

    fun finishCurrentPage() {
        drawFooter()
        pdfDocument.finishPage(page)
    }

    fun startNewPage() {
        pageNumber++

        page = pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        )
        canvas = page.canvas

        y = margin

        canvas.drawText("CDT | RAM", margin, y, sectionPaint)
        canvas.drawText(
            "Informe de sesión · continuación",
            margin + 70f,
            y,
            subtitlePaint
        )

        y += 18f
        drawDivider(canvas, margin, y, contentWidth)
        y += 26f
    }

    fun ensureSpace(requiredHeight: Float) {
        if (y + requiredHeight > usableBottom) {
            finishCurrentPage()
            startNewPage()
        }
    }

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

    y += drawingSize + 16f

    // Indicadores técnicos destacados
    val highlightedIndicators = buildSessionMetricInterpretations(
        session = session,
        metrics = executionMetrics
    )
        .filter { interpretation ->
            interpretation.title in pdfHighlightedIndicatorTitles &&
                    interpretation.level != MetricReviewLevel.LOW
        }
        .take(2)

    if (highlightedIndicators.isNotEmpty()) {
        val indicatorsBoxHeight = highlightedIndicatorsBoxHeight(highlightedIndicators.size)

        ensureSpace(16f + indicatorsBoxHeight + 18f)

        drawSectionTitle(canvas, "Indicadores técnicos destacados", margin, y, sectionPaint)
        y += 16f

        y = drawHighlightedIndicatorsBox(
            canvas = canvas,
            indicators = highlightedIndicators,
            x = margin,
            y = y,
            width = contentWidth,
            backgroundPaint = lightBackgroundPaint,
            borderPaint = borderPaint,
            labelPaint = labelPaint,
            valuePaint = valuePaint,
            smallPaint = smallPaint
        )

        y += 18f
    }

    // Lectura del comportamiento hover
    val hoverObservationParts = buildSessionReviewHoverObservation(executionMetrics)
        .split("\n\n")

    val hoverSummary = hoverObservationParts.getOrNull(0)
        ?: "Sin observación hover disponible."

    val hoverReading = hoverObservationParts.getOrNull(1)
        ?: "No se dispone de lectura técnica adicional sobre el comportamiento hover."

    val hoverReadingLines = wrappedLineCount(
        text = hoverReading,
        maxWidth = contentWidth - 32f,
        paint = smallPaint
    )

    val hoverReadingHeight = hoverReadingLines * 11.5f
    val hoverBoxHeight = 94f + hoverReadingHeight

    ensureSpace(18f + hoverBoxHeight + 20f)

    drawSectionTitle(canvas, "Lectura del comportamiento hover", margin, y, sectionPaint)
    y += 18f

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = hoverBoxHeight,
        backgroundPaint = lightBackgroundPaint,
        borderPaint = borderPaint
    )

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

    val hoverSupportY = y + 43f + hoverReadingHeight + 14f

    canvas.drawText(
        "Datos técnicos de soporte",
        margin + 16f,
        hoverSupportY,
        labelPaint
    )

    canvas.drawText(
        "Eventos: total ${executionMetrics.totalEventCount} · dibujo ${executionMetrics.drawingEventCount} · hover ${executionMetrics.hoverEventCount}",
        margin + 16f,
        hoverSupportY + 18f,
        smallPaint
    )

    canvas.drawText(
        "Segmentos: ${executionMetrics.hoverSegmentCount} · hover inicial ${formatMilliseconds(executionMetrics.hoverBeforeFirstDrawMs)} · media hover ${formatMilliseconds(executionMetrics.averageHoverSegmentTimeMs)}",
        margin + 16f,
        hoverSupportY + 33f,
        smallPaint
    )

    y += hoverBoxHeight + 20f

    // Observaciones
    val notes = session.professionalNotes?.takeIf { it.isNotBlank() }
        ?: "Sin observaciones registradas."

    val notesLines = wrappedLineCount(
        text = notes,
        maxWidth = contentWidth - 28f,
        paint = valuePaint
    )

    val notesBoxHeight = maxOf(
        54f,
        28f + (notesLines * 14f)
    )

    ensureSpace(18f + notesBoxHeight + 20f)

    drawSectionTitle(canvas, "Observaciones del profesional", margin, y, sectionPaint)
    y += 18f

    drawRoundedBox(
        canvas = canvas,
        x = margin,
        y = y,
        width = contentWidth,
        height = notesBoxHeight,
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

    y += notesBoxHeight + 20f

    // Aviso
    val warningText = "Este informe procede de un prototipo académico de apoyo al registro y revisión del Test del Reloj. No emite diagnóstico automático y sus resultados deben ser interpretados por personal sanitario cualificado."

    val warningLines = wrappedLineCount(
        text = warningText,
        maxWidth = contentWidth,
        paint = smallPaint
    )

    ensureSpace(16f + (warningLines * 12.5f) + 20f)

    drawSectionTitle(canvas, "Aviso", margin, y, sectionPaint)
    y += 16f

    drawWrappedText(
        canvas = canvas,
        text = warningText,
        x = margin,
        y = y,
        maxWidth = contentWidth,
        paint = smallPaint,
        lineHeight = 12.5f
    )

    finishCurrentPage()

    pdfDocument.writeTo(outputStream)
    pdfDocument.close()
}

private val pdfHighlightedIndicatorTitles = setOf(
    "Número de trazos",
    "Número de pausas",
    "Tiempo total de pausas",
    "Velocidad media",
    "Presión media relativa"
)

private fun highlightedIndicatorsBoxHeight(
    indicatorCount: Int
): Float {
    return 16f + (indicatorCount * 42f)
}

private fun drawHighlightedIndicatorsBox(
    canvas: android.graphics.Canvas,
    indicators: List<MetricInterpretation>,
    x: Float,
    y: Float,
    width: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    labelPaint: Paint,
    valuePaint: Paint,
    smallPaint: Paint
): Float {
    val rowHeight = 42f
    val boxHeight = highlightedIndicatorsBoxHeight(indicators.size)

    drawRoundedBox(
        canvas = canvas,
        x = x,
        y = y,
        width = width,
        height = boxHeight,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    var itemY = y + 18f

    indicators.forEach { indicator ->
        drawHighlightedIndicatorRow(
            canvas = canvas,
            indicator = indicator,
            x = x + 14f,
            y = itemY,
            width = width - 28f,
            labelPaint = labelPaint,
            valuePaint = valuePaint,
            smallPaint = smallPaint
        )

        itemY += rowHeight
    }

    return y + boxHeight
}

private fun drawHighlightedIndicatorRow(
    canvas: android.graphics.Canvas,
    indicator: MetricInterpretation,
    x: Float,
    y: Float,
    width: Float,
    labelPaint: Paint,
    valuePaint: Paint,
    smallPaint: Paint
) {
    canvas.drawText(
        indicator.title,
        x,
        y,
        labelPaint
    )

    canvas.drawText(
        "${indicator.valueText} · ${indicator.levelText}",
        x,
        y + 15f,
        valuePaint
    )

    canvas.drawText(
        ellipsizeText(
            text = indicator.technicalReading,
            maxWidth = width,
            paint = smallPaint
        ),
        x,
        y + 29f,
        smallPaint
    )
}

private fun wrappedLineCount(
    text: String,
    maxWidth: Float,
    paint: Paint
): Int {
    val words = text
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    if (words.isEmpty()) return 1

    var lineCount = 1
    var currentLine = ""

    words.forEach { word ->
        val candidate = if (currentLine.isBlank()) {
            word
        } else {
            "$currentLine $word"
        }

        if (paint.measureText(candidate) <= maxWidth) {
            currentLine = candidate
        } else {
            lineCount++
            currentLine = word
        }
    }

    return lineCount
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