package com.example.clocktestdigital.ui.history.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface

fun drawBox(
    canvas: Canvas,
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

fun drawDivider(
    canvas: Canvas,
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

fun drawMetricRow(
    canvas: Canvas,
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

fun drawWrappedText(
    canvas: Canvas,
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

fun textPaint(
    size: Float,
    bold: Boolean,
    color: Int
): Paint {
    return Paint().apply {
        typeface = if (bold) {
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        } else {
            Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        textSize = size
        this.color = color
        isAntiAlias = true
    }
}