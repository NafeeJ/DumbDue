package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByCount.Day
import org.threeten.bp.*

class RepeatMonthlyByCountTest {
    private val time = LocalTime.of(10,15)

    @Test
    fun getNextDueDate_frequency1Recurrence2ndMondayJune82020_july132020() {
        val june82020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 8), time, ZoneId.systemDefault())

        val july132020 = ZonedDateTime.of(LocalDate.of(2020, Month.JULY,13), time, ZoneId.systemDefault())

        val recurrence2ndMonday = listOf(Day(DayOfWeek.MONDAY, 2))

        val repeat = RepeatMonthlyByCount(1, YearMonth.of(2020,Month.JUNE),time,recurrence2ndMonday)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(july132020,result)
    }

    @Test
    fun getNextDueDate_frequency2Recurrence2ndMondayJune82020_august102020() {
        val june82020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 8), time, ZoneId.systemDefault())

        val august102020 = ZonedDateTime.of(LocalDate.of(2020, Month.AUGUST, 10), time, ZoneId.systemDefault())

        val recurrence2ndMonday = listOf(Day(DayOfWeek.MONDAY,2))

        val repeat = RepeatMonthlyByCount(2, YearMonth.of(2020,Month.JUNE),time,recurrence2ndMonday)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(august102020,result)
    }

    @Test
    fun getNextDueDate_frequency8Recurrence2ndMondayJune82020_February82021() {
        val june82020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 8), time, ZoneId.systemDefault())

        val february82021 = ZonedDateTime.of(LocalDate.of(2021, Month.FEBRUARY, 8), time, ZoneId.systemDefault())

        val recurrence2ndMonday = listOf(Day(DayOfWeek.MONDAY,2))

        val repeat = RepeatMonthlyByCount(8,YearMonth.of(2020, Month.JUNE),time,recurrence2ndMonday)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(february82021,result)
    }

    @Test
    fun getNextDueDate_frequency1RecurrenceArbitraryJune82020_June192020() {
        val june82020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 8), time, ZoneId.systemDefault())

        val june192020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 19), time, ZoneId.systemDefault())

        val recurrence = listOf(
            Day(DayOfWeek.TUESDAY,1),
            Day(DayOfWeek.FRIDAY,1),
            Day(DayOfWeek.MONDAY,2),
            Day(DayOfWeek.FRIDAY,3),
            Day(DayOfWeek.THURSDAY,4)
        )

        val repeat = RepeatMonthlyByCount(1, YearMonth.of(2020,Month.JUNE),time,recurrence)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(june192020,result)
    }

    @Test
    fun getNextDueDate_frequency1RecurrenceLastFridayJune262020_july312020() {
        val june262020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 26), time, ZoneId.systemDefault())

        val july312020 = ZonedDateTime.of(LocalDate.of(2020, Month.JULY, 31), time, ZoneId.systemDefault())

        val recurrenceLastFriday = listOf(Day(DayOfWeek.FRIDAY,5))

        val repeat = RepeatMonthlyByCount(1,YearMonth.of(2020, Month.JUNE),time,recurrenceLastFriday)
        val result = repeat.getNextDueDate(june262020)

        assertEquals(july312020,result)
    }

    @Test
    fun getNextDueDate_frequency1Recurrence4thFridayLastThursdayMay282020_june252020() {
        val may282020 = ZonedDateTime.of(LocalDate.of(2020, Month.MAY, 28), time, ZoneId.systemDefault())

        val june252020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 25), time, ZoneId.systemDefault())

        val recurrenceLastFriday = listOf(
            Day(DayOfWeek.FRIDAY,4),
            Day(DayOfWeek.THURSDAY,5)
        )

        val repeat = RepeatMonthlyByCount(1, YearMonth.of(2020, Month.MAY),time,recurrenceLastFriday)
        val result = repeat.getNextDueDate(may282020)

        assertEquals(june252020,result)
    }

    @Test
    fun getNextDueDate_frequency1Recurrence4thFridayJune182020_June262020() {
        val june182020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 18), time, ZoneId.systemDefault())

        val june262020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 26), time, ZoneId.systemDefault())

        val recurrenceLastFriday = listOf(Day(DayOfWeek.FRIDAY,4))

        val repeat = RepeatMonthlyByCount(1, YearMonth.of(2020, Month.JUNE),time,recurrenceLastFriday)
        val result = repeat.getNextDueDate(june182020)

        assertEquals(result,june262020)
    }
}