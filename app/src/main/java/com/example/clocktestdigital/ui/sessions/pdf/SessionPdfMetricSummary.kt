package com.example.clocktestdigital.ui.sessions.pdf

import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.clocktestdigital.analysis.MetricReviewLevel

internal fun drawPdfMetricSummaryRow(
    canvas: android.graphics.Canvas,
    title: String,
    value: String,
    level: MetricReviewLevel,
    x: Float,
    y: Float,
    width: Float
) {
    val rowHeight = 31f

    val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = level.pdfBackgroundColor()
        isAntiAlias = true
    }

    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 9.5f
        color = android.graphics.Color.rgb(71, 85, 105)
        isAntiAlias = true
    }

    val valuePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 9.2f
        color = android.graphics.Color.rgb(51, 65, 85)
        isAntiAlias = true
    }

    val badgePaint = Paint().apply {
        style = Paint.Style.FILL
        color = level.pdfBadgeColor()
        isAntiAlias = true
    }

    val badgeTextPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 7.6f
        color = level.pdfTextColor()
        isAntiAlias = true
    }

    val rect = RectF(x, y, x + width, y + rowHeight)
    canvas.drawRoundRect(rect, 8f, 8f, backgroundPaint)

    canvas.drawText(title, x + 8f, y + 12f, titlePaint)
    canvas.drawText(value, x + 8f, y + 25f, valuePaint)

    val levelLabel = level.pdfLabelText()
    val badgePaddingX = 7f
    val badgeWidth = badgeTextPaint.measureText(levelLabel) + (badgePaddingX * 2)
    val badgeHeight = 13f
    val badgeX = x + width - badgeWidth - 8f
    val badgeY = y + 6f

    val badgeRect = RectF(
        badgeX,
        badgeY,
        badgeX + badgeWidth,
        badgeY + badgeHeight
    )

    canvas.drawRoundRect(badgeRect, 6.5f, 6.5f, badgePaint)
    canvas.drawText(levelLabel, badgeX + badgePaddingX, badgeY + 9.4f, badgeTextPaint)
}

private fun MetricReviewLevel.pdfLabelText(): String {
    return when (this) {
        MetricReviewLevel.LOW -> "Sin destacar"
        MetricReviewLevel.MODERATE -> "Revisar"
        MetricReviewLevel.HIGH -> "Prioritario"
    }
}

private fun MetricReviewLevel.pdfBackgroundColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(240, 253, 244)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(255, 247, 237)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(254, 242, 242)
    }
}

private fun MetricReviewLevel.pdfBadgeColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(220, 252, 231)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(255, 237, 213)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(254, 226, 226)
    }
}

private fun MetricReviewLevel.pdfTextColor(): Int {
    return when (this) {
        MetricReviewLevel.LOW -> android.graphics.Color.rgb(22, 101, 52)
        MetricReviewLevel.MODERATE -> android.graphics.Color.rgb(180, 83, 9)
        MetricReviewLevel.HIGH -> android.graphics.Color.rgb(185, 28, 28)
    }
}