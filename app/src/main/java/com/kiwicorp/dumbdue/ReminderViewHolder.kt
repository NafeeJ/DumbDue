package com.kiwicorp.dumbdue

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reminder_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ReminderViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val reminderTextView: TextView = itemView.reminderTextView
    private val timeFromNowTextView: TextView = itemView.timeFromNowTextView
    private val dateOrRepeatTextView: TextView = itemView.dateOrRepeatTextView
    private val colorBar: View = itemView.colorBar
    val rootView = itemView

    private val dateFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.US) //month day, hour:min am/pm
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)//hour:min am/pm
    private val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.US)//day

    fun bind(reminder: Reminder) {
        //sets the reminder text
        reminderTextView.text = reminder.getText()

        val remindCalendar: Calendar = reminder.getRemindCalendar()
        val repeatVal: Int = reminder.getRepeatVal()

        when {
            remindCalendar < Calendar.getInstance() -> {
                colorBar.setBackgroundColor(Color.parseColor("#f54242"))//set color bar to red
                dateOrRepeatTextView.setTextColor(Color.parseColor("#f54242"))//set text color to red
            }
            remindCalendar < MainActivity.todayCalendar -> {
                colorBar.setBackgroundColor(Color.parseColor("#fff262"))//sets color bar to yellow
                dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
            }
            remindCalendar < MainActivity.tomorrowCalendar -> {
                colorBar.setBackgroundColor(Color.parseColor("#3371FF"))//sets color bar to blue
                dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
            }
            remindCalendar < MainActivity.next7daysCalendar -> {
                colorBar.setBackgroundColor(Color.parseColor("#6a44b1"))//sets color bar to purple
                dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
            }
            else -> {
                colorBar.setBackgroundColor(Color.parseColor("#525252"))//sets color bar to grey
                dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
            }
        }

        timeFromNowTextView.text = findTimeFromNowString(reminder.getRemindCalendar())

        //if the reminder is repeating display its repeating interval
        if (repeatVal != Reminder.REPEAT_NONE) {
            when (repeatVal) {
                Reminder.REPEAT_DAILY -> dateOrRepeatTextView.text = "Daily "
                    .plus(timeFormatter.format(reminder.getRemindCalendar().time))

                Reminder.REPEAT_WEEKLY -> dateOrRepeatTextView.text = dayOfWeekFormatter
                    .format(reminder.getRemindCalendar().get(
                    Calendar.DAY_OF_WEEK)).plus("s ")
                    .plus(timeFormatter.format(reminder.getRemindCalendar().time))

                Reminder.REPEAT_MONTHLY -> dateOrRepeatTextView.text = reminder.getRemindCalendar()
                    .get(Calendar.DAY_OF_MONTH).toString()
                    .plus(MainActivity.daySuffixFinder(reminder.getRemindCalendar()))
                    .plus(" each month at ")
                    .plus(timeFormatter.format(reminder.getRemindCalendar().time))
            }
        } else {//if the reminder is not repeating display the date
            dateOrRepeatTextView.text = dateFormatter.format(reminder.getRemindCalendar().time)
        }
    }
    //returns a string with absolute value of time from now and its correct unit
    //or returns day of week if in between 1 day and 1 week
    private fun findTimeFromNowString(calendar: Calendar): String {
        val fromNowMins: Int = MainActivity.findTimeFromNowMins(calendar)
        val absMins = fromNowMins.absoluteValue

        var timeFromNowString: String

        val dayFormatter = SimpleDateFormat("EEE", Locale.US)
        //sets timeFromNow to the greatest whole unit of time + that unit
        timeFromNowString = when {
            absMins == 0 -> { "0m" } //less than 1 minute
            absMins == 1 -> { absMins.toString().plus("m") } //equal to 1 minute
            absMins < 60 -> { absMins.toString().plus("m") } //less than 1 hour
            absMins / 60 == 1 -> { (absMins / 60.0).roundToInt().toString().plus("h") } //equal to 1 hour
            absMins / 60 < 24 -> { (absMins / 60.0).roundToInt().toString().plus("h") } //less than 1 day
            absMins / 60 / 24 == 1 -> { (absMins / 60 / 24.0).roundToInt().toString().plus("d") } //equal to 1 day
            absMins / 60 / 24 < 7 -> { (absMins / 60 / 24.0).roundToInt().toString().plus("d") } //less than 1 week
            absMins / 60 / 24 / 7 == 1 -> {  (absMins / 60 / 24 / 7.0).roundToInt().toString().plus("w") } //equal to 1 week
            absMins / 60 / 24 / 7 < 4 -> { (absMins / 60 / 24 / 7.0).roundToInt().toString().plus("w") } //less than 1 month
            absMins / 60 / 24 / 7 / 4 == 1 -> { (absMins / 60 / 24 / 7 / 4.0).roundToInt().toString().plus("mo") } //equal to 1 month
            absMins / 60 / 24 / 7 / 4 < 12 -> { (absMins / 60 / 24 / 7 / 4.0).roundToInt().toString().plus("mo") } //less than one year
            absMins / 60 / 24 / 7 / 4 / 12 == 1 ->{ (absMins / 60 / 24 / 7 / 4 / 12.0).roundToInt().toString().plus("yr")  } //equal to 1 year
            else -> (absMins / 60 / 24 / 7 / 4 / 12.0).roundToInt().toString().plus("yr")
        }

        timeFromNowString = when {
            //add "in" to time from now string if within 3 hours or more than a week
            fromNowMins >= 0 && ((absMins / 60.0).roundToInt() <= 3) || calendar.timeInMillis > MainActivity.next7daysCalendar.timeInMillis -> {
                "in ".plus(timeFromNowString)
            }
            //set time from now string to be the time if less than 2 days from today
            fromNowMins > 0 && calendar.timeInMillis < MainActivity.tomorrowCalendar.timeInMillis -> {
                timeFormatter.format(calendar.time)
            }
            //set time from now string to be the day if less than a week from today
            fromNowMins > 0 && calendar.timeInMillis < MainActivity.next7daysCalendar.timeInMillis -> {
                dayFormatter.format(calendar.time)
            }
            //if time from now is negative add "ago"
            else -> { timeFromNowString.plus(" ago") }
        }

        return timeFromNowString
    }

}