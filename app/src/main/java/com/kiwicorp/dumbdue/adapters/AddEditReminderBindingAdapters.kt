package com.kiwicorp.dumbdue.adapters

import android.graphics.Color
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.util.daySuffix
import com.kiwicorp.dumbdue.util.timeFromNowMins
import com.kiwicorp.dumbdue.util.timeFromNowString
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("timeFromNow")
fun TextView.setTimeFromNow(calendar: Calendar) {
    val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a", Locale.US)
    val dateTime = dateFormatter.format(calendar.time)
    val timeFromNow = calendar.timeFromNowString()

    text = context.resources.getString(
        R.string.time_from_now_future,
        dateTime,
        timeFromNow)

    setTextColor(Color.parseColor(if (calendar.timeFromNowMins() < 0) "#f54242" else "#FFFFFF"))
}

/**
 * Updates the text for TextViews that displays a repeat frequency
 */
@BindingAdapter(value = ["calendar","repeatVal"], requireAll = true)
fun TextView.setRepeatText(calendar: Calendar, repeatVal: Int) {
    val resources = context.resources

    val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
    val dayFormatter = SimpleDateFormat("EEEE", Locale.US)
    val dateFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.US)

    text = when(repeatVal) {
        Reminder.REPEAT_NONE -> resources.getString(R.string.repeat_off)
        Reminder.REPEAT_DAILY -> {
            val time: String = timeFormatter.format(calendar.time)
            resources.getString(R.string.repeat_daily,time)
        }
        Reminder.REPEAT_WEEKDAYS -> {
            val time: String = timeFormatter.format(calendar.time)
            resources.getString(R.string.repeat_weekdays,time)
        }
        Reminder.REPEAT_WEEKLY ->  {
            val time: String = timeFormatter.format(calendar.time)
            val dayOfWeek = dayFormatter.format(calendar.time)
            resources.getString(R.string.repeat_weekly,"${dayOfWeek}s $time")
        }
        Reminder.REPEAT_MONTHLY -> {
            val time: String = timeFormatter.format(calendar.time)
            val dayOfMonth: String = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val daySuffix = calendar.daySuffix()
            resources.getString(R.string.repeat_monthly,"${dayOfMonth}$daySuffix",time)
        }
        Reminder.REPEAT_YEARLY -> {
            val dateTime = dateFormatter.format(calendar.time)
            resources.getString(R.string.repeat_yearly,dateTime)
        }
        Reminder.REPEAT_CUSTOM -> resources.getString(R.string.repeat_custom)
        else -> throw IllegalArgumentException("Unknown Repeat Value: $repeatVal")
    }
}

/**
 * Updates the AutoSnooze image button in fragment_add_reminder.xml and fragment_edit_reminder.xml
 */
@BindingAdapter("autoSnooze")
fun ImageButton.setAutoSnooze(autoSnooze: Int) {
    setImageResource(when(autoSnooze) {
        Reminder.AUTO_SNOOZE_NONE -> R.drawable.white_none_square
        Reminder.AUTO_SNOOZE_MINUTE -> R.drawable.one_white
        Reminder.AUTO_SNOOZE_5_MINUTES -> R.drawable.five_white
        Reminder.AUTO_SNOOZE_10_MINUTES -> R.drawable.ten_white
        Reminder.AUTO_SNOOZE_15_MINUTES -> R.drawable.fifteen_white
        Reminder.AUTO_SNOOZE_30_MINUTES -> R.drawable.thirty_white
        Reminder.AUTO_SNOOZE_HOUR -> R.drawable.one_hour_white
        else -> throw IllegalArgumentException("Unknown Auto Snooze Value: $autoSnooze")
    })
}