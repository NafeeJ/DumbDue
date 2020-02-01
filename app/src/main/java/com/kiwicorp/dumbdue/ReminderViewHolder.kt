package com.kiwicorp.dumbdue

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reminder_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class ReminderViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
    private val reminderTextView: TextView = itemView.reminderTextView
    private val timeFromNowTextView: TextView = itemView.timeFromNowTextView
    private val dateOrRepeatTextView: TextView = itemView.dateOrRepeatTextView
    private val colorBar: View = itemView.colorBar
    val rootView = itemView

//        private val onReminderClickListener: OnReminderClickListener = onReminderClickListener

    private val dateFormatter = SimpleDateFormat("MMM d, h:mm a") //month day, hour:min am/pm
    private val timeFormatter = SimpleDateFormat("h:mm a")//hour:min am/pm
    private val dayOfWeekFormatter = SimpleDateFormat("EEEE")//day

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(reminder: Reminder) {
        val fromNowMins = MainActivity.findTimeFromNowMins(reminder.getRemindCalendar())
        //sets the reminder text
        reminderTextView.text = reminder.getText()

        if (fromNowMins > 0) {
            colorBar.setBackgroundColor(Color.parseColor("#3371FF"))//set color bar to blue
            dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
            timeFromNowTextView.text = findTimeFromNowString(reminder.getRemindCalendar())
        } else {
            colorBar.setBackgroundColor(Color.parseColor("#f54242"))//set color bar to red
            dateOrRepeatTextView.setTextColor(Color.parseColor("#f54242"))//set text color to red
            timeFromNowTextView.text = findTimeFromNowString(reminder.getRemindCalendar())
        }
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
//        override fun onClick(v: View?) {
//            onReminderClickListener.onReminderClick(adapterPosition)
//        }

    private fun findTimeFromNowString(calendar: Calendar): String { //returns a string with absolute value of time from now and its correct unit or if in between 1 day and 1 week, returns day of week
        val fromNowMins: Int = MainActivity.findTimeFromNowMins(calendar)
        val absTime = fromNowMins.absoluteValue

        var timeFromNowString: String

        val dayformatter = SimpleDateFormat("EEE")
        //sets timeFromNow to the greatest whole unit of time + that unit
        if (absTime == 0) { timeFromNowString = "0m" } //less than 1 minute
        else if (absTime == 1) { timeFromNowString = absTime.toString().plus("m") } //equal to 1 minute
        else if (absTime < 60) { timeFromNowString = absTime.toString().plus("m") } //less than 1 hour
        else if ((absTime / 60) == 1) { timeFromNowString = (absTime / 60).toString().plus("h") } //equal to 1 hour
        else if ((absTime / 60) < 24 ) { timeFromNowString = (absTime / 60).toString().plus("h") } //less than 1 day
        else if ((absTime / 60 / 24) == 1) { timeFromNowString = (absTime / 60 / 24).toString().plus("d") } //equal to 1 day
        else if ((absTime / 60 / 24) < 7) { timeFromNowString = (absTime / 60 / 24).toString().plus("d") } //less than 1 week
        else if ((absTime / 60 / 24 / 7) == 1) { timeFromNowString = (absTime / 60 / 24 / 7).toString().plus("w") } //equal to 1 week
        else if ((absTime / 60 / 24 / 7) < 4) { timeFromNowString = (absTime / 60 / 24 / 7).toString().plus("w") } //less than 1 month
        else if ((absTime / 60 / 24 / 7 / 4) == 1) { timeFromNowString = (absTime / 60 / 24 / 7 / 4).toString().plus("mo") } //equal to 1 month
        else if ((absTime / 60 / 24 / 7 / 4) < 12) { timeFromNowString = (absTime / 60 / 24 / 7 / 4).toString().plus("mo") } //less than one year
        else if ((absTime / 60 / 24 / 7 / 4 / 12) == 1) { timeFromNowString = (absTime / 60 / 24 / 7 / 4 / 12).toString().plus("yr")  } //equal to 1 year
        else timeFromNowString = (absTime / 60 / 24 / 7 / 4 / 12).toString().plus("yr")

        if (fromNowMins >= 0 && (((absTime / 60 / 24) >= 7 && (absTime / 60) >= 24) || (absTime / 60) <= 12  )) {//adds "in" to beginning of timeFromNow if positive and not in between 1 week and 12 hours
            timeFromNowString = "in ".plus(timeFromNowString)
        } else if(fromNowMins > 0 && ((absTime / 60) > 12 && (absTime / 60) < 24)  ) {
            timeFromNowString = timeFormatter.format(calendar.time)
        }else if ((fromNowMins / 60 / 24) < 7 && fromNowMins > 0) {//sets timeFromNow to be the day of reminder, if fromNowMins is postive and less than 7 days but more than one day
            timeFromNowString = dayformatter.format(calendar.time)
        } else {//if time from now is negative add "ago"
            timeFromNowString = timeFromNowString.plus(" ago")
        }
        return timeFromNowString
    }

    override fun onClick(v: View?) {

    }
}