package com.example.clocktestdigital.analysis

import com.example.clocktestdigital.data.local.TestSessionEntity
import java.util.Locale

enum class MetricReviewLevel {
    LOW,
    MODERATE,
    HIGH
}

data class MetricInterpretation(
    val title: String,
    val valueText: String,
    val level: MetricReviewLevel,
    val levelText: String,
    val technicalReading: String,
    val recommendation: String
)

fun buildSessionMetricInterpretations(
    session: TestSessionEntity,
    metrics: SessionExecutionMetrics
): List<MetricInterpretation> {
    return listOf(
        interpretExecutionDuration(session),
        interpretStrokeCount(session),
        interpretPauseCount(session),
        interpretPauseTime(session),
        interpretAverageSpeed(session),
        interpretAveragePressure(session),
        interpretHoverTime(metrics),
        interpretHoverPercentage(metrics),
        interpretHoverSegmentCount(metrics),
        interpretHoverBeforeFirstDraw(metrics)
    )
}

fun buildHistoryMetricInterpretations(
    summary: PatientHistorySummary
): List<MetricInterpretation> {
    return listOf(
        interpretAverageExecutionDuration(summary),
        interpretAverageStrokeCount(summary),
        interpretAveragePauseCount(summary),
        interpretAveragePauseTime(summary),
        interpretAverageSpeed(summary),
        interpretAveragePressure(summary),
        interpretAverageHoverPercentage(summary),
        interpretAverageHoverTime(summary)
    )
}

private fun interpretExecutionDuration(
    session: TestSessionEntity
): MetricInterpretation {
    val durationMs = session.totalSessionTimeMs
        ?: (session.executionTimeSeconds * 1000L)

    val level = reviewExecutionDuration(durationMs)

    return MetricInterpretation(
        title = "Duración total",
        valueText = formatMillisecondsAsSeconds(durationMs),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La duración total no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La duración total muestra una ejecución algo prolongada o con posibles interrupciones."

            MetricReviewLevel.HIGH ->
                "La duración total elevada puede reflejar una ejecución más lenta o con mayor número de interrupciones."
        },
        recommendation = "Valorar junto con pausas, hover, dibujo final y observaciones profesionales."
    )
}

private fun interpretStrokeCount(
    session: TestSessionEntity
): MetricInterpretation {
    val value = session.strokeCount
    val level = reviewStrokeCount(value)

    val technicalReading = when {
        value <= 3 ->
            "El número de trazos es muy bajo. Puede orientar la revisión de una ejecución incompleta, muy simplificada o no ajustada a la consigna."

        value <= 7 ->
            "El número de trazos es reducido. Conviene revisar si el dibujo final incluye los elementos principales del reloj y si la ejecución se ajusta a la consigna."

        value <= 20 ->
            "El número de trazos no destaca dentro de los criterios técnicos del prototipo."

        value <= 35 ->
            "El número de trazos puede sugerir cierta fragmentación, repasos o correcciones durante la ejecución."

        else ->
            "Un número elevado de trazos puede sugerir mayor fragmentación, repasos o correcciones durante la tarea."
    }

    val recommendation = when {
        value <= 7 ->
            "Revisar junto con el dibujo final si aparecen esfera, números y agujas, y valorar si la sesión debe considerarse válida."

        value > 20 ->
            "Revisar junto con el dibujo final, las pausas, la velocidad y las observaciones de la sesión."

        else ->
            "Revisar junto con el dibujo final y las observaciones de la sesión."
    }

    return MetricInterpretation(
        title = "Número de trazos",
        valueText = value.toString(),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = technicalReading,
        recommendation = recommendation
    )
}

private fun interpretPauseCount(
    session: TestSessionEntity
): MetricInterpretation {
    val value = session.pauseCount
    val level = reviewPauseCount(value)

    return MetricInterpretation(
        title = "Número de pausas",
        valueText = value.toString(),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El número de pausas no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La presencia de varias pausas puede indicar una ejecución más fragmentada."

            MetricReviewLevel.HIGH ->
                "Un mayor número de pausas puede reflejar una ejecución claramente fragmentada."
        },
        recommendation = "Valorar junto con duración total, tiempo de pausa y hover."
    )
}

private fun interpretPauseTime(
    session: TestSessionEntity
): MetricInterpretation {
    val value = session.totalPauseTimeMs
    val level = reviewPauseTime(value)

    return MetricInterpretation(
        title = "Tiempo total de pausas",
        valueText = formatMillisecondsAsSeconds(value),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El tiempo total de pausas no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "Las pausas acumuladas pueden señalar momentos de detención o planificación durante la tarea."

            MetricReviewLevel.HIGH ->
                "Las pausas prolongadas pueden señalar detenciones relevantes o mayor tiempo de planificación durante la ejecución."
        },
        recommendation = "Revisar si las pausas se relacionan con dudas, correcciones o interrupciones observadas."
    )
}

private fun interpretAverageSpeed(
    session: TestSessionEntity
): MetricInterpretation {
    return buildAverageSpeedInterpretation(
        value = session.averageSpeedMmPerSec,
        title = "Velocidad media"
    )
}

private fun interpretAveragePressure(
    session: TestSessionEntity
): MetricInterpretation {
    return buildAveragePressureInterpretation(
        value = session.averagePressure,
        title = "Presión media relativa"
    )
}

private fun interpretHoverTime(
    metrics: SessionExecutionMetrics
): MetricInterpretation {
    val value = metrics.totalHoverTimeMs
    val level = reviewHoverTime(value)

    return MetricInterpretation(
        title = "Tiempo total hover",
        valueText = formatMillisecondsAsSeconds(value),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El tiempo total hover no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "El tiempo total hover tiene una presencia relevante en la sesión."

            MetricReviewLevel.HIGH ->
                "Un tiempo total hover prolongado puede orientar la revisión de posibles momentos de planificación, detención o preparación durante la tarea."
        },
        recommendation = "Valorar junto con proporción hover/sesión, segmentos hover, pausas y dibujo final."
    )
}

private fun interpretHoverPercentage(
    metrics: SessionExecutionMetrics
): MetricInterpretation {
    val value = metrics.hoverPercentageOfSession
    val level = reviewHoverPercentage(value)

    return MetricInterpretation(
        title = "Proporción hover/sesión",
        valueText = "${formatFloat(value, 1)} %",
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La proporción de hover no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La proporción de hover puede reflejar preparación, reposicionamiento o detenciones previas al trazo."

            MetricReviewLevel.HIGH ->
                "Una proporción elevada de hover puede reflejar mayor tiempo relativo de preparación, reposicionamiento o detención durante la tarea."
        },
        recommendation = "Valorar junto con tiempo total hover, pausas, duración total y dibujo final."
    )
}

private fun interpretHoverSegmentCount(
    metrics: SessionExecutionMetrics
): MetricInterpretation {
    val value = metrics.hoverSegmentCount
    val level = reviewHoverSegmentCount(value)

    return MetricInterpretation(
        title = "Segmentos hover",
        valueText = value.toString(),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La actividad hover aparece poco fragmentada en esta sesión."

            MetricReviewLevel.MODERATE ->
                "Los segmentos hover pueden ayudar a localizar momentos de transición o preparación entre partes del dibujo."

            MetricReviewLevel.HIGH ->
                "La presencia de varios segmentos hover puede orientar la revisión de posibles pausas, transiciones, reposicionamientos o momentos de preparación durante la ejecución."
        },
        recommendation = "Revisar junto con el tiempo total hover, la proporción hover/sesión y las pausas."
    )
}

private fun interpretHoverBeforeFirstDraw(
    metrics: SessionExecutionMetrics
): MetricInterpretation {
    val value = metrics.hoverBeforeFirstDrawMs
    val level = reviewHoverBeforeFirstDraw(value)

    return MetricInterpretation(
        title = "Hover antes del primer trazo",
        valueText = formatMillisecondsAsSeconds(value),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El tiempo hover inicial no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "El hover inicial puede aportar información sobre preparación o planificación antes de iniciar el dibujo."

            MetricReviewLevel.HIGH ->
                "Un hover inicial prolongado puede aportar información sobre el tiempo previo de planificación antes del primer trazo."
        },
        recommendation = "Revisar junto con la latencia inicial, el comienzo del dibujo y las observaciones profesionales."
    )
}

private fun interpretAverageExecutionDuration(
    summary: PatientHistorySummary
): MetricInterpretation {
    val durationMs = (summary.averageExecutionTimeSeconds * 1000f).toLong()
    val level = reviewExecutionDuration(durationMs)

    return MetricInterpretation(
        title = "Duración media del historial",
        valueText = formatMillisecondsAsSeconds(durationMs),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La duración media no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La duración media muestra ejecuciones algo prolongadas o con posibles interrupciones."

            MetricReviewLevel.HIGH ->
                "Una duración media elevada puede reflejar ejecuciones más lentas o con mayor número de interrupciones."
        },
        recommendation = "Valorar la evolución junto con pausas, hover y comparación entre sesiones."
    )
}
private fun interpretAverageStrokeCount(
    summary: PatientHistorySummary
): MetricInterpretation {
    val value = summary.averageStrokeCount
    val level = reviewAverageStrokeCount(value)

    val technicalReading = when {
        value <= 3f ->
            "El número medio de trazos es muy bajo. Puede orientar la revisión de ejecuciones incompletas, muy simplificadas o no ajustadas a la consigna."

        value <= 7f ->
            "El número medio de trazos es reducido. Conviene revisar la evolución del dibujo y comprobar si las sesiones incluyen los elementos principales del reloj."

        value <= 20f ->
            "El número medio de trazos no destaca dentro de los criterios técnicos del prototipo."

        value <= 35f ->
            "El número medio de trazos puede sugerir cierta fragmentación en la ejecución."

        else ->
            "Un número medio elevado de trazos puede sugerir mayor fragmentación, repasos o correcciones."
    }

    val recommendation = when {
        value <= 7f ->
            "Revisar la evolución del dibujo, la validez de las sesiones y la comparación entre pruebas."

        value > 20f ->
            "Revisar la evolución del dibujo, las pausas y la comparación entre sesiones."

        else ->
            "Revisar la evolución del dibujo y la comparación entre sesiones."
    }

    return MetricInterpretation(
        title = "Trazos medios",
        valueText = formatFloat(value, 1),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = technicalReading,
        recommendation = recommendation
    )
}

private fun interpretAveragePauseCount(
    summary: PatientHistorySummary
): MetricInterpretation {
    val value = summary.averagePauseCount
    val level = reviewAveragePauseCount(value)

    return MetricInterpretation(
        title = "Pausas medias",
        valueText = formatFloat(value, 1),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El número medio de pausas no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La presencia media de pausas puede indicar una ejecución más fragmentada."

            MetricReviewLevel.HIGH ->
                "Un número medio elevado de pausas puede reflejar ejecuciones claramente fragmentadas."
        },
        recommendation = "Valorar junto con tiempo medio de pausas y proporción de hover."
    )
}

private fun interpretAveragePauseTime(
    summary: PatientHistorySummary
): MetricInterpretation {
    val value = summary.averagePauseTimeMs
    val level = reviewAveragePauseTime(value)

    return MetricInterpretation(
        title = "Tiempo medio de pausas",
        valueText = formatMillisecondsAsSeconds(value.toLong()),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El tiempo medio de pausas no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "El tiempo medio de pausas puede señalar momentos de detención o planificación durante la tarea."

            MetricReviewLevel.HIGH ->
                "Un tiempo medio de pausas elevado puede señalar detenciones relevantes o mayor tiempo de planificación."
        },
        recommendation = "Revisar junto con duración media, hover y observaciones profesionales."
    )
}

private fun interpretAverageSpeed(
    summary: PatientHistorySummary
): MetricInterpretation {
    return buildAverageSpeedInterpretation(
        value = summary.averageSpeedMmPerSec,
        title = "Velocidad media del historial"
    )
}

private fun interpretAveragePressure(
    summary: PatientHistorySummary
): MetricInterpretation {
    return buildAveragePressureInterpretation(
        value = summary.averagePressure,
        title = "Presión media del historial"
    )
}

private fun interpretAverageHoverPercentage(
    summary: PatientHistorySummary
): MetricInterpretation {
    val value = summary.averageHoverPercentage
    val level = reviewHoverPercentage(value)

    return MetricInterpretation(
        title = "Hover medio/sesión",
        valueText = "${formatFloat(value, 1)} %",
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La proporción media de hover no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La proporción media de hover puede reflejar preparación, reposicionamiento o detenciones previas al trazo."

            MetricReviewLevel.HIGH ->
                "Una proporción media elevada de hover puede reflejar mayor tiempo relativo de preparación, reposicionamiento o detención durante las sesiones."
        },
        recommendation = "Valorar la evolución hover/sesión y su relación con pausas, duración y dibujo final."
    )
}

private fun interpretAverageHoverTime(
    summary: PatientHistorySummary
): MetricInterpretation {
    val value = summary.averageHoverTimeMs
    val level = reviewAverageHoverTime(value)

    return MetricInterpretation(
        title = "Tiempo medio hover",
        valueText = formatMillisecondsAsSeconds(value.toLong()),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "El tiempo medio hover no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "El tiempo medio hover puede aportar información sobre preparación o reposicionamiento durante las sesiones."

            MetricReviewLevel.HIGH ->
                "Un tiempo medio hover elevado puede aportar información sobre preparación, reposicionamiento o detenciones frecuentes."
        },
        recommendation = "Valorar junto con la gráfica de evolución hover/sesión y las observaciones profesionales."
    )
}

private fun buildAverageSpeedInterpretation(
    value: Float,
    title: String
): MetricInterpretation {
    val level = reviewSpeed(value)

    return MetricInterpretation(
        title = title,
        valueText = "${formatFloat(value, 1)} mm/s",
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La velocidad media no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "La velocidad media puede asociarse a una ejecución más lenta del trazo."

            MetricReviewLevel.HIGH ->
                "Una velocidad media baja puede reflejar una ejecución lenta o menos fluida del trazo."
        },
        recommendation = "Valorar junto con pausas, duración total, presión y dibujo final."
    )
}

private fun buildAveragePressureInterpretation(
    value: Float,
    title: String
): MetricInterpretation {
    val level = reviewPressure(value)

    return MetricInterpretation(
        title = title,
        valueText = formatFloat(value, 2),
        level = level,
        levelText = level.toLevelText(),
        technicalReading = when (level) {
            MetricReviewLevel.LOW ->
                "La presión media no destaca dentro de los criterios técnicos del prototipo."

            MetricReviewLevel.MODERATE ->
                "Una presión media baja o elevada puede reflejar cambios en la intensidad del trazo."

            MetricReviewLevel.HIGH ->
                "La presión media presenta un valor llamativo y debe revisarse en contexto."
        },
        recommendation = "Interpretar con cautela, ya que la presión puede depender del dispositivo y del stylus utilizado."
    )
}

private fun MetricReviewLevel.toLevelText(): String {
    return when (this) {
        MetricReviewLevel.LOW -> "Sin destacar"
        MetricReviewLevel.MODERATE -> "Revisar en contexto"
        MetricReviewLevel.HIGH -> "Revisión prioritaria"
    }
}

private fun formatMillisecondsAsSeconds(
    milliseconds: Long
): String {
    return String.format(
        Locale.getDefault(),
        "%.1f s",
        milliseconds / 1000f
    )
}

private fun formatFloat(
    value: Float,
    decimals: Int = 1
): String {
    return "%.${decimals}f".format(Locale.getDefault(), value)
}
