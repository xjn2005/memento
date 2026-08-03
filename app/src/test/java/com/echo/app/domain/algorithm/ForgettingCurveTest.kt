package com.echo.app.domain.algorithm

import com.echo.app.domain.model.RecallFeedback
import org.junit.Assert.assertEquals
import org.junit.Test

class ForgettingCurveTest {
    private val curve = ForgettingCurve()

    @Test
    fun `initial stability schedules recall after two days`() {
        assertEquals(2, curve.nextIntervalDays(3.0))
    }

    @Test
    fun `important feedback increases stability by two point four`() {
        assertEquals(7.2, curve.adjustStability(3.0, RecallFeedback.Important), 0.001)
    }

    @Test
    fun `interval is never less than one day`() {
        assertEquals(1, curve.nextIntervalDays(0.1))
    }
}
