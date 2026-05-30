package com.example.clocktestdigital.ui.sessions

import com.example.clocktestdigital.analysis.MetricReviewLevel
import com.example.clocktestdigital.analysis.SessionExecutionMetrics
import com.example.clocktestdigital.analysis.reviewHoverBeforeFirstDraw
import com.example.clocktestdigital.analysis.reviewHoverPercentage
import com.example.clocktestdigital.analysis.reviewHoverSegmentCount
import com.example.clocktestdigital.analysis.reviewHoverTime

fun buildSessionReviewHoverObservation(
    metrics: SessionExecutionMetrics
): String {
    if (metrics.hoverEventCount == 0) {
        return "Sin eventos hover registrados en esta sesión.\n\n" +
                "La revisión técnica se apoya principalmente en los eventos de dibujo, pausas, presión y velocidad."
    }

    val hoverTimeLevel = reviewHoverTime(metrics.totalHoverTimeMs)
    val hoverPercentageLevel = reviewHoverPercentage(metrics.hoverPercentageOfSession)
    val hoverSegmentLevel = reviewHoverSegmentCount(metrics.hoverSegmentCount)
    val initialHoverLevel = reviewHoverBeforeFirstDraw(metrics.hoverBeforeFirstDrawMs)

    val summaryText = "${hoverTimeLevel.hoverTimeLabel()} · " +
            "${formatMillisecondsAsSeconds(metrics.totalHoverTimeMs)} en hover · " +
            "${hoverPercentageLevel.hoverPercentageLabel()} (${formatFloat(metrics.hoverPercentageOfSession, 1)} %)."

    val totalHoverObservation = when (hoverTimeLevel) {
        MetricReviewLevel.HIGH -> {
            "El tiempo sin trazo visible es prolongado, por lo que puede orientar la revisión de posibles momentos de planificación, detención o preparación durante la tarea."
        }

        MetricReviewLevel.MODERATE -> {
            "El tiempo sin trazo visible tiene una presencia relevante en la sesión."
        }

        MetricReviewLevel.LOW -> {
            "El tiempo sin trazo visible es reducido en esta sesión."
        }
    }

    val segmentObservation = when (hoverSegmentLevel) {
        MetricReviewLevel.HIGH -> {
            "La presencia de varios segmentos hover puede orientar la revisión de posibles pausas, transiciones, reposicionamientos o momentos de preparación durante la ejecución."
        }

        MetricReviewLevel.MODERATE -> {
            "Los segmentos hover registrados pueden ayudar a localizar momentos de transición o preparación entre partes del dibujo."
        }

        MetricReviewLevel.LOW -> {
            "La actividad hover aparece poco fragmentada, por lo que la revisión puede centrarse principalmente en la continuidad del trazo, las pausas, la presión y la velocidad."
        }
    }

    val initialHoverObservation = when (initialHoverLevel) {
        MetricReviewLevel.MODERATE,
        MetricReviewLevel.HIGH -> {
            "Además, se registra una espera previa antes del primer trazo, dato útil para revisar el inicio de la tarea."
        }

        MetricReviewLevel.LOW -> {
            if (metrics.hoverBeforeFirstDrawMs > 0L) {
                "El hover inicial antes del primer trazo es breve."
            } else {
                "No se registra hover inicial antes del primer trazo."
            }
        }
    }

    return "$summaryText\n\n$totalHoverObservation $segmentObservation $initialHoverObservation"
}

private fun MetricReviewLevel.hoverTimeLabel(): String {
    return when (this) {
        MetricReviewLevel.LOW -> "Hover reducido"
        MetricReviewLevel.MODERATE -> "Hover intermedio"
        MetricReviewLevel.HIGH -> "Hover prolongado"
    }
}

private fun MetricReviewLevel.hoverPercentageLabel(): String {
    return when (this) {
        MetricReviewLevel.LOW -> "peso relativo bajo"
        MetricReviewLevel.MODERATE -> "peso relativo intermedio"
        MetricReviewLevel.HIGH -> "peso relativo alto"
    }
}