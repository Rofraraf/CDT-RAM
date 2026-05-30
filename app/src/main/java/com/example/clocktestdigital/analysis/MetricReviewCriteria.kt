package com.example.clocktestdigital.analysis

fun reviewExecutionDuration(
    durationMs: Long
): MetricReviewLevel {
    return when {
        durationMs < 30_000L -> MetricReviewLevel.LOW
        durationMs <= 60_000L -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewStrokeCount(
    strokeCount: Int
): MetricReviewLevel {
    return when {
        strokeCount <= 20 -> MetricReviewLevel.LOW
        strokeCount <= 35 -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewAverageStrokeCount(
    averageStrokeCount: Float
): MetricReviewLevel {
    return when {
        averageStrokeCount <= 20f -> MetricReviewLevel.LOW
        averageStrokeCount <= 35f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewPressure(
    averagePressure: Float
): MetricReviewLevel {
    return when {
        averagePressure < 0.10f -> MetricReviewLevel.MODERATE
        averagePressure > 0.85f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.LOW
    }
}

fun reviewSpeed(
    averageSpeedMmPerSec: Float
): MetricReviewLevel {
    return when {
        averageSpeedMmPerSec < 20f -> MetricReviewLevel.HIGH
        averageSpeedMmPerSec < 40f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.LOW
    }
}

fun reviewPauseCount(
    pauseCount: Int
): MetricReviewLevel {
    return when {
        pauseCount <= 1 -> MetricReviewLevel.LOW
        pauseCount <= 3 -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewAveragePauseCount(
    averagePauseCount: Float
): MetricReviewLevel {
    return when {
        averagePauseCount <= 1f -> MetricReviewLevel.LOW
        averagePauseCount <= 3f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewPauseTime(
    pauseTimeMs: Long
): MetricReviewLevel {
    return when {
        pauseTimeMs < 1_500L -> MetricReviewLevel.LOW
        pauseTimeMs < 5_000L -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewAveragePauseTime(
    averagePauseTimeMs: Float
): MetricReviewLevel {
    return when {
        averagePauseTimeMs < 1_500f -> MetricReviewLevel.LOW
        averagePauseTimeMs < 5_000f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewHoverTime(
    hoverTimeMs: Long
): MetricReviewLevel {
    return when {
        hoverTimeMs < 15_000L -> MetricReviewLevel.LOW
        hoverTimeMs < 35_000L -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewAverageHoverTime(
    averageHoverTimeMs: Float
): MetricReviewLevel {
    return when {
        averageHoverTimeMs < 15_000f -> MetricReviewLevel.LOW
        averageHoverTimeMs < 35_000f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewHoverPercentage(
    hoverPercentage: Float
): MetricReviewLevel {
    return when {
        hoverPercentage < 25f -> MetricReviewLevel.LOW
        hoverPercentage < 60f -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}

fun reviewHoverBeforeFirstDraw(
    hoverBeforeFirstDrawMs: Long
): MetricReviewLevel {
    return when {
        hoverBeforeFirstDrawMs == 0L -> MetricReviewLevel.LOW
        hoverBeforeFirstDrawMs < 3_000L -> MetricReviewLevel.LOW
        else -> MetricReviewLevel.MODERATE
    }
}

fun reviewHoverSegmentCount(
    hoverSegmentCount: Int
): MetricReviewLevel {
    return when {
        hoverSegmentCount <= 2 -> MetricReviewLevel.LOW
        hoverSegmentCount <= 7 -> MetricReviewLevel.MODERATE
        else -> MetricReviewLevel.HIGH
    }
}