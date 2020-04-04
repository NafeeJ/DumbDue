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


class Reminder(val text: String, val remindCalendar: Calendar, val repeatVal: Int,val autoSnoozeVal: Int, @Transient var context: Context) {

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
    }
    var requestCode: Int //reminder's unique requestCode for pending intent
    @Transient var list: LinkedList<Reminder>//contains one of companion lists
    @Transient lateinit var section: ReminderSection

    @Transient lateinit var intermediateReceiverIntent: Intent
    @Transient lateinit var intermediateReceiverPendingIntent: PendingIntent

    init {
        requestCode = ++globalRequestCode
        list = MainFragment.getCorrectList(remindCalendar)
        MainFragment.insertReminderInOrder(list,this)
        MainFragment.saveAll(context)
        setNotifications()
    }

    fun getReminderData(): ReminderData {
        return ReminderData(text,remindCalendar,repeatVal,requestCode,autoSnoozeVal,section.title,list.indexOf(this))
    }

    //re-adds this reminder into its position after it has been deleted/completed and the user undo's
    fun reAddReminder() {
        MainFragment.insertReminderInOrder(list,this)
        MainFragment.saveAll(context)
        setNotifications()
    }
    //loads reminder after app closes and is reopened
    fun loadReminder(context: Context) {
        list = MainFragment.getCorrectList(remindCalendar)
        MainFragment.insertReminderInOrder(list,this)

        this.context = context
        intermediateReceiverIntent = Intent(context,IntermediateReceiver::class.java)
        val reminderDataBundle = Bundle()
        reminderDataBundle.putParcelable("ReminderData",getReminderData())
        intermediateReceiverIntent.putExtra("ReminderDataBundle",reminderDataBundle)
        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(context, requestCode,
            intermediateReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    //function that sets all the alarms for when reminder was deleted or app was closed
    private fun setNotifications() {
        intermediateReceiverIntent = Intent(context,IntermediateReceiver::class.java)
        val reminderDataBundle = Bundle()
        reminderDataBundle.putParcelable("ReminderData",getReminderData())
        intermediateReceiverIntent.putExtra("ReminderDataBundle",reminderDataBundle)

        intermediateReceiverPendingIntent = PendingIntent.getBroadcast(context, requestCode,
            intermediateReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        MainFragment.globalAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
            remindCalendar.timeInMillis - 10000,
            intermediateReceiverPendingIntent)
    }

    fun cancelNotifications() {
        //cancel the intermediate alarm
        MainFragment.globalAlarmManager.cancel(intermediateReceiverPendingIntent)
        //cancels repeating alarms
        val notificationReceiverIntent = Intent(context, NotificationReceiver::class.java)
        val notificationPendingIntent = PendingIntent.getBroadcast(context, requestCode, notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        MainFragment.globalAlarmManager.cancel(notificationPendingIntent)

        //cancels all notification currently being shown
        //todo fix this so that notifications IDs are being kept track of
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
    //data class that stores the essentials for recreating reminders when saving, loading, and editing
    @Parcelize data class ReminderData(
        val text: String,
        val remindCalendar: Calendar,
        val repeatVal: Int,
        val requestCode: Int,
        val autoSnoozeVal: Int,
        val sectionTitle: String,
        val positionInSection: Int): Parcelable
    }