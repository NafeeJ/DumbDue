package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


class Reminder(val text: String, val remindCalendar: Calendar, val repeatVal: Int,val autoSnoozeVal: Int, @Transient val context: Context) {

    companion object {
        //ints used to determine the user's desired repeat frequency
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKDAYS: Int = 2
        const val REPEAT_WEEKLY: Int = 3
        const val REPEAT_MONTHLY: Int = 4
        const val REPEAT_YEARLY: Int = 5
        const val REPEAT_CUSTOM: Int = 6
        //ints used to determine user's desired autosnooze
        const val AUTO_SNOOZE_NONE: Int = 0
        const val AUTO_SNOOZE_MINUTE: Int = 1
        const val AUTO_SNOOZE_5_MINUTES: Int = 2
        const val AUTO_SNOOZE_10_MINUTES: Int = 3
        const val AUTO_SNOOZE_15_MINUTES: Int = 4
        const val AUTO_SNOOZE_30_MINUTES: Int = 5
        const val AUTO_SNOOZE_HOUR : Int = 6
        //request code used to keep track and make sure all pending intents for notifications are unique
        var globalRequestCode: Int = 0
        //lists that store the reminders
        var overdueList: LinkedList<Reminder> = LinkedList()
        var todayList: LinkedList<Reminder> = LinkedList()
        var tomorrowList: LinkedList<Reminder> = LinkedList()
        var next7daysList: LinkedList<Reminder> = LinkedList()
        var futureList: LinkedList<Reminder> = LinkedList()
    }
    var requestCode: Int //reminder's unique requestCode for pending intent

    @Transient private var alarmManager: AlarmManager = ReminderActivity.globalAlarmManager
    @Transient private lateinit var intermediateReceiverIntent: Intent
    @Transient private lateinit var intermediateReceiverPendingIntent: PendingIntent

    init {
        requestCode = ++globalRequestCode
        insertInOrder(getList(),this)
        ReminderActivity.saveAll(context)

        setNotifications(context)

        val section = ReminderSection.getReminderSection(this)
        section.isVisible = true
    }
    //inserts reminder into its correct position in the given list
    private fun insertInOrder(list: LinkedList<Reminder>, reminder: Reminder) {
        //simply adds reminder if list is empty
        if (list.isEmpty()) {
            list.add(reminder)
            ReminderActivity.sectionAdapter.notifyDataSetChanged()
            return
        }
        //finds and adds reminder into its correct position
        val iterator = list.listIterator()
        for (element in iterator) {
            if (element.remindCalendar.timeInMillis > reminder.remindCalendar.timeInMillis) {
                list.add(iterator.previousIndex(), reminder)
                ReminderActivity.sectionAdapter.notifyDataSetChanged()
                return
            }
        }
        list.add(reminder)
        ReminderActivity.sectionAdapter.notifyDataSetChanged()
    }
    //returns the list this reminder belongs to based off of its time
    private fun getList(): LinkedList<Reminder> {
        return when {
            remindCalendar.timeInMillis < Calendar.getInstance().timeInMillis -> overdueList
            remindCalendar.timeInMillis < ReminderActivity.todayCalendar.timeInMillis -> todayList
            remindCalendar.timeInMillis < ReminderActivity.tomorrowCalendar.timeInMillis -> tomorrowList
            remindCalendar.timeInMillis < ReminderActivity.next7daysCalendar.timeInMillis -> next7daysList
            else -> futureList
        }
    }

    fun getReminderData(): ReminderData {
        val section: ReminderSection = ReminderSection.getReminderSection(this)
        val data =  ReminderData(text,remindCalendar,repeatVal,requestCode,autoSnoozeVal,section.getTitle(),getList().indexOf(this))
        return data
    }
    //function that sets all the alarms for when reminder was deleted or app was closed
    private fun setNotifications(context: Context) {
        intermediateReceiverIntent = Intent(context,IntermediateReceiver::class.java)
        val reminderDataBundle = Bundle()
        reminderDataBundle.putParcelable("ReminderData",getReminderData())
        intermediateReceiverIntent.putExtra("ReminderDataBundle",reminderDataBundle)

        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(context, requestCode,
            intermediateReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            remindCalendar.timeInMillis - 10000,
            intermediateReceiverPendingIntent)
    }

    fun deleteReminder() {
        //cancel the intermediate alarm
        alarmManager.cancel(intermediateReceiverPendingIntent)
        //cancels repeating alarms
        val notificationReceiverIntent = Intent(context,NotificationReceiver::class.java)
        val notificationPendingIntent = PendingIntent.getBroadcast(context,requestCode,notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(notificationPendingIntent)

        val section: ReminderSection = ReminderSection.getReminderSection(this)
        val list = getList()
        val positionInSection = list.indexOf(this)
        //remove this reminder from its list and save
        ReminderActivity.sectionAdapter.notifyItemRemovedFromSection(section,positionInSection)
        list.remove(this)
        ReminderActivity.saveAll(context)
        //if list is empty sets this reminder's section to be invisible
        if (list.isEmpty()) {
            section.isVisible = false
            ReminderActivity.sectionAdapter.notifyDataSetChanged()
        }
        //cancels all notification currently being shown
        //todo fix this so that notifications IDs are being kept track of
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
    //readds this reminder into its position after it has been deleted/completed and the user undos
    fun reAddReminder(position: Int) {
        insertInOrder(getList(),this)
        ReminderActivity.sectionAdapter.notifyItemInserted(position)
        ReminderActivity.saveAll(context)

        setNotifications(context)

        val section = ReminderSection.getReminderSection(this)
        section.isVisible = true
        ReminderActivity.sectionAdapter.notifySectionChangedToVisible(section)
    }
    //data class that stores the essentials for recreating reminders when saving, loading, and editing
    @Parcelize data class ReminderData(val text: String, val remindCalendar: Calendar,
                                       val repeatVal: Int, val requestCode: Int, val autoSnoozeVal: Int,
                                       val sectionTitle: String, val positionInSection: Int): Parcelable
}