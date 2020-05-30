package com.kiwicorp.dumbdue.adapters

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.util.daySuffix
import com.kiwicorp.dumbdue.util.timeFromNowMins
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@BindingAdapter("items")
fun RecyclerView.setItems(items: List<Reminder>?) {
    items?.let {
        (adapter as ReminderAdapter).addHeadersAndSubmitList(it)
    }
}

@BindingAdapter("timeFromNowAbbr")
fun TextView.setTimeFromNowAbbr(calendar: Calendar) {
    val fromNowMins = calendar.timeFromNowMins()
    val absMins = fromNowMins.absoluteValue

    //sets timeFromNow to the greatest whole unit of time + that unit
    text = when {
        absMins == 0 -> { "0m" } //less than 1 minute
        absMins == 1 -> { "${absMins}m" } //equal to 1 minute
        absMins < 60 -> { "${absMins}m" } //less than 1 hour
        absMins / 60 == 1 -> { "${(absMins / 60.0).roundToInt()}h" } //equal to 1 hour
        absMins / 60 < 24 -> { "${(absMins / 60.0).roundToInt()}h"  } //less than 1 day
        absMins / 60 / 24 == 1 -> { "${(absMins / 60 / 24.0).roundToInt()}d" } //equal to 1 day
        absMins / 60 / 24 < 7 -> { "${(absMins / 60 / 24.0).roundToInt()}d" } //less than 1 week
        absMins / 60 / 24 / 7 == 1 -> { "${(absMins / 60 / 24 / 7.0).roundToInt()}w" } //equal to 1 week
        absMins / 60 / 24 / 7 < 4 -> { "${(absMins / 60 / 24 / 7.0).roundToInt()}w" } //less than 1 month
        absMins / 60 / 24 / 7 / 4 == 1 -> { "${(absMins / 60 / 24 / 7 / 4.0).roundToInt()}mo" } //equal to 1 month
        absMins / 60 / 24 / 7 / 4 < 12 -> { "${(absMins / 60 / 24 / 7 / 4.0).roundToInt()}mo" } //less than one year
        absMins / 60 / 24 / 7 / 4 / 12 == 1 ->{ "${(absMins / 60 / 24 / 7 / 4 / 12.0).roundToInt()}yr" } //equal to 1 year
        else -> "${(absMins / 60 / 24 / 7 / 4 / 12.0).roundToInt()}yr"
    }

    //calendar with date at 23:59:59 tomorrow
    val endOfTomorrowCalendar = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND,59)
        set(Calendar.SECOND,59)
        set(Calendar.MINUTE,59)
        set(Calendar.HOUR_OF_DAY,23)
        add(Calendar.DAY_OF_YEAR,1)
    }
    //calendar with date at 23:59:59 in 7 days
    val endOfNext7daysCalendar = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND,59)
        set(Calendar.SECOND,59)
        set(Calendar.MINUTE,59)
        set(Calendar.HOUR_OF_DAY,23)
        add(Calendar.WEEK_OF_YEAR,1)
    }

    val time = SimpleDateFormat("h:mm a", Locale.US).format(calendar.time)
    val day = SimpleDateFormat("EEE", Locale.US).format(calendar.time)

    text = when {
        //add "in" to time from now string if within 3 hours or more than a week
        fromNowMins >= 0 && ((absMins / 60.0).roundToInt() <= 3) || calendar.timeInMillis > endOfNext7daysCalendar.timeInMillis -> {
            "in $text"
        }
        //set time from now string to be the time if less than 2 days from today
        fromNowMins > 0 && calendar.timeInMillis < endOfTomorrowCalendar.timeInMillis -> {
            time
        }
        //set time from now string to be the day if less than a week from today
        fromNowMins > 0 && calendar.timeInMillis < endOfNext7daysCalendar.timeInMillis -> {
            day
        }
        //if time from now is negative add "ago"
        else -> { "$text ago" }
    }
}
//itemCalendar and itemRepeatVal are used because function parameters conflict with [TextView.setRepeatText()]
@BindingAdapter(value = ["itemCalendar","itemRepeatVal"], requireAll = true)
fun TextView.setDateOrRepeatText(calendar: Calendar,repeatVal: Int) {
    if (calendar.timeFromNowMins() < 0) {
        setTextColor(Color.parseColor("#f54242"))
    } else {
        setTextColor(Color.parseColor("#525252"))
    }

    val time = SimpleDateFormat("h:mm a", Locale.US).format(calendar.time)
    val date = SimpleDateFormat("MMM d, h:mm a", Locale.US).format(calendar.time)
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
    val dayOfMonth = "${calendar.get(Calendar.DAY_OF_MONTH)}${calendar.daySuffix()}"

    text = if (repeatVal != Reminder.REPEAT_NONE) {
        when (repeatVal) {
            Reminder.REPEAT_DAILY -> "Daily $time"
            Reminder.REPEAT_WEEKDAYS -> "Weekdays $time"
            Reminder.REPEAT_WEEKLY -> "${dayOfWeek}s $time"
            Reminder.REPEAT_MONTHLY -> "$dayOfMonth each month at $time"
            Reminder.REPEAT_YEARLY -> "Every $date"
            else -> "Custom repeat at $time"
        }
    } else {
        date
    }
}

@BindingAdapter("calendar")
fun View.setColor(calendar: Calendar) {
    //calendar with date at 23:59:59 today
    val endOfTodayCalendar = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND,59)
        set(Calendar.SECOND,59)
        set(Calendar.MINUTE,59)
        set(Calendar.HOUR_OF_DAY,23)
    }
    //calendar with date at 23:59:59 tomorrow
    val endOfTomorrowCalendar = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND,59)
        set(Calendar.SECOND,59)
        set(Calendar.MINUTE,59)
        set(Calendar.HOUR_OF_DAY,23)
        add(Calendar.DAY_OF_YEAR,1)
    }
    //calendar with date at 23:59:59 in 7 days
    val endOfNext7daysCalendar = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND,59)
        set(Calendar.SECOND,59)
        set(Calendar.MINUTE,59)
        set(Calendar.HOUR_OF_DAY,23)
        add(Calendar.WEEK_OF_YEAR,1)
    }

    setBackgroundColor(when {
        calendar < Calendar.getInstance() -> Color.parseColor("#f54242") //red
        calendar < endOfTodayCalendar -> Color.parseColor("#fff262") //yellow
        calendar < endOfTomorrowCalendar -> Color.parseColor("#3371FF")//blue
        calendar < endOfNext7daysCalendar -> Color.parseColor("#6a44b1") //purple
        else -> Color.parseColor("#525252") //grey
    })
}