package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


class Reminder(text: String, remindCalendar: Calendar, repeatVal: Int, context: Context) {

    companion object {
        //const vals used to determine the user's desired repeat frequency
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKLY: Int = 2
        const val REPEAT_MONTHLY: Int = 3
        //request code used to keep track and make sure all pending intents are unique
        var globalRequestCode: Int = 0

        var reminderList: LinkedList<Reminder> = LinkedList()

        val overdueList: LinkedList<Reminder> = LinkedList()
        val todayList: LinkedList<Reminder> = LinkedList()
        val tomorrowList: LinkedList<Reminder> = LinkedList()
        val next7daysList: LinkedList<Reminder> = LinkedList()
        val futureList: LinkedList<Reminder> = LinkedList()

    }

    private var title: String = ""
    private var remindCalendar: Calendar //calendar with date to remind
    private var repeatVal: Int = 0 //val for user's desired repeat frequency
    @Transient private var context: Context
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
//        MainActivity.saveAll(this.context)
        setAlarm(this.remindCalendar)
    }

    private fun insertInOrder(list: LinkedList<Reminder>, reminder: Reminder) {
        if (list.isEmpty()) {
            list.add(reminder)
            MainActivity.sectionAdapter.notifyDataSetChanged()
            return
        }

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

    private fun findList(): LinkedList<Reminder> {//returns the list this reminder belongs to
        if (remindCalendar.timeInMillis < Calendar.getInstance().timeInMillis) {
            return overdueList
        } else if (remindCalendar.timeInMillis < MainActivity.todayCalendar.timeInMillis) {
            return todayList
        } else if (remindCalendar.timeInMillis < MainActivity.tomorrowCalendar.timeInMillis) {
            return tomorrowList
        } else if (remindCalendar.timeInMillis < MainActivity.next7daysCalendar.timeInMillis) {
            return next7daysList
        } else {
            return futureList
        }
    }

    fun getText(): String { return title }

    fun getRemindCalendar(): Calendar{ return remindCalendar }

    fun getRepeatVal(): Int{ return repeatVal }

    fun getReminderData(): ReminderData {
        return ReminderData(this.title,this.remindCalendar,this.repeatVal, reminderList.indexOf(this))
    }

    fun setNotifications(context: Context) {//sets all the alarms lost when reminder was deleted or app was closed
        this.context = context
        intermediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
        intermediateReceiverIntent.putExtra("requestCode", requestCode)
        intermediateReceiverIntent.putExtra("reminderText",this.title)
        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(this.context,this.requestCode,intermediateReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager = MainActivity.globalAlarmManager
        setAlarm(remindCalendar)
    }

    private fun setAlarm(remindCalendar: Calendar) { alarmManager.setExact(AlarmManager.RTC_WAKEUP,remindCalendar.timeInMillis - 10000,this.intermediateReceiverPendingIntent) }

    fun deleteReminder() {
        alarmManager.cancel(intermediateReceiverPendingIntent)//cancels the alarm that triggers the repeating alarm

        //cancels repeating alarms
        val notificationReceiverIntent = Intent(context,NotificationReceiver::class.java)
        val notificationPendingIntent = PendingIntent.getBroadcast(context,requestCode,notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(notificationPendingIntent)
        //remove from list and save
        findList().remove(this)
//        MainActivity.saveAll(context)

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun reAddReminder(position: Int) {
        val list = findList()
        insertInOrder(findList(),this)
        setNotifications(context)
        MainActivity.sectionAdapter.notifyItemInserted(position)
//        MainActivity.saveAll(context)
    }

    @Parcelize
    data class ReminderData(val text: String, val remindCalendar: Calendar, val repeatVal: Int, val index: Int): Parcelable
}