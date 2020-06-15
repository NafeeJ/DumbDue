package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RepeatDailyTest {
    @Test
    fun getNextDueDate_frequency1june152020_june162020() {
        val june152020: Calendar = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val repeat = RepeatDaily(1)

        val june162020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,16,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val result = repeat.getNextDueDate(june152020)
        assertEquals(result,june162020)
    }

    @Test
    fun getNextDueDate_frequency3june152020_june182020() {
        val june152020: Calendar = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val repeat = RepeatDaily(3)

        val june182020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,18,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val result = repeat.getNextDueDate(june152020)
        assertEquals(result,june182020)
    }

}