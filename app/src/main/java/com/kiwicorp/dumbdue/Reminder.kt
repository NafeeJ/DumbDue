package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.absoluteValue


class Reminder(text: String, remindCalendar: Calendar, repeatVal: Int, context: Context) {

    companion object {
        //ints used to determine the user's desired repeat frequency
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKLY: Int = 2
        const val REPEAT_MONTHLY: Int = 3
        //request code used to keep track and make sure all pending intents for notifications are unique
        var globalRequestCode: Int = 0
        //lists that store the reminders
        var overdueList: LinkedList<Reminder> = LinkedList()
        var todayList: LinkedList<Reminder> = LinkedList()
        var tomorrowList: LinkedList<Reminder> = LinkedList()
        var next7daysList: LinkedList<Reminder> = LinkedList()
        var futureList: LinkedList<Reminder> = LinkedList()

    }
    private var title: String = ""
    private var remindCalendar: Calendar //calendar with date and time of intended reminder
    private var repeatVal: Int = 0 //int to indicate user's desired repeat frequency
    @Transient private var context: Context //application context
    private var requestCode: Int = 0 //reminder's unique requestCode for pending intent

    @Transient private var alarmManager: AlarmManager = MainActivity.globalAlarmManager
    @Transient private var intermediateReceiverIntent: Intent
    @Transient private var intermediateReceiverPendingIntent: PendingIntent

    init {
        this.title = text
        this.remindCalendar = remindCalendar
        this.repeatVal = repeatVal
        this.context = context
        requestCode = ++globalRequestCode

        intermediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
        intermediateReceiverIntent.putExtra("requestCode", requestCode)
        intermediateReceiverIntent.putExtra("reminderText",this.title)

        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(this.context,this.requestCode,intermediateReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        insertInOrder(findList(),this)
        MainActivity.saveAll(this.context)
        setAlarm(this.remindCalendar)
        val section = ReminderSection.getReminderSection(this)
        section.isVisible = true
    }
    //inserts reminder into its correct position in the given list
    private fun insertInOrder(list: LinkedList<Reminder>, reminder: Reminder) {
        //simply adds reminder if list is empty
        if (list.isEmpty()) {
            list.add(reminder)
            MainActivity.sectionAdapter.notifyDataSetChanged()
            return
        }
        //finds and adds reminder into its correct position
        val iterator = list.listIterator()
        for (element in iterator) {
            if (element.getRemindCalendar().timeInMillis > reminder.getRemindCalendar().timeInMillis) {
                list.add(iterator.previousIndex(), reminder)
                MainActivity.sectionAdapter.notifyDataSetChanged()
                return
            }
        }

        list.add(reminder)
        MainActivity.sectionAdapter.notifyDataSetChanged()
    }
    //returns the list this reminder belongs to
    private fun findList(): LinkedList<Reminder> {
        when {
            remindCalendar.timeInMillis < Calendar.getInstance().timeInMillis -> return overdueList
            remindCalendar.timeInMillis < MainActivity.todayCalendar.timeInMillis -> return todayList
            remindCalendar.timeInMillis < MainActivity.tomorrowCalendar.timeInMillis -> return tomorrowList
            remindCalendar.timeInMillis < MainActivity.next7daysCalendar.timeInMillis -> return next7daysList
        }
        return futureList
    }

    fun getText(): String { return title }

    fun getRemindCalendar(): Calendar{ return remindCalendar }

    fun getRepeatVal(): Int{ return repeatVal }

    fun getReminderData(): ReminderData {
        val section: ReminderSection = ReminderSection.getReminderSection(this)
        return ReminderData(title,remindCalendar,repeatVal,section.getTitle(),section.getList().indexOf(this))
    }
    //function that sets all the alarms for when reminder was deleted or app was closed
    private fun setNotifications(context: Context) {
        intermediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
        intermediateReceiverIntent.putExtra("requestCode", requestCode)
        intermediateReceiverIntent.putExtra("reminderTitle",title)

        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(context,
            requestCode,
            intermediateReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager = MainActivity.globalAlarmManager
        setAlarm(remindCalendar)
    }
    //sets alarm that triggers the intermediate receiver
    private fun setAlarm(remindCalendar: Calendar) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            remindCalendar.timeInMillis - 10000,
            this.intermediateReceiverPendingIntent)
    }
    //deletes this reminder
    fun deleteReminder() {
        //cancels the alarm that triggers the repeating alarm
        alarmManager.cancel(intermediateReceiverPendingIntent)
        //cancels repeating alarms
        val notificationReceiverIntent = Intent(context,NotificationReceiver::class.java)
        val notificationPendingIntent = PendingIntent.getBroadcast(context,requestCode,notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(notificationPendingIntent)

        val section: ReminderSection = ReminderSection.getReminderSection(this)
        //remove this reminder from its list and save
        findList().remove(this)
        MainActivity.saveAll(context)
        //cancels all notification currently being shown
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        //if list is empty sets this reminder's section to be invisible
        if (section.getList().isEmpty()) {
            section.isVisible = false
            MainActivity.sectionAdapter.notifyDataSetChanged()
        }
    }
    //readds this reminder into its position after it has been deleted/completed and the user undos
    fun reAddReminder(position: Int) {
        insertInOrder(findList(),this)
        MainActivity.sectionAdapter.notifyItemInserted(position)

        setNotifications(context)

        MainActivity.saveAll(context)

        val section = ReminderSection.getReminderSection(this)
        section.isVisible = true
        MainActivity.sectionAdapter.notifySectionChangedToVisible(section)
    }
    //data class that stores the essentials for recreating reminders when saving, loading, and editing
    @Parcelize data class ReminderData(val text: String,
                                       val remindCalendar: Calendar, val repeatVal: Int,
                                       val sectionTitle: String,
                                       val indexInSection: Int): Parcelable
}