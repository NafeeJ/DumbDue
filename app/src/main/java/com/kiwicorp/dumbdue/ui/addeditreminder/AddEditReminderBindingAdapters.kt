package com.kiwicorp.dumbdue.ui.addeditreminder

import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.repeat.RepeatInterval
import com.kiwicorp.dumbdue.util.getColorFromAttr
import com.kiwicorp.dumbdue.util.timeFromNowString
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Updates the AutoSnooze image button in fragment_add_reminder.xml and fragment_edit_reminder.xml
 */
@BindingAdapter("autoSnooze")
fun ImageButton.setAutoSnooze(autoSnooze: Long) {
    setImageResource(when(autoSnooze) {
        Reminder.AUTO_SNOOZE_NONE -> R.drawable.ic_none
        Reminder.AUTO_SNOOZE_MINUTE -> R.drawable.one_white
        Reminder.AUTO_SNOOZE_5_MINUTES -> R.drawable.five_white
        Reminder.AUTO_SNOOZE_10_MINUTES -> R.drawable.ten_white
        Reminder.AUTO_SNOOZE_15_MINUTES -> R.drawable.fifteen_white
        Reminder.AUTO_SNOOZE_30_MINUTES -> R.drawable.thirty_white
        Reminder.AUTO_SNOOZE_HOUR -> R.drawable.one_hour_white
        else -> throw IllegalArgumentException("Unknown Auto Snooze Value: $autoSnooze")
    })
}

@BindingAdapter("autoSnooze")
fun TextView.setAutoSnooze(autoSnooze: Long) {
    text = when(autoSnooze) {
        Reminder.AUTO_SNOOZE_NONE -> resources.getString(R.string.auto_snooze_none)
        Reminder.AUTO_SNOOZE_MINUTE -> resources.getString(R.string.auto_snooze_minute)
        Reminder.AUTO_SNOOZE_5_MINUTES -> resources.getString(R.string.auto_snooze_5_minute)
        Reminder.AUTO_SNOOZE_10_MINUTES -> resources.getString(R.string.auto_snooze_10_minutes)
        Reminder.AUTO_SNOOZE_15_MINUTES -> resources.getString(R.string.auto_snooze_15_minutes)
        Reminder.AUTO_SNOOZE_30_MINUTES -> resources.getString(R.string.auto_snooze_30_minutes)
        Reminder.AUTO_SNOOZE_HOUR -> resources.getString(R.string.auto_snooze_hour)
        else -> throw IllegalArgumentException("Unknown Auto Snooze value: $autoSnooze")
    }
}

@BindingAdapter(value = ["app:timeFromNowDueDate","app:timeFromNowLong"], requireAll = true)
fun TextView.setTimeFromNowText(dueDate: ZonedDateTime, long: Boolean) {
    val timeFromNow = dueDate.timeFromNowString(false)

    if (long) {
        val dateTime = dueDate.format(DateTimeFormatter.ofPattern("EEE, d MMM, h:mm a"))

        val stringId = if (dueDate.isBefore(ZonedDateTime.now()))
            R.string.time_from_now_long_past
        else
            R.string.time_from_now_long_future


        text = context.getString(stringId, dateTime, timeFromNow)
    } else {
        val stringId = if (dueDate.isBefore(ZonedDateTime.now()))
            R.string.time_from_now_short_past
        else
            R.string.time_from_now_short_future


        text = context.getString(stringId, timeFromNow)
    }

    setTextColor(context.getColorFromAttr(
        if (dueDate.isBefore(ZonedDateTime.now())) R.attr.colorError else R.attr.colorOnSurface
    ))
}

@BindingAdapter("repeatInterval")
fun TextView.setRepeatInterval(repeatInterval: RepeatInterval?) {
    text = repeatInterval?.toString() ?: context.getString(R.string.repeat_off)
}