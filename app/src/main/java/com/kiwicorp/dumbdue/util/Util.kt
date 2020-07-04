package com.kiwicorp.dumbdue.util

import android.graphics.drawable.GradientDrawable
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kiwicorp.dumbdue.R
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.format.TextStyle
import java.util.*

fun DayOfWeek.getFullName(): String = getDisplayName(TextStyle.FULL, Locale.ENGLISH)

fun Month.getFullName(): String = getDisplayName(TextStyle.FULL, Locale.ENGLISH)

fun List<DayOfWeek>.sortedSundayFirst(): List<DayOfWeek> {
    val sorted = this.sorted().toMutableList()
    if (sorted.last() == DayOfWeek.SUNDAY) {
        sorted.remove(DayOfWeek.SUNDAY)
        sorted.add(0, DayOfWeek.SUNDAY)
    }
    return sorted
}

fun <T> Fragment.getDropDownMenuAdapter(list: List<T>): ArrayAdapter<T> {
    return ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, list)
}

fun getDaySuffix(dayOfMonth: Int): String {
    return when {
        dayOfMonth.rem(10) == 1 && dayOfMonth != 11 -> "st"
        dayOfMonth.rem(10) == 2 && dayOfMonth != 12 -> "nd"
        dayOfMonth.rem(10) == 3 && dayOfMonth != 13 -> "rd"
        else -> "th"
    }
}
