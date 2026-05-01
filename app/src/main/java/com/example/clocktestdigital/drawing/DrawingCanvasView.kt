package com.example.clocktestdigital.drawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

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

    private val capturedInputEvents = mutableListOf<CapturedInputEvent>()
    private var currentStrokeIndex = 0

    var testStartTimeMs: Long? = null

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

    fun getCapturedInputEvents(): List<CapturedInputEvent> {
        return capturedInputEvents.toList()
    }

    fun clearCapturedInputEvents() {
        capturedInputEvents.clear()
        currentStrokeIndex = 0
        testStartTimeMs = null
    }

    private fun recordDrawingEvent(
        eventType: String,
        event: MotionEvent
    ) {
        val relativeTime = testStartTimeMs?.let { startTime ->
            event.eventTime - startTime
        }

        capturedInputEvents.add(
            CapturedInputEvent(
                eventType = eventType,
                x = event.x,
                y = event.y,
                pressure = event.pressure,
                eventTimeMs = event.eventTime,
                relativeTimeMs = relativeTime,
                strokeIndex = currentStrokeIndex,
                isHoverEvent = false
            )
        )
    }

    private fun recordHoverEvent(
        eventType: String,
        event: MotionEvent
    ) {
        val relativeTime = testStartTimeMs?.let { startTime ->
            event.eventTime - startTime
        }

        capturedInputEvents.add(
            CapturedInputEvent(
                eventType = eventType,
                x = event.x,
                y = event.y,
                pressure = null,
                eventTimeMs = event.eventTime,
                relativeTimeMs = relativeTime,
                strokeIndex = null,
                isHoverEvent = true
            )
        )
    }

    override fun onHoverEvent(event: MotionEvent): Boolean {
        if (!isTestActive) return false

        val toolType = event.getToolType(0)
        val isStylus = toolType == MotionEvent.TOOL_TYPE_STYLUS ||
                toolType == MotionEvent.TOOL_TYPE_ERASER

        if (!isStylus) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_HOVER_ENTER -> {
                recordHoverEvent("HOVER_ENTER", event)
            }

            MotionEvent.ACTION_HOVER_MOVE -> {
                recordHoverEvent("HOVER_MOVE", event)
            }

            MotionEvent.ACTION_HOVER_EXIT -> {
                recordHoverEvent("HOVER_EXIT", event)
            }
        }

        return true
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
                currentStrokeIndex++
                recordDrawingEvent("DRAW_DOWN", event)
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
                recordDrawingEvent("DRAW_MOVE", event)
                path.lineTo(x, y)

                if (hasPreviousTouchPoint) {
                    val dxPx = x - lastTouchX
                    val dyPx = y - lastTouchY

                    val dxMm = dxPx * 25.4f / xdpiValue
                    val dyMm = dyPx * 25.4f / ydpiValue

                    val distanceMn = sqrt(dxMm * dxMm + dyMm * dyMm)
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
                recordDrawingEvent("DRAW_UP", event)
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

    fun exportToBitmap(): Bitmap? {
        if (width <= 0 || height <= 0) {
            return null
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.WHITE)
        draw(canvas)

        return bitmap
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