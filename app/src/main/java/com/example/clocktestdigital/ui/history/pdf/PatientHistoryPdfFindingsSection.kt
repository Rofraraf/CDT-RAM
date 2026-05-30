package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.clocktestdigital.analysis.MetricInterpretation
import com.example.clocktestdigital.analysis.MetricReviewLevel
import com.example.clocktestdigital.analysis.PatientHistorySummary
import com.example.clocktestdigital.analysis.buildHistoryMetricInterpretations

fun drawHistoryTechnicalReadingSection(
    canvas: Canvas,
    summary: PatientHistorySummary,
    x: Float,
    y: Float,
    width: Float,
    backgroundPaint: Paint,
    borderPaint: Paint,
    sectionPaint: Paint,
    labelPaint: Paint,
    smallPaint: Paint
): Float {
    var currentY = y

    canvas.drawText("Hallazgos principales", x, currentY, sectionPaint)
    currentY += 16f

    drawBox(
        canvas = canvas,
        x = x,
        y = currentY,
        width = width,
        height = 100f,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    val interpretations = buildHistoryMetricInterpretations(summary)

    val strokeLevel = findHistoryLevel(
        interpretations = interpretations,
        keyword = "Trazos"
    )

    val pauseLevel = maxMetricLevel(
        first = findHistoryLevel(interpretations, "Pausas medias"),
        second = findHistoryLevel(interpretations, "Tiempo medio de pausas")
    )

    val speedLevel = findHistoryLevel(
        interpretations = interpretations,
        keyword = "Velocidad"
    )

    val hoverLevel = maxMetricLevel(
        first = findHistoryLevel(interpretations, "Hover"),
        second = findHistoryLevel(interpretations, "Proporción")
    )

    val cardGap = 10f
    val cardWidth = (width - 38f) / 2f
    val cardHeight = 35f

    val leftX = x + 14f
    val rightX = leftX + cardWidth + cardGap
    val firstRowY = currentY + 12f
    val secondRowY = firstRowY + cardHeight + 9f

    drawHistoryFindingCard(
        canvas = canvas,
        title = "Trazos",
        level = strokeLevel,
        badgeText = levelBadgeText(strokeLevel),
        description = when (strokeLevel) {
            MetricReviewLevel.LOW ->
                "Trazos medios sin hallazgos destacados."

            MetricReviewLevel.MODERATE ->
                "Trazos medios en rango de revisión; observar continuidad."

            MetricReviewLevel.HIGH ->
                "Trazos medios elevados; revisar repasos o correcciones."
        },
        x = leftX,
        y = firstRowY,
        width = cardWidth,
        height = cardHeight
    )

    drawHistoryFindingCard(
        canvas = canvas,
        title = "Pausas",
        level = pauseLevel,
        badgeText = levelBadgeText(pauseLevel),
        description = when (pauseLevel) {
            MetricReviewLevel.LOW ->
                "Pausas medias sin hallazgos destacados."

            MetricReviewLevel.MODERATE ->
                "Pausas repetidas; revisar interrupciones."

            MetricReviewLevel.HIGH ->
                "Pausas elevadas; revisar detenciones relevantes."
        },
        x = rightX,
        y = firstRowY,
        width = cardWidth,
        height = cardHeight
    )

    drawHistoryFindingCard(
        canvas = canvas,
        title = "Velocidad",
        level = speedLevel,
        badgeText = levelBadgeText(speedLevel),
        description = when (speedLevel) {
            MetricReviewLevel.LOW ->
                "Velocidad media sin hallazgos destacados."

            MetricReviewLevel.MODERATE ->
                "Velocidad reducida; observar ejecución."

            MetricReviewLevel.HIGH ->
                "Velocidad baja; revisar posible enlentecimiento."
        },
        x = leftX,
        y = secondRowY,
        width = cardWidth,
        height = cardHeight
    )

    drawHistoryFindingCard(
        canvas = canvas,
        title = "Hover",
        level = hoverLevel,
        badgeText = when (hoverLevel) {
            MetricReviewLevel.LOW -> "Reducido"
            MetricReviewLevel.MODERATE -> "Intermedio"
            MetricReviewLevel.HIGH -> "Alto"
        },
        description = when (hoverLevel) {
            MetricReviewLevel.LOW ->
                "Hover medio reducido en el historial."

            MetricReviewLevel.MODERATE ->
                "Hover relevante; revisar preparación y transiciones."

            MetricReviewLevel.HIGH ->
                "Hover elevado; revisar planificación o detenciones."
        },
        x = rightX,
        y = secondRowY,
        width = cardWidth,
        height = cardHeight
    )

    return currentY + 120f
}

private fun findHistoryLevel(
    interpretations: List<MetricInterpretation>,
    keyword: String
): MetricReviewLevel {
    return interpretations
        .firstOrNull { interpretation ->
            interpretation.title.contains(keyword, ignoreCase = true)
        }
        ?.level
        ?: MetricReviewLevel.LOW
}

private fun maxMetricLevel(
    first: MetricReviewLevel,
    second: MetricReviewLevel
): MetricReviewLevel {
    return when {
        first == MetricReviewLevel.HIGH || second == MetricReviewLevel.HIGH ->
            MetricReviewLevel.HIGH

        first == MetricReviewLevel.MODERATE || second == MetricReviewLevel.MODERATE ->
            MetricReviewLevel.MODERATE

        else ->
            MetricReviewLevel.LOW
    }
}

private fun levelBadgeText(
    level: MetricReviewLevel
): String {
    return when (level) {
        MetricReviewLevel.LOW -> "Normal"
        MetricReviewLevel.MODERATE -> "Revisar"
        MetricReviewLevel.HIGH -> "Prioritario"
    }
}

private fun drawHistoryFindingCard(
    canvas: Canvas,
    title: String,
    level: MetricReviewLevel,
    badgeText: String,
    description: String,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) {
    val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = level.historyFindingBackgroundColor()
        isAntiAlias = true
    }

    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 8.5f
        color = android.graphics.Color.rgb(30, 41, 59)
        isAntiAlias = true
    }

    val descriptionPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 7.3f
        color = android.graphics.Color.rgb(71, 85, 105)
        isAntiAlias = true
    }

    val badgePaint = Paint().apply {
        style = Paint.Style.FILL
        color = level.historyFindingBadgeColor()
        isAntiAlias = true
    }

    val badgeTextPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 6.7f
        color = level.historyFindingTextColor()
        isAntiAlias = true
    }

    val rect = RectF(x, y, x + width, y + height)
    canvas.drawRoundRect(rect, 8f, 8f, backgroundPaint)

    canvas.drawText(title, x + 8f, y + 11.5f, titlePaint)

    val badgePaddingX = 5.5f
    val badgeWidth = badgeTextPaint.measureText(badgeText) + (badgePaddingX * 2)
    val badgeHeight = 11f
    val badgeX = x + width - badgeWidth - 7f
    val badgeY = y + 5f

    val badgeRect = RectF(
        badgeX,
        badgeY,
        badgeX + badgeWidth,
        badgeY + badgeHeight
    )

    canvas.drawRoundRect(badgeRect, 5.5f, 5.5f, badgePaint)
    canvas.drawText(badgeText, badgeX + badgePaddingX, badgeY + 8f, badgeTextPaint)

    drawWrappedText(
        canvas = canvas,
        text = description,
        x = x + 8f,
        y = y + 25f,
        maxWidth = width - 16f,
        paint = descriptionPaint,
        lineHeight = 8.3f
    )
}

private fun MetricReviewLevel.historyFindingBackgroundColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(240, 253, 244)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(255, 247, 237)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(254, 242, 242)
    }
}

private fun MetricReviewLevel.historyFindingBadgeColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(220, 252, 231)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(255, 237, 213)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(254, 226, 226)
    }
}

private fun MetricReviewLevel.historyFindingTextColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(22, 101, 52)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(180, 83, 9)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(185, 28, 28)
    }
}
