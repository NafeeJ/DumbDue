package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RepeatYearlyByNumberTest {
    @Test
    fun getNextDueDate_frequency1june302020_june302021() {
        val june302020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val june302021 = Calendar.getInstance().apply {
            set(2021, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val repeat = RepeatYearlyByNumber(1)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(result, june302021)
    }

    @Test
    fun getNextDueDate_frequency3june302020_june302023() {
        val june302020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val june302023 = Calendar.getInstance().apply {
            set(2023, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val repeat = RepeatYearlyByNumber(3)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(result, june302023)
    }

}