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

    private val dateFormatter = SimpleDateFormat("MMM d, h:mm a") //month day, hour:min am/pm
    private val timeFormatter = SimpleDateFormat("h:mm a")//hour:min am/pm
    private val dayOfWeekFormatter = SimpleDateFormat("EEEE")//day

    fun bind(reminder: Reminder) {

        //sets the reminder text
        reminderTextView.text = reminder.getText()

        if (reminder.getRemindCalendar().timeInMillis < Calendar.getInstance().timeInMillis) {
            colorBar.setBackgroundColor(Color.parseColor("#f54242"))//set color bar to red
            dateOrRepeatTextView.setTextColor(Color.parseColor("#f54242"))//set text color to red
        } else if(reminder.getRemindCalendar() < MainActivity.todayCalendar) {
            colorBar.setBackgroundColor(Color.parseColor("#fff262"))//sets color bar to yellow
            dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
        } else if(reminder.getRemindCalendar() < MainActivity.tomorrowCalendar) {
            colorBar.setBackgroundColor(Color.parseColor("#3371FF"))//sets color bar to blue
            dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
        } else if (reminder.getRemindCalendar().timeInMillis < MainActivity.next7daysCalendar.timeInMillis) {
            colorBar.setBackgroundColor(Color.parseColor("#6a44b1"))//sets color bar to purple
            dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
        } else {
            colorBar.setBackgroundColor(Color.parseColor("#525252"))//sets color bar to grey
            dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
        }

        timeFromNowTextView.text = findTimeFromNowString(reminder.getRemindCalendar())

        //if the reminder is repeating display its repeating interval
        if (reminder.getRepeatVal() != Reminder.REPEAT_NONE) {
            if (reminder.getRepeatVal() == Reminder.REPEAT_DAILY) {
                dateOrRepeatTextView.text = "Daily ".plus(timeFormatter.format(reminder.getRemindCalendar().time))
            } else if (reminder.getRepeatVal() == Reminder.REPEAT_WEEKLY) {
                dateOrRepeatTextView.text = dayOfWeekFormatter.format(reminder.getRemindCalendar().get(
                    Calendar.DAY_OF_WEEK)).plus("s ").plus(timeFormatter.format(reminder.getRemindCalendar().time))
            } else if (reminder.getRepeatVal() == Reminder.REPEAT_MONTHLY) {
                dateOrRepeatTextView.text = reminder.getRemindCalendar().get(Calendar.DAY_OF_MONTH).toString().plus(MainActivity.daySuffixFinder(reminder.getRemindCalendar())).plus(" each month at ").plus(timeFormatter.format(reminder.getRemindCalendar().time))
            }
        } else {//if the reminder is not repeating display the date
            dateOrRepeatTextView.text = dateFormatter.format(reminder.getRemindCalendar().time)
        }
    }

    private fun findTimeFromNowString(calendar: Calendar): String { //returns a string with absolute value of time from now and its correct unit or if in between 1 day and 1 week, returns day of week
        val fromNowMins: Int = MainActivity.findTimeFromNowMins(calendar)
        val absTime = fromNowMins.absoluteValue

        var timeFromNowString: String

        val dayformatter = SimpleDateFormat("EEE")
        //sets timeFromNow to the greatest whole unit of time + that unit
        if (absTime == 0) { timeFromNowString = "0m" } //less than 1 minute
        else if (absTime == 1) { timeFromNowString = absTime.toString().plus("m") } //equal to 1 minute
        else if (absTime < 60) { timeFromNowString = absTime.toString().plus("m") } //less than 1 hour
        else if ((absTime / 60) == 1) { timeFromNowString = (absTime / 60.0).roundToInt().toString().plus("h") } //equal to 1 hour
        else if ((absTime / 60) < 24 ) { timeFromNowString = (absTime / 60.0).roundToInt().toString().plus("h") } //less than 1 day
        else if ((absTime / 60 / 24) == 1) { timeFromNowString = (absTime / 60 / 24.0).roundToInt().toString().plus("d") } //equal to 1 day
        else if ((absTime / 60 / 24) < 7) { timeFromNowString = (absTime / 60 / 24.0).roundToInt().toString().plus("d") } //less than 1 week
        else if ((absTime / 60 / 24 / 7) == 1) { timeFromNowString = (absTime / 60 / 24 / 7.0).roundToInt().toString().plus("w") } //equal to 1 week
        else if ((absTime / 60 / 24 / 7) < 4) { timeFromNowString = (absTime / 60 / 24 / 7.0).roundToInt().toString().plus("w") } //less than 1 month
        else if ((absTime / 60 / 24 / 7 / 4) == 1) { timeFromNowString = (absTime / 60 / 24 / 7 / 4.0).roundToInt().toString().plus("mo") } //equal to 1 month
        else if ((absTime / 60 / 24 / 7 / 4) < 12) { timeFromNowString = (absTime / 60 / 24 / 7 / 4.0).roundToInt().toString().plus("mo") } //less than one year
        else if ((absTime / 60 / 24 / 7 / 4 / 12) == 1) { timeFromNowString = (absTime / 60 / 24 / 7 / 4 / 12.0).roundToInt().toString().plus("yr")  } //equal to 1 year
        else timeFromNowString = (absTime / 60 / 24 / 7 / 4 / 12.0).roundToInt().toString().plus("yr")

        if (fromNowMins >= 0 && ((absTime / 60.0).roundToInt() <= 3) || calendar.timeInMillis > MainActivity.next7daysCalendar.timeInMillis) {
            timeFromNowString = "in ".plus(timeFromNowString)
        } else if(fromNowMins > 0 && calendar.timeInMillis < MainActivity.tomorrowCalendar.timeInMillis) {
            timeFromNowString = timeFormatter.format(calendar.time)
        }else if (fromNowMins > 0 && calendar.timeInMillis < MainActivity.next7daysCalendar.timeInMillis) {
            timeFromNowString = dayformatter.format(calendar.time)
        } else {//if time from now is negative add "ago"
            timeFromNowString = timeFromNowString.plus(" ago")
        }
        return timeFromNowString
    }

}