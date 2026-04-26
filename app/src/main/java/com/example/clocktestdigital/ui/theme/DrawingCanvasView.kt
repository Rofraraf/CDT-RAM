package com.example.clocktestdigital

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.security.KeyStore

data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float,
    val eventTime: Long,
    val action: String
)

class DrawingCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val path = Path()

    val capturedPoints = mutableListOf<StrokePoint>()

    var strokeCount: Int = 0
        private set

    var onStrokeCountChanged: ((Int) -> Unit)? = null
    private var pressureSum: Float = 0f
    private var pressureSamples: Int = 0

    var averagePressure: Float = 0f
        private set
    var onAveragePressureChanged: ((Float) -> Unit)? = null

    var isTestActive: Boolean = false

    private var hasFirstTouchBeenRegistered: Boolean = false
    var onFirstTouchDetected: ((Long) -> Unit)? = null

    private var totalDistanceMm: Float = 0f     /* Distancia recorrida por el stylus mientras dibuja */
    private var totalMoveTimeMs: Long = 0L      /* Tiempo total de movimiento real */

    /* Punto anterior y comparar cuanto se ha movido entre un evento y el siguiente */
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var lastTouchTime: Long = 0L
    private var hasPreviousTouchPoint: Boolean = false /* Indica si existe un punto previo válido para emepzar a calcular */

    private val xdpiValue = resources.displayMetrics.xdpi
    private val ydpiValue = resources.displayMetrics.ydpi
    var averageSpeedPxPerSec: Float = 0f        /* Velocidad media del trazo */
        private set

    var onAverageSpeedChanged: ((Float) -> Unit)? = null

    private val pauseThresholdMs: Long = 800L
    private var lastStrokeEndTime: Long? = null

    var totalPauseTimeMs: Long = 0L
        private set
    var pauseCount: Int = 0
        private set

    var onTotalPauseTimeChanged: ((Long) -> Unit)? = null
    var onPauseCountChanged: ((Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(path, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isTestActive) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        val x = event.x
        val y = event.y
        val pressure = event.pressure
        val time = event.eventTime
        val action = event.actionMasked

        if (action == MotionEvent.ACTION_DOWN && !hasFirstTouchBeenRegistered) {
            hasFirstTouchBeenRegistered = true
            onFirstTouchDetected?.invoke(time)
        }

        val toolType = event.getToolType(0)
        val isStylus = toolType == MotionEvent.TOOL_TYPE_STYLUS ||
                toolType == MotionEvent.TOOL_TYPE_ERASER

        capturedPoints.add(
            StrokePoint(
                x = x,
                y = y,
                pressure = pressure,
                eventTime = time,
                action = actionToString(action)
            )
        )

        if (isStylus && (action == MotionEvent.ACTION_DOWN ||
                    action == MotionEvent.ACTION_MOVE ||
                    action == MotionEvent.ACTION_UP)
        ) {
            pressureSum += pressure
            pressureSamples++

            averagePressure = if (pressureSamples > 0) {
                pressureSum / pressureSamples
            } else {
                0f
            }

            onAveragePressureChanged?.invoke(averagePressure)
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (lastStrokeEndTime != null) {
                    val pauseDuration = time - lastStrokeEndTime!!

                    if (pauseDuration >= pauseThresholdMs) {
                        totalPauseTimeMs += pauseDuration
                        pauseCount++

                        onTotalPauseTimeChanged?.invoke(totalPauseTimeMs)
                        onPauseCountChanged?.invoke(pauseCount)
                    }

                    lastStrokeEndTime = null
                }
                path.moveTo(x, y)
                strokeCount++
                onStrokeCountChanged?.invoke(strokeCount)

                lastTouchX = x
                lastTouchY = y
                lastTouchTime = time
                hasPreviousTouchPoint = true
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)

                if (hasPreviousTouchPoint) {
                    val dxPx = x - lastTouchX
                    val dyPx = y - lastTouchY

                    val dxMm = dxPx * 25.4f / xdpiValue
                    val dyMm = dyPx * 25.4f / ydpiValue

                    val distanceMn = kotlin.math.sqrt(dxMm * dxMm + dyMm * dyMm)
                    val deltaTime = time - lastTouchTime

                    if (deltaTime > 0) {
                        totalDistanceMm += distanceMn
                        totalMoveTimeMs += deltaTime

                        averageSpeedPxPerSec = if (totalMoveTimeMs > 0) {
                            (totalDistanceMm / totalMoveTimeMs) * 1000f
                        } else {
                            0f
                        }

                        onAverageSpeedChanged?.invoke(averageSpeedPxPerSec)
                    }
                }

                lastTouchX = x
                lastTouchY = y
                lastTouchTime = time
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(x, y)

                lastStrokeEndTime = time
                hasPreviousTouchPoint = false

            }
        }

        invalidate()
        return true
    }
    fun clearCanvas() {
        path.reset()
        capturedPoints.clear()

        strokeCount = 0
        onStrokeCountChanged?.invoke(strokeCount)

        pressureSum = 0f
        pressureSamples = 0
        averagePressure = 0f
        onAveragePressureChanged?.invoke(averagePressure)

        totalDistanceMm = 0f
        totalMoveTimeMs = 0L
        averageSpeedPxPerSec = 0f
        onAverageSpeedChanged?.invoke(averageSpeedPxPerSec)

        lastTouchX = 0f
        lastTouchY = 0f
        lastTouchTime = 0L
        hasPreviousTouchPoint = false

        hasFirstTouchBeenRegistered =false
        isTestActive = false

        lastStrokeEndTime = null
        totalPauseTimeMs = 0L
        pauseCount = 0
        onTotalPauseTimeChanged?.invoke(totalPauseTimeMs)
        onPauseCountChanged?.invoke(pauseCount)

        invalidate()
    }

    private fun actionToString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_UP -> "UP"
            else -> "OTHER"
        }
    }
}