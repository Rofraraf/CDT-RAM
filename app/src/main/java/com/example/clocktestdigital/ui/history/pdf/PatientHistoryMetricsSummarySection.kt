package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.clocktestdigital.analysis.MetricReviewLevel
import com.example.clocktestdigital.analysis.PatientHistorySummary
import com.example.clocktestdigital.analysis.reviewAverageHoverTime
import com.example.clocktestdigital.analysis.reviewAveragePauseCount
import com.example.clocktestdigital.analysis.reviewAveragePauseTime
import com.example.clocktestdigital.analysis.reviewAverageStrokeCount
import com.example.clocktestdigital.analysis.reviewExecutionDuration
import com.example.clocktestdigital.analysis.reviewHoverPercentage
import com.example.clocktestdigital.analysis.reviewPressure
import com.example.clocktestdigital.analysis.reviewSpeed
import java.util.Locale

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
        height = 106f,
        backgroundPaint = backgroundPaint,
        borderPaint = borderPaint
    )

    val leftX = x + 14f
    val rightX = x + 284f

    var leftY = currentY + 21f
    var rightY = currentY + 21f

    leftY = drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "Duración media",
        value = formatAverageSeconds(summary.averageExecutionTimeSeconds),
        level = reviewExecutionDuration(
            (summary.averageExecutionTimeSeconds * 1000f).toLong()
        ),
        x = leftX,
        y = leftY,
        width = 232f
    )

    leftY = drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "Trazos medios",
        value = String.format(Locale.getDefault(), "%.1f", summary.averageStrokeCount),
        level = reviewAverageStrokeCount(summary.averageStrokeCount),
        x = leftX,
        y = leftY,
        width = 232f
    )

    leftY = drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "Presión media",
        value = String.format(Locale.getDefault(), "%.2f", summary.averagePressure),
        level = reviewPressure(summary.averagePressure),
        x = leftX,
        y = leftY,
        width = 232f
    )

    drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "Velocidad media",
        value = String.format(Locale.getDefault(), "%.1f mm/s", summary.averageSpeedMmPerSec),
        level = reviewSpeed(summary.averageSpeedMmPerSec),
        x = leftX,
        y = leftY,
        width = 232f
    )

    rightY = drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "Pausas medias",
        value = String.format(Locale.getDefault(), "%.1f", summary.averagePauseCount),
        level = reviewAveragePauseCount(summary.averagePauseCount),
        x = rightX,
        y = rightY,
        width = 214f
    )

    rightY = drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "T. medio pausas",
        value = formatMillisecondsFromFloat(summary.averagePauseTimeMs),
        level = reviewAveragePauseTime(summary.averagePauseTimeMs),
        x = rightX,
        y = rightY,
        width = 214f
    )

    rightY = drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "Hover medio",
        value = String.format(Locale.getDefault(), "%.1f %%", summary.averageHoverPercentage),
        level = reviewHoverPercentage(summary.averageHoverPercentage),
        x = rightX,
        y = rightY,
        width = 214f
    )

    drawHistoryMetricSummaryRow(
        canvas = canvas,
        label = "T. medio hover",
        value = formatMillisecondsFromFloat(summary.averageHoverTimeMs),
        level = reviewAverageHoverTime(summary.averageHoverTimeMs),
        x = rightX,
        y = rightY,
        width = 214f
    )

    return currentY + 132f
}

private fun drawHistoryMetricSummaryRow(
    canvas: Canvas,
    label: String,
    value: String,
    level: MetricReviewLevel,
    x: Float,
    y: Float,
    width: Float
): Float {
    val rowHeight = 17f

    val metricLabelPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 8.2f
        color = android.graphics.Color.rgb(71, 85, 105)
        isAntiAlias = true
    }

    val metricValuePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 8.2f
        color = android.graphics.Color.rgb(51, 65, 85)
        isAntiAlias = true
    }

    val badgePaint = Paint().apply {
        style = Paint.Style.FILL
        color = level.historyMetricBadgeColor()
        isAntiAlias = true
    }

    val badgeTextPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 6.7f
        color = level.historyMetricTextColor()
        isAntiAlias = true
    }

    canvas.drawText(label, x, y, metricLabelPaint)
    canvas.drawText(value, x + 103f, y, metricValuePaint)

    val badgeText = level.historyMetricLabel()
    val badgePaddingX = 5.5f
    val badgeWidth = badgeTextPaint.measureText(badgeText) + (badgePaddingX * 2)
    val badgeHeight = 11.5f
    val badgeX = x + width - badgeWidth
    val badgeY = y - 9f

    val badgeRect = RectF(
        badgeX,
        badgeY,
        badgeX + badgeWidth,
        badgeY + badgeHeight
    )

    canvas.drawRoundRect(badgeRect, 5.5f, 5.5f, badgePaint)

    canvas.drawText(
        badgeText,
        badgeX + badgePaddingX,
        badgeY + 8.2f,
        badgeTextPaint
    )

    return y + rowHeight
}

private fun MetricReviewLevel.historyMetricLabel(): String {
    return when (this) {
        MetricReviewLevel.LOW -> "Normal"
        MetricReviewLevel.MODERATE -> "Revisar"
        MetricReviewLevel.HIGH -> "Prioritario"
    }
}

private fun MetricReviewLevel.historyMetricBadgeColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(220, 252, 231)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(255, 237, 213)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(254, 226, 226)
    }
}

private fun MetricReviewLevel.historyMetricTextColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(22, 101, 52)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(180, 83, 9)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(185, 28, 28)
    }
}