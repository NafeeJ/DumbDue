package com.kiwicorp.dumbdue

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_reminder_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


class ReminderRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var items: MutableList<Reminder>
    private lateinit var onReminderListener: OnReminderListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_reminder_item,parent,false),onReminderListener
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

    fun submitList(reminderList: MutableList<Reminder>) {
        items = reminderList
    }

    fun submitOnReminderClickListener(onReminderListener: OnReminderListener) {
        this.onReminderListener = onReminderListener
    }

    fun swipeDeleteItem(viewHolder: RecyclerView.ViewHolder, view: View) {

        val deletedItem: Reminder = items[viewHolder.adapterPosition]
        deleteItem(deletedItem,view)

    }

    fun removeItem(reminder: Reminder) {
        val index: Int = items.indexOf(reminder)
        items.remove(reminder)
        reminder.deleteReminder()
        notifyItemRemoved(index)
    }

    fun deleteItem(reminder: Reminder, view: View) {

        removeItem(reminder)
        showSnackBarDelete(reminder,view)

    }

    private fun showSnackBarDelete(reminder: Reminder, view: View) {
        Snackbar.make(view, "Bye-Bye " + reminder.getText(), Snackbar.LENGTH_LONG).setAction("Undo") {
            undoRemoval(reminder)
        }.show()
    }

    fun completeItem(viewHolder: RecyclerView.ViewHolder, view: View) {
        val completedPosition: Int = viewHolder.adapterPosition
        val completedItem: Reminder = items[completedPosition]

        items.removeAt(viewHolder.adapterPosition)
        completedItem.deleteReminder()
        notifyItemRemoved(viewHolder.adapterPosition)

        //if reminder is repeating, readd item with remind calendar incremented with the correct amount
        when(completedItem.getRepeatVal()) {
            Reminder.REPEAT_DAILY -> {
                completedItem.getRemindCalendar().add(Calendar.DAY_OF_YEAR, 1)
                undoRemoval(completedItem)
            }
            Reminder.REPEAT_WEEKLY -> {
                completedItem.getRemindCalendar().add(Calendar.WEEK_OF_YEAR, 1)
                undoRemoval(completedItem)
            }
            Reminder.REPEAT_MONTHLY -> {
                completedItem.getRemindCalendar().add(Calendar.MONTH, 1)
                undoRemoval(completedItem)
            }
        }

        Snackbar.make(view,"Completed " + completedItem.getText() + " :)", Snackbar.LENGTH_LONG).setAction("Undo") {
            //if reminder is repeating, remove item and readd with remind calendar decremented with the correct amount
            //else readd reminder normally
            when(completedItem.getRepeatVal()) {
                Reminder.REPEAT_DAILY -> {
                    val updatedCompletedPosition = items.indexOf(completedItem)
                    items.removeAt(updatedCompletedPosition)
                    notifyItemRemoved(updatedCompletedPosition)

                    completedItem.getRemindCalendar().add(Calendar.DAY_OF_YEAR, -1)
                    undoRemoval(completedItem)
                }
                Reminder.REPEAT_WEEKLY -> {
                    val updatedCompletedPosition = items.indexOf(completedItem)
                    items.removeAt(updatedCompletedPosition)
                    notifyItemRemoved(updatedCompletedPosition)

                    completedItem.getRemindCalendar().add(Calendar.WEEK_OF_YEAR, -1)
                    undoRemoval(completedItem)
                }
                Reminder.REPEAT_MONTHLY -> {
                    val updatedCompletedPosition = items.indexOf(completedItem)
                    items.removeAt(updatedCompletedPosition)
                    notifyItemRemoved(updatedCompletedPosition)

                    completedItem.getRemindCalendar().add(Calendar.MONTH, -1)
                    undoRemoval(completedItem)
                }
                else -> undoRemoval(completedItem)
            }
        }.show()
    }

    private fun undoRemoval(item: Reminder) {
        item.reAddReminder()
    }

    class ReminderViewHolder constructor(itemView: View, onReminderListener: OnReminderListener) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
        private val reminderTextView: TextView = itemView.reminderTextView
        private val timeFromNowTextView: TextView = itemView.timeFromNowTextView
        private val dateOrRepeatTextView: TextView = itemView.dateOrRepeatTextView
        private val colorBar: View = itemView.colorBar
        var onReminderListener: OnReminderListener

        private val dateFormatter = SimpleDateFormat("MMM d, h:mm a") //month day, hour:min am/pm
        private val timeFormatter = SimpleDateFormat("h:mm a")//hour:min am/pm
        private val dayOfWeekFormatter = SimpleDateFormat("EEEE")//day

        init {
            this.onReminderListener = onReminderListener
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
                    dateOrRepeatTextView.text = dayOfWeekFormatter.format(reminder.getRemindCalendar().get(Calendar.DAY_OF_WEEK)).plus("s ").plus(timeFormatter.format(reminder.getRemindCalendar().time))
                } else if (reminder.getRepeatVal() == Reminder.REPEAT_MONTHLY) {
                    dateOrRepeatTextView.text = reminder.getRemindCalendar().get(Calendar.DAY_OF_MONTH).toString().plus(MainActivity.daySuffixFinder(reminder.getRemindCalendar())).plus(" each month at ").plus(timeFormatter.format(reminder.getRemindCalendar().time))
                }
            } else {//if the reminder is not repeating display the date
               dateOrRepeatTextView.text = dateFormatter.format(reminder.getRemindCalendar().time)
            }
        }
        override fun onClick(v: View?) {
            onReminderListener.onReminderClick(adapterPosition)
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
    }
}

interface OnReminderListener {
    fun onReminderClick(position: Int)
}