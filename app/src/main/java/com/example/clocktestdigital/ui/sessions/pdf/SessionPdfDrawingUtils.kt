package com.example.clocktestdigital.ui.sessions.pdf

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF

internal fun drawRoundedBox(
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

internal fun drawDivider(
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

internal fun drawSectionTitle(
    canvas: android.graphics.Canvas,
    title: String,
    x: Float,
    y: Float,
    paint: Paint
) {
    canvas.drawText(title, x, y, paint)
}

internal fun drawMetricRow(
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

internal fun drawCompactInfoItem(
    canvas: android.graphics.Canvas,
    label: String,
    value: String,
    x: Float,
    y: Float,
    valueOffset: Float,
    maxValueWidth: Float,
    labelPaint: Paint,
    valuePaint: Paint
) {
    canvas.drawText("$label:", x, y, labelPaint)
    canvas.drawText(
        ellipsizeText(
            text = value,
            maxWidth = maxValueWidth,
            paint = valuePaint
        ),
        x + valueOffset,
        y,
        valuePaint
    )
}

internal fun drawCanvasImageBox(
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

internal fun drawWrappedText(
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