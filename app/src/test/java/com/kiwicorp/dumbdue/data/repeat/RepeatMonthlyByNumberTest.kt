package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.*
import java.util.*

class RepeatMonthlyByNumberTest {

    private val time = LocalTime.of(10, 15)

    @Test
    fun getNextDueDate_frequency1Recurrence15June152020_July152020() {
        val june152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 15), time, ZoneId.systemDefault())
        val july152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JULY, 15), time, ZoneId.systemDefault())

        val recurrenceFifteenthOnly = listOf(15)

        val repeat = RepeatMonthlyByNumber(1, YearMonth.of(2020,Month.JUNE), time, recurrenceFifteenthOnly)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(july152020, result)
    }

    @Test
    fun getNextDueDate_frequency1ArbitraryRecurrenceWith18thJune152020_June182020() {
        val june152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 15), time, ZoneId.systemDefault())
        val june182020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 18), time, ZoneId.systemDefault())

        val recurrenceDays = listOf(1,10,13,15,18,21,30)

        val repeat = RepeatMonthlyByNumber(1,YearMonth.of(2020, Month.JUNE),time,recurrenceDays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(june182020, result)
    }

    @Test
    fun getNextDueDate_frequency1ArbitraryRecurrenceJune302020_July1() {
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())
        val july12020 = ZonedDateTime.of(LocalDate.of(2020, Month.JULY, 1), time, ZoneId.systemDefault())

        val recurrenceDays = listOf(1,10,13,15,18,21,30)

        val repeat = RepeatMonthlyByNumber(1, YearMonth.of(2020,Month.JUNE),time,recurrenceDays)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(july12020,result)
    }

    @Test
    fun getNextDueDate_frequency3ArbitraryRecurrenceJune302020_September12020() {
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())
        val september12020 = ZonedDateTime.of(LocalDate.of(2020, Month.SEPTEMBER, 1), time, ZoneId.systemDefault())

        val recurrenceDays = listOf(1,10,13,15,18,21,30)

        val repeat = RepeatMonthlyByNumber(3, YearMonth.of(2020,Month.JUNE),time,recurrenceDays)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(september12020,result)
    }

    @Test
    fun getNextDueDate_frequency1LastDayRecurrenceJune302020_July312020() {
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())
        val july312020 = ZonedDateTime.of(LocalDate.of(2020, Month.JULY, 31), time, ZoneId.systemDefault())

        val recurrenceDays = listOf(32)

        val repeat = RepeatMonthlyByNumber(1,YearMonth.of(2020, Month.JUNE),time,recurrenceDays)
        val result = repeat.getNextDueDate(june302020)

        assertEquals(july312020,result)
    }

    @Test
    fun getNextDueDate_frequency3ArbitraryRecurrenceWithLastDayJune212020_June302020() {
        val june212020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 21), time, ZoneId.systemDefault())
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())

        val recurrenceDays = listOf(1,10,13,15,18,21,32)

        val repeat = RepeatMonthlyByNumber(3, YearMonth.of(2020,Month.JUNE),time,recurrenceDays)
        val result = repeat.getNextDueDate(june212020)

        assertEquals(june302020, result)
    }
}