package com.kiwicorp.dumbdue.data.repeat

import java.util.*

interface RepeatInterval {
    val frequency: Int
    fun getNextDueDate(calendar: Calendar): Calendar?
    fun getText(calendar: Calendar): String
}