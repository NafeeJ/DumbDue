package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RepeatNoneTest {
    @Test
    fun getNextDueDate_frequency0_null() {
        val calendar: Calendar = Calendar.getInstance()
        val repeat = RepeatNone(1)

        val result = repeat.getNextDueDate(calendar)
        assertEquals(result,null)
    }
}