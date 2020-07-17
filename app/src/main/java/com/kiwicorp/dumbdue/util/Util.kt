package com.kiwicorp.dumbdue.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.kiwicorp.dumbdue.R
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Month
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

fun Fragment.closeKeyboard() {
    val view = requireActivity().currentFocus
    view?.let { v ->
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun getDaySuffix(dayOfMonth: Int): String {
    return when {
        dayOfMonth.rem(10) == 1 && dayOfMonth != 11 -> "st"
        dayOfMonth.rem(10) == 2 && dayOfMonth != 12 -> "nd"
        dayOfMonth.rem(10) == 3 && dayOfMonth != 13 -> "rd"
        else -> "th"
    }
}

fun Int.toBoolean(): Boolean {
    return this != 0
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun Resources.Theme.isLightTheme(): Boolean {
    val typedValue = TypedValue()
    resolveAttribute(R.attr.isLightTheme, typedValue, true)
    return typedValue.data.toBoolean()
}