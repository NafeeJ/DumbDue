package com.kiwicorp.dumbdue.timesetters

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

class IncrementalTimeSetterTest {

    @Test
    fun adjustInto_FiveForty_TenMinutes_resultFiveFifty() {
        val fiveForty = LocalTime.of(5, 40)
        val fiveFifty = LocalTime.of(5, 50)

        val incrementalTimeSetter10Minutes = IncrementalTimeSetter(10, ChronoUnit.MINUTES)
        val result = fiveForty.with(incrementalTimeSetter10Minutes)

        assertEquals(result, fiveFifty)
    }
}