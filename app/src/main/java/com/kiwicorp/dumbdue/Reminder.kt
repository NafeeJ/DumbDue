package com.kiwicorp.dumbdue

import android.app.AlarmManager
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

    }

    private var text: String
    private var remindCalendar: Calendar //calendar with date to remind
    private var repeatVal: Int = 0 //val for user's desired repeat frequency
    @Transient private var context: Context
    private var requestCode: Int = 0 //reminder's unique requestCode for pending intent

    @Transient private var alarmManager: AlarmManager = MainActivity.globalAlarmManager
    @Transient private var intermediateReceiverIntent: Intent
    @Transient private var intermediateReceiverPendingIntent: PendingIntent

    init {
        this.text = text
        this.remindCalendar = remindCalendar
        this.repeatVal = repeatVal
        this.context = context
        requestCode = ++globalRequestCode

        intermediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
        intermediateReceiverIntent.putExtra("requestCode", requestCode)
        intermediateReceiverIntent.putExtra("reminderText",this.text)

        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(this.context,this.requestCode,intermediateReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        insertInOrder(reminderList, this)
        MainActivity.saveAll(this.context)
        setAlarm(this.remindCalendar)
    }

    private fun insertInOrder(reminderList: LinkedList<Reminder>, reminder: Reminder) {
        if (reminderList.isEmpty()) {
            reminderList.add(reminder)
            MainActivity.reminderRecyclerAdapter.notifyItemInserted(0)
            return
        }

        val iterator = reminderList.listIterator()
        for (element in iterator) {
            if (element.getRemindCalendar().timeInMillis > reminder.getRemindCalendar().timeInMillis) {
                reminderList.add(iterator.previousIndex(), reminder)
                MainActivity.reminderRecyclerAdapter.notifyItemInserted(iterator.previousIndex())
                return
            }
        }

        reminderList.add(reminder)
        MainActivity.reminderRecyclerAdapter.notifyDataSetChanged()
    }

    fun getText(): String { return text }

    fun getRemindCalendar(): Calendar{ return remindCalendar }

    fun getRepeatVal(): Int{ return repeatVal }

    fun getReminderData(): ReminderData {
        val reminderData = ReminderData(this.text,this.remindCalendar,this.repeatVal, reminderList.indexOf(this))
        return reminderData
    }

    fun setNotifications(context: Context) {//sets all the alarms lost when reminder was deleted or app was closed
        this.context = context
        intermediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
        intermediateReceiverIntent.putExtra("requestCode", requestCode)
        intermediateReceiverIntent.putExtra("reminderText",this.text)
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
        reminderList.remove(this)
        MainActivity.saveAll(context)
    }

    fun reAddReminder() {
        insertInOrder(reminderList,this)
        setNotifications(context)
        MainActivity.saveAll(context)
    }

    @Parcelize
    data class ReminderData(val text: String, val remindCalendar: Calendar, val repeatVal: Int, val index: Int): Parcelable
}