package com.kiwicorp.dumbdue

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import java.util.*

class ReminderSectionedAdapter : SectionedRecyclerViewAdapter() {

    fun swipeDeleteItem(viewHolder: RecyclerView.ViewHolder, view: View) {

        val reminderHolder: ReminderViewHolder = viewHolder as ReminderViewHolder
        val reminderPositionInAdapter: Int = reminderHolder.adapterPosition
        val section: ReminderSection = getReminderSection(reminderPositionInAdapter)

        val reminderPositionInSection : Int = super.getPositionInSection(reminderPositionInAdapter)

        val reminderList : MutableList<Reminder> = section.getList()

        val removedReminder: Reminder = reminderList[reminderPositionInSection]
        removedReminder.deleteReminder()
        reminderList.remove(removedReminder)

        super.notifyItemRemovedFromSection(section,reminderPositionInSection)

        Snackbar.make(view, "Bye-Bye " + removedReminder.getText(), Snackbar.LENGTH_LONG).setAction("Undo") {
            removedReminder.reAddReminder(reminderPositionInAdapter)
        }.show()
    }
    //returns section reminder belongs to
    private fun getReminderSection(positionInAdapter: Int): ReminderSection {
        val todayPosition : Int = super.getSectionPosition("Today")
        val tomorrowPosition : Int = super.getSectionPosition("Tomorrow")
        val next7daysPosition : Int = super.getSectionPosition("Next 7 Days")
        val futurePosition : Int = super.getSectionPosition("Future")

        if (positionInAdapter < todayPosition) {
            return super.getSection("Overdue") as ReminderSection
        } else if (positionInAdapter < tomorrowPosition) {
            return super.getSection("Today") as ReminderSection
        } else if (positionInAdapter < next7daysPosition) {
            return super.getSection("Tomorrow") as ReminderSection
        } else if (positionInAdapter < futurePosition){
            return  super.getSection("Next 7 Days") as ReminderSection
        } else {
            return super.getSection("Future") as ReminderSection
        }
    }
    //returns section reminder belongs to
    private fun getReminderSection(reminder: Reminder): ReminderSection {
        val overdueSection: ReminderSection = super.getSection("Overdue") as ReminderSection
        val todaySection: ReminderSection = super.getSection("Today") as ReminderSection
        val tomorrowSection: ReminderSection = super.getSection("Tomorrow") as ReminderSection
        val next7daysSection: ReminderSection = super.getSection("Next 7 Days") as ReminderSection
        val futureSection: ReminderSection = super.getSection("Future") as ReminderSection

        if (futureSection.getList().indexOf(reminder) != -1) {
            return futureSection
        } else if (next7daysSection.getList().indexOf(reminder) != -1) {
            return next7daysSection
        } else if (tomorrowSection.getList().indexOf(reminder) != -1) {
            return tomorrowSection
        } else if (todaySection.getList().indexOf(reminder) != -1) {
            return todaySection
        } else {
            return overdueSection
        }

    }

    fun swipeCompleteItem(viewHolder: RecyclerView.ViewHolder, view: View) {
        val reminderHolder: ReminderViewHolder = viewHolder as ReminderViewHolder
        val reminderPositionInAdapter: Int = reminderHolder.adapterPosition
        val section: ReminderSection = getReminderSection(reminderPositionInAdapter)

        val reminderPositionInSection : Int = super.getPositionInSection(reminderPositionInAdapter)

        val reminderList : MutableList<Reminder> = section.getList()

        val removedReminder: Reminder = reminderList[reminderPositionInSection]
        removedReminder.deleteReminder()
        reminderList.remove(removedReminder)

        super.notifyItemRemovedFromSection(section,reminderPositionInSection)

        //if reminder is repeating, readd item with remind calendar incremented with the correct amount
        when(removedReminder.getRepeatVal()) {
            Reminder.REPEAT_DAILY -> {
                removedReminder.getRemindCalendar().add(Calendar.DAY_OF_YEAR, 1)
                removedReminder.reAddReminder(reminderPositionInAdapter)
            }
            Reminder.REPEAT_WEEKLY -> {
                removedReminder.getRemindCalendar().add(Calendar.WEEK_OF_YEAR, 1)
                removedReminder.reAddReminder(reminderPositionInAdapter)
            }
            Reminder.REPEAT_MONTHLY -> {
                removedReminder.getRemindCalendar().add(Calendar.MONTH, 1)
                removedReminder.reAddReminder(reminderPositionInAdapter)
            }
        }

        Snackbar.make(view,"Completed " + removedReminder.getText() + " :)", Snackbar.LENGTH_LONG).setAction("Undo") {
            val updatedSection : ReminderSection = getReminderSection(removedReminder)
            val updatedList : LinkedList<Reminder> = updatedSection.getList()
            val updatedCompletedPosition = updatedList.indexOf(removedReminder)

            //if reminder is repeating, remove item and readd with remind calendar decremented with the correct amount
            //else readd reminder normally
            when(removedReminder.getRepeatVal()) {
                Reminder.REPEAT_DAILY -> {
                    updatedList.removeAt(updatedCompletedPosition)
                    notifyItemRemoved(updatedCompletedPosition)

                    removedReminder.getRemindCalendar().add(Calendar.DAY_OF_YEAR, -1)
                    removedReminder.reAddReminder(reminderPositionInAdapter)
                }
                Reminder.REPEAT_WEEKLY -> {
                    updatedList.removeAt(updatedCompletedPosition)
                    notifyItemRemoved(updatedCompletedPosition)

                    removedReminder.getRemindCalendar().add(Calendar.WEEK_OF_YEAR, -1)
                    removedReminder.reAddReminder(reminderPositionInAdapter)
                }
                Reminder.REPEAT_MONTHLY -> {
                    updatedList.removeAt(updatedCompletedPosition)
                    notifyItemRemoved(updatedCompletedPosition)

                    removedReminder.getRemindCalendar().add(Calendar.MONTH, -1)
                    removedReminder.reAddReminder(reminderPositionInAdapter)
                }
                else -> removedReminder.reAddReminder(reminderPositionInAdapter)
            }
        }.show()
    }

}