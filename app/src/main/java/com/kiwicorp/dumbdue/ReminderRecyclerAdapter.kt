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
//    private lateinit var onReminderClickListener: OnReminderClickListener

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

    fun submitList(reminderList: MutableList<Reminder>) {
        items = reminderList
    }

//    fun submitOnReminderClickListener(onReminderClickListener: OnReminderClickListener) {
//        this.onReminderClickListener = onReminderClickListener
//    }

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
        item.reAddReminder(2)
    }

}

//interface OnReminderClickListener {
//    fun onReminderClick(position: Int)
//}