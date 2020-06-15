package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class RepeatMonthlyByCountTest {
    private val dateFormatter = SimpleDateFormat("MMM d YYYY, h:mm a")

    @Test
    fun getNextDueDate_frequency1Recurrence2ndMondayJune82020_july132020() {
        val june82020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,8,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val july132020 = Calendar.getInstance().apply {
            set(2020, Calendar.JULY,13,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrence2ndMonday = listOf(Pair(Calendar.MONDAY,2))

        val repeat = RepeatMonthlyByCount(1,recurrence2ndMonday)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(result,july132020)
    }

    @Test
    fun getNextDueDate_frequency2Recurrence2ndMondayJune82020_august102020() {
        val june82020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,8,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val august102020 = Calendar.getInstance().apply {
            set(2020, Calendar.AUGUST,10,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrence2ndMonday = listOf(Pair(Calendar.MONDAY,2))

        val repeat = RepeatMonthlyByCount(2,recurrence2ndMonday)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(result,august102020)
    }

    @Test
    fun getNextDueDate_frequency8Recurrence2ndMondayJune82020_February82021() {
        val june82020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,8,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val february82021 = Calendar.getInstance().apply {
            set(2021, Calendar.FEBRUARY,8,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrence2ndMonday = listOf(Pair(Calendar.MONDAY,2))

        val repeat = RepeatMonthlyByCount(8,recurrence2ndMonday)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(result,february82021)
    }

    @Test
    fun getNextDueDate_frequency1RecurrenceArbitraryJune82020_June192020() {
        val june82020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,8,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val june192020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,19,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrence = listOf(
            Pair(Calendar.TUESDAY,1),
            Pair(Calendar.FRIDAY,1),
            Pair(Calendar.MONDAY,2),
            Pair(Calendar.FRIDAY,3),
            Pair(Calendar.THURSDAY,4)
        )

        val repeat = RepeatMonthlyByCount(1,recurrence)
        val result = repeat.getNextDueDate(june82020)

        assertEquals(result,june192020)
    }

    @Test
    fun getNextDueDate_frequency1RecurrenceLastFridayJune262020_july312020() {
        val june262020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,26,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val july312020 = Calendar.getInstance().apply {
            set(2020, Calendar.JULY,31,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceLastFriday = listOf(Pair(Calendar.FRIDAY,5))

        val repeat = RepeatMonthlyByCount(1,recurrenceLastFriday)
        val result = repeat.getNextDueDate(june262020)

        assertEquals(result,july312020)
    }

    @Test
    fun getNextDueDate_frequency1Recurrence4thFridayLastThursdayMay282020_june252020() {
        val may282020 = Calendar.getInstance().apply {
            set(2020, Calendar.MAY,28,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val june252020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,25,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val recurrenceLastFriday = listOf(
            Pair(Calendar.FRIDAY,4),
            Pair(Calendar.THURSDAY,5)
        )

        val repeat = RepeatMonthlyByCount(1,recurrenceLastFriday)
        val result = repeat.getNextDueDate(may282020)

        assertEquals(result,june252020)
    }
}