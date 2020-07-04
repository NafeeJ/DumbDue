package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.*

class RepeatYearlyByNumberTest {
    private val time = LocalTime.of(10,15)

    @Test
    fun getNextDueDate_frequency1RecurrenceJune30June302020_june302021() {
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())
        val june302021 = ZonedDateTime.of(LocalDate.of(2021, Month.JUNE, 30), time, ZoneId.systemDefault())

        val repeat = RepeatYearlyByNumber(1, LocalDateTime.from(june302020))
        val result = repeat.getNextDueDate(june302020)

        assertEquals(june302021,result)
    }

    @Test
    fun getNextDueDate_frequency3RecurrenceJune30June302020_june302023() {
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())
        val june302023 = ZonedDateTime.of(LocalDate.of(2023, Month.JUNE, 30), time, ZoneId.systemDefault())

        val repeat = RepeatYearlyByNumber(3, LocalDateTime.from(june302020))
        val result = repeat.getNextDueDate(june302020)

        assertEquals(june302023,result)
    }


    @Test
    fun getNextDueDate_frequency3RecurrenceJune30July42020_June302023() {
        val june302020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 30), time, ZoneId.systemDefault())
        val july42020 = ZonedDateTime.of(LocalDate.of(2020, Month.JULY, 4), time, ZoneId.systemDefault())
        val june302023 = ZonedDateTime.of(LocalDate.of(2023, Month.JUNE, 30), time, ZoneId.systemDefault())

        val repeat = RepeatYearlyByNumber(3, LocalDateTime.from(june302020))
        val result = repeat.getNextDueDate(july42020)

        assertEquals(june302023,result)
    }

}