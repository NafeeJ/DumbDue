package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RepeatMonthlyByNumberTest {
    @Test
    fun getNextDueDate_frequency1Recurrence15June152020_July152020() {
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val july152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JULY,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val recurrenceFifteenthOnly = List(32) { it == 15 }

        val repeat = RepeatMonthlyByNumber(1, recurrenceFifteenthOnly)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(result,july152020)
    }

    @Test
    fun getNextDueDate_frequency1ArbitraryRecurrenceWith18thJune152020_June182020() {
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val june182020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,18,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceDays = List(32) {
            it == 1 || it == 10 || it == 13 || it == 15 || it == 18 || it == 21|| it == 30
        }

        val repeat = RepeatMonthlyByNumber(1,recurrenceDays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(result,june182020)
    }

    @Test
    fun getNextDueDate_frequency1ArbitraryRecurrenceJune302020_July1() {
        val june302020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val july12020 = Calendar.getInstance().apply {
            set(2020, Calendar.JULY,1,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceDays = List(32) {
            it == 1 || it == 10 || it == 13 || it == 15 || it == 18 || it == 21|| it == 30
        }

        val repeat = RepeatMonthlyByNumber(1,recurrenceDays)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(result,july12020)
    }

    @Test
    fun getNextDueDate_frequency3ArbitraryRecurrenceJune302020_September12020() {
        val june302020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val september12020 = Calendar.getInstance().apply {
            set(2020, Calendar.SEPTEMBER,1,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceDays = List(32) {
            it == 1 || it == 10 || it == 13 || it == 15 || it == 18 || it == 21|| it == 30
        }

        val repeat = RepeatMonthlyByNumber(3,recurrenceDays)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(result,september12020)
    }

    @Test
    fun getNextDueDate_frequency1LastDayRecurrenceJune302020_July312020() {
        val june302020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val july312020 = Calendar.getInstance().apply {
            set(2020, Calendar.JULY,31,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceDays = List(32) { it == 0 }

        val repeat = RepeatMonthlyByNumber(1,recurrenceDays)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(result,july312020)
    }

    @Test
    fun getNextDueDate_frequency3ArbitraryRecurrenceWithLastDayJune212020_June302020() {
        val june212020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,21,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val june302020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceDays = List(32) {
            it == 0 ||it == 1 || it == 10 || it == 13 || it == 15 || it == 18 || it == 21
        }

        val repeat = RepeatMonthlyByNumber(3,recurrenceDays)
        val result = repeat.getNextDueDate(june212020)

        assertEquals(result,june302020)
    }
}