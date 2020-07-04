package com.kiwicorp.dumbdue.timesetters

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.LocalTime

class QuickAccessTimeSetterTest {

    @Test
    fun adjustInto_EightThirty_FiveForty() {
        val eightThirty = LocalTime.of(8,30)

        val fiveForty = LocalTime.of(5, 40)

        val timeSetter = QuickAccessTimeSetter(5,40)
        val result = eightThirty.with(timeSetter)

        assertEquals(fiveForty, result)
    }
}