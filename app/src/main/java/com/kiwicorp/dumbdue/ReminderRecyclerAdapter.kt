package com.kiwicorp.dumbdue

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reminder_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class ReminderRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var items: List<Reminder>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_reminder_item,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ReminderViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(reminderList: List<Reminder>) {
        items = reminderList
    }

    class ReminderViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reminderTextView: TextView = itemView.reminderTextView
        private val timeFromNowTextView: TextView = itemView.timeFromNowTextView
        private val dateOrRepeatTextView: TextView = itemView.dateOrRepeatTextView
        private val colorBar: View = itemView.colorBar

        private val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a") //creates a date format
        private val timeFormatter = SimpleDateFormat("h:mm a")
        private val dayOfWeekFormatter = SimpleDateFormat("EEEE")

        fun bind(reminder: Reminder) {
            val fromNowMins = MainActivity.findTimeFromNowMins(reminder.getRemindCalendar())
            //sets the reminder text
            reminderTextView.text = reminder.getText()

            if (fromNowMins > 0) {
                colorBar.setBackgroundColor(Color.parseColor("#0000ff"))//set color bar to blue
                dateOrRepeatTextView.setTextColor(Color.parseColor("#525252"))//set text color to grey
                timeFromNowTextView.text = "in ".plus(MainActivity.findTimeFromNowString(fromNowMins))
            } else {
                colorBar.setBackgroundColor(Color.parseColor("#ff0000"))//set color bar to red
                dateOrRepeatTextView.setTextColor(Color.parseColor("#ff0000"))//set text color to red
                timeFromNowTextView.text = MainActivity.findTimeFromNowString(fromNowMins).plus(" ago")
            }
            //if the reminder is repeating display its repeating interval
            if (reminder.getRepeatVal() != Reminder.REPEAT_NONE) {
                if (reminder.getRepeatVal() == Reminder.REPEAT_DAILY) {
                    dateOrRepeatTextView.text = "Daily ".plus(timeFormatter.format(reminder.getRemindCalendar().time))
                } else if (reminder.getRepeatVal() == Reminder.REPEAT_WEEKLY) {
                    dateOrRepeatTextView.text = dayOfWeekFormatter.format(reminder.getRemindCalendar().get(Calendar.DAY_OF_WEEK)).plus("s ").plus(timeFormatter.format(reminder.getRemindCalendar().time))
                } else if (reminder.getRepeatVal() == Reminder.REPEAT_MONTHLY) {
                    dateOrRepeatTextView.text = reminder.getRemindCalendar().get(Calendar.DAY_OF_MONTH).toString().plus(MainActivity.daySuffixFinder(reminder.getRemindCalendar())).plus(" each month at ").plus(timeFormatter.format(reminder.getRemindCalendar().time))
                }
            } else {//if the reminder is not repeating display the date
               dateOrRepeatTextView.text = dateFormatter.format(reminder.getRemindCalendar().time)
            }
        }
    }
}