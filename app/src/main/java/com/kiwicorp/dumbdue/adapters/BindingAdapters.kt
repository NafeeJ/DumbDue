package com.kiwicorp.dumbdue.adapters

import android.graphics.Color
import android.opengl.Visibility
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.util.daySuffix
import com.kiwicorp.dumbdue.util.timeFromNowMins
import com.kiwicorp.dumbdue.util.timeFromNowString
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("timeFromNow")
fun TextView.setTimeFromNow(calendar: LiveData<Calendar>) {
    val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a", Locale.US)
    val dateTime = dateFormatter.format(calendar.value!!.time)
    val timeFromNow = calendar.value!!.timeFromNowString()

    text = context.resources.getString(
        R.string.time_from_now_future,
        dateTime,
        timeFromNow)

    if (calendar.value!!.timeFromNowMins() < 0) {
        setBackgroundColor(Color.RED)
    } else {
        setBackgroundColor(Color.parseColor("#303030"))
    }
}

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
        else -> resources.getString(R.string.repeat_custom)
    }
}
