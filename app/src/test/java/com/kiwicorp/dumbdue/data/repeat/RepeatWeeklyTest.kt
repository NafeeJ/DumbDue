package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class RepeatWeeklyTest {

    @Test
    fun getNextDueDate_frequency1WeekdaysJune152020_june162020() {
        // a monday
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        // a tuesday
        val june162020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,16,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val weekDays = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY
        )

        val repeat = RepeatWeekly(1,june152020,weekDays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(result,june162020)
    }

    @Test
    fun getNextDueDate_frequency2WeekdaysJune1502020_june162020() {
        // a monday
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        // a tuesday
        val june162020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,16,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val weekDays = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY
        )

        val repeat = RepeatWeekly(2,june152020,weekDays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(result,june162020)
    }

    @Test
    fun getNextDueDate_frequency2WeekdaysJune122020_june1502020() {
        //a friday
        val june122020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,12,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        // monday next week
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,22,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val weekDays = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY
        )

        val repeat = RepeatWeekly(2,june122020,weekDays)
        val result = repeat.getNextDueDate(june122020)

        assertEquals(result,june152020)
    }

    @Test
    fun getNextDueDate_frequency3EverydayExceptWednesdayJune162020_june182020() {
        // a tuesday
        val june162020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,16,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        // a thursday
        val june182020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,18,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val everydayExceptWednesday = listOf(
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY
        )

        val repeat = RepeatWeekly(3,june162020,everydayExceptWednesday)
        val result = repeat.getNextDueDate(june162020)

        assertEquals(result,june182020)
    }

    @Test
    fun getNextDueDate_frequency1MondaysJune152020_june222020() {
        // a monday
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        // next monday
        val june222020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,22,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val mondays = listOf(Calendar.MONDAY)

        val repeat = RepeatWeekly(1,june152020,mondays)
        val result = repeat.getNextDueDate(june152020)

        assertEquals(result,june222020)
    }

    @Test
    fun getNextDueDate_frequency1MondaysJune112020_June152020() {
        // a thursday
        val june112020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,11,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        // next monday
        val june152020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,15,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val mondays = listOf(Calendar.MONDAY)

        val repeat = RepeatWeekly(1,june112020,mondays)
        val result = repeat.getNextDueDate(june112020)

        assertEquals(result,june152020)
    }
}