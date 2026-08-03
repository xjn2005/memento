package com.echo.app.domain.algorithm

import com.echo.app.domain.model.RecallFeedback
import kotlin.math.ceil
import kotlin.math.ln

class ForgettingCurve(
    private val recallThreshold: Double = 0.56,
) {
    init {
        require(recallThreshold in 0.0..1.0) { "Recall threshold must be between 0 and 1." }
    }

    fun nextIntervalDays(stabilityDays: Double): Int {
        require(stabilityDays > 0) { "Stability must be positive." }
        return ceil(-ln(recallThreshold) * stabilityDays).toInt().coerceAtLeast(1)
    }

    fun adjustStability(currentStability: Double, feedback: RecallFeedback): Double {
        require(currentStability > 0) { "Stability must be positive." }
        return when (feedback) {
            RecallFeedback.Important -> (currentStability * 2.4).coerceAtMost(180.0)
            RecallFeedback.Changed -> (currentStability * 1.25).coerceAtMost(90.0)
            RecallFeedback.NoLongerNeeded -> currentStability
        }
    }
}
