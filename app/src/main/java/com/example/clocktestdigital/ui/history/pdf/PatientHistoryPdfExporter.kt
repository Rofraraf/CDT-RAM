package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.clocktestdigital.data.local.PatientEntity
import com.example.clocktestdigital.ui.history.PatientHistoryAnalysisItem
import com.example.clocktestdigital.ui.history.calculatePatientHistorySummary
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun buildPatientHistoryPdfFileName(
    patient: PatientEntity
): String {
    val formatter = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
    val date = formatter.format(Date(System.currentTimeMillis()))
    val safePatientCode = patient.patientCode.replace(Regex("[^A-Za-z0-9_-]"), "_")

    return "historial_${safePatientCode}_$date.pdf"
}

fun writePatientHistoryPdf(
    outputStream: OutputStream,
    patient: PatientEntity,
    analysisItems: List<PatientHistoryAnalysisItem>
) {
    val pdfDocument = PdfDocument()

    val sessions = analysisItems.map { it.session }
    val summary = calculatePatientHistorySummary(analysisItems)

    val pageWidth = 595
    val pageHeight = 842
    val margin = 42f
    val contentWidth = pageWidth - (margin * 2)

    var pageNumber = 1
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas

    val titlePaint = textPaint(
        size = 21f,
        bold = true,
        color = android.graphics.Color.rgb(30, 41, 59)
    )

    val subtitlePaint = textPaint(
        size = 10.5f,
        bold = false,
        color = android.graphics.Color.rgb(100, 116, 139)
    )

    val sectionPaint = textPaint(
        size = 13.5f,
        bold = true,
        color = android.graphics.Color.rgb(30, 41, 59)
    )

    val labelPaint = textPaint(
        size = 10.2f,
        bold = true,
        color = android.graphics.Color.rgb(71, 85, 105)
    )

    val valuePaint = textPaint(
        size = 10.2f,
        bold = false,
        color = android.graphics.Color.rgb(51, 65, 85)
    )

    val smallPaint = textPaint(
        size = 9f,
        bold = false,
        color = android.graphics.Color.rgb(100, 116, 139)
    )

    val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.1f
        color = android.graphics.Color.rgb(203, 213, 225)
        isAntiAlias = true
    }

    val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = android.graphics.Color.rgb(248, 250, 252)
        isAntiAlias = true
    }

    var y = margin

    y = drawHistoryHeader(
        canvas = canvas,
        pageWidth = pageWidth,
        margin = margin,
        y = y,
        contentWidth = contentWidth,
        titlePaint = titlePaint,
        subtitlePaint = subtitlePaint
    )

    y = drawPatientDataSection(
        canvas = canvas,
        patient = patient,
        x = margin,
        y = y,
        width = contentWidth,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint,
        sectionPaint = sectionPaint,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    y = drawGeneralSummarySection(
        canvas = canvas,
        summary = summary,
        x = margin,
        y = y,
        width = contentWidth,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint,
        sectionPaint = sectionPaint,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    y = drawHistoryMetricsSummarySection(
        canvas = canvas,
        summary = summary,
        x = margin,
        y = y,
        width = contentWidth,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint,
        sectionPaint = sectionPaint,
        labelPaint = labelPaint,
        valuePaint = valuePaint
    )

    canvas.drawText("Evolución hover/sesión", margin, y, sectionPaint)
    y += 16f

    drawHoverChart(
        canvas = canvas,
        analysisItems = analysisItems,
        x = margin,
        y = y,
        width = contentWidth,
        height = 160f,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint,
        labelPaint = labelPaint,
        smallPaint = smallPaint
    )

    // Footer página 1
    drawDivider(canvas, margin, pageHeight - 38f, contentWidth)

    canvas.drawText(
        "CDT | RAM · Informe de historial · Página 1 · Prototipo académico · No diagnóstico automático",
        margin,
        pageHeight - 22f,
        smallPaint
    )

    pdfDocument.finishPage(page)

    // Páginas de historial de sesiones
    val sortedSessions = sessions.sortedByDescending { it.testDateTime }
    val sessionPages = sortedSessions.chunked(HISTORY_ROWS_PER_PAGE)

    if (sessionPages.isEmpty()) {
        pageNumber += 1
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas

        y = margin

        y = drawHistoryHeader(
            canvas = canvas,
            pageWidth = pageWidth,
            margin = margin,
            y = y,
            contentWidth = contentWidth,
            titlePaint = titlePaint,
            subtitlePaint = subtitlePaint
        )

        canvas.drawText("Historial de sesiones", margin, y, sectionPaint)
        y += 18f

        canvas.drawText(
            "No hay sesiones registradas.",
            margin,
            y,
            valuePaint
        )

        y += 30f

        canvas.drawText("Aviso", margin, y, sectionPaint)
        y += 15f

        drawWrappedText(
            canvas = canvas,
            text = "Este informe procede de un prototipo académico de apoyo al registro y revisión del Test del Reloj. No emite diagnóstico automático y sus resultados deben ser interpretados por personal sanitario cualificado.",
            x = margin,
            y = y,
            maxWidth = contentWidth,
            paint = smallPaint,
            lineHeight = 12.5f
        )

        drawDivider(canvas, margin, pageHeight - 38f, contentWidth)

        canvas.drawText(
            "CDT | RAM · Informe de historial · Página $pageNumber · Prototipo académico · No diagnóstico automático",
            margin,
            pageHeight - 22f,
            smallPaint
        )

        pdfDocument.finishPage(page)
    } else {
        sessionPages.forEachIndexed { pageIndex, sessionPage ->
            pageNumber += 1
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas

            y = margin

            y = drawHistoryHeader(
                canvas = canvas,
                pageWidth = pageWidth,
                margin = margin,
                y = y,
                contentWidth = contentWidth,
                titlePaint = titlePaint,
                subtitlePaint = subtitlePaint
            )

            canvas.drawText("Historial de sesiones", margin, y, sectionPaint)
            y += 18f

            y = drawSessionsTablePage(
                canvas = canvas,
                sessions = sessionPage,
                x = margin,
                y = y,
                width = contentWidth,
                startIndex = (pageIndex * HISTORY_ROWS_PER_PAGE) + 1,
                totalSessions = sortedSessions.size,
                labelPaint = labelPaint,
                valuePaint = valuePaint,
                smallPaint = smallPaint,
                borderPaint = borderPaint
            )

            val isLastSessionPage = pageIndex == sessionPages.lastIndex

            if (isLastSessionPage) {
                y += 12f

                canvas.drawText("Aviso", margin, y, sectionPaint)
                y += 15f

                drawWrappedText(
                    canvas = canvas,
                    text = "Este informe procede de un prototipo académico de apoyo al registro y revisión del Test del Reloj. No emite diagnóstico automático y sus resultados deben ser interpretados por personal sanitario cualificado.",
                    x = margin,
                    y = y,
                    maxWidth = contentWidth,
                    paint = smallPaint,
                    lineHeight = 12.5f
                )
            }

            drawDivider(canvas, margin, pageHeight - 38f, contentWidth)

            canvas.drawText(
                "CDT | RAM · Informe de historial · Página $pageNumber · Prototipo académico · No diagnóstico automático",
                margin,
                pageHeight - 22f,
                smallPaint
            )

            pdfDocument.finishPage(page)
        }
    }

    pdfDocument.writeTo(outputStream)
    pdfDocument.close()
}
