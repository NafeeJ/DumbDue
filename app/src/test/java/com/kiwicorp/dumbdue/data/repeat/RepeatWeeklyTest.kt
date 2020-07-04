package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.*

class RepeatWeeklyTest {

    private val time = LocalTime.of(10,15)

    private val weekDays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    @Test
    fun getNextDueDate_frequency1WeekdaysJune152020_june162020() {
        // a monday
        val june152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,15),time, ZoneId.systemDefault())
        // a tuesday
        val june162020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,16),time, ZoneId.systemDefault())

        val repeat = RepeatWeekly(1, LocalDateTime.of(2020,Month.JUNE,14,time.hour, time.minute),weekDays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(june162020,result)
    }

    @Test
    fun getNextDueDate_frequency2WeekdaysJune1502020_june162020() {
        // a monday
        val june152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,15),time, ZoneId.systemDefault())
        // a tuesday
        val june162020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,16),time, ZoneId.systemDefault())

        val firstDateTimeOfWeek = LocalDateTime.of(2020,Month.JUNE,14,time.hour, time.minute)

        val repeat = RepeatWeekly(2, firstDateTimeOfWeek, weekDays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(june162020, result)
    }

    @Test
    fun getNextDueDate_frequency1WeekdaysJune122020_june152020() {
        //a friday
        val june122020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,12),time, ZoneId.systemDefault())

        // monday next week
        val june152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,15),time, ZoneId.systemDefault())

        val firstDateTimeOfWeek = LocalDateTime.of(2020,Month.JUNE,7,time.hour, time.minute)

        val repeat = RepeatWeekly(1,firstDateTimeOfWeek,weekDays)
        val result = repeat.getNextDueDate(june122020)

        assertEquals(june152020,result)
    }

    @Test
    fun getNextDueDate_frequency3EverydayExceptWednesdayJune162020_june182020() {
        // a tuesday
        val june162020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,16),time, ZoneId.systemDefault())
        // a thursday
        val june182020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,18),time, ZoneId.systemDefault())

        val everydayExceptWednesday = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        val firstDateTimeOfWeek = LocalDateTime.of(2020,Month.JUNE,14,time.hour, time.minute)

        val repeat = RepeatWeekly(3,firstDateTimeOfWeek,everydayExceptWednesday)
        val result = repeat.getNextDueDate(june162020)

        assertEquals(result,june182020)
    }

    @Test
    fun getNextDueDate_frequency1MondaysJune152020_june222020() {
        // a monday
        val june152020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,15),time, ZoneId.systemDefault())

        // next monday
        val june222020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,22),time, ZoneId.systemDefault())

        val mondays = listOf(DayOfWeek.MONDAY)

        val firstDateTimeOfWeek = LocalDateTime.of(2020,Month.JUNE,14,time.hour, time.minute)

        val repeat = RepeatWeekly(1,firstDateTimeOfWeek,mondays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(result,june222020)
    }

    @Test
    fun getNextDueDate_frequency1MondaysJune112020_June152020() {
        // a thursday
        val june112020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,11),time, ZoneId.systemDefault())

        // next monday
        val june222020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE,22),time, ZoneId.systemDefault())

        val mondays = listOf(DayOfWeek.MONDAY)

        val firstDateTimeOfWeek = LocalDateTime.of(2020,Month.JUNE,7,time.hour, time.minute)

        val repeat = RepeatWeekly(2,firstDateTimeOfWeek,mondays)
        val result = repeat.getNextDueDate(june112020)

        assertEquals(result,june222020)
    }
}