package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


class Reminder(text: String,remindCalendar: Calendar,repeatVal: Int,context: Context) {

    companion object {
        //const vals used to determine the user's desired repeat frequency
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKLY: Int = 2
        const val REPEAT_MONTHLY: Int = 3
        //request code used to keep track and make sure all pending intents are unique
        var globalRequestCode: Int = 0

        val reminderList: LinkedList<Reminder> = LinkedList()
    }

    private var text: String
    private var remindCalendar: Calendar //calendar with date to remind
    private var repeatVal: Int = 0 //val for user's desired repeat frequency
    private var context: Context
    private var requestCode: Int = 0 //reminder's unique requestCode for pending intent

    init {
        this.text = text
        this.remindCalendar = remindCalendar
        this.repeatVal = repeatVal
        this.context = context
        this.requestCode = ++globalRequestCode

        reminderList.add(this)
        setAlarm(this.remindCalendar)
    }

    private val alarmManager: AlarmManager =  this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val interMediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
    private val intermediateReceiverPendingIntent = PendingIntent.getBroadcast(this.context,this.requestCode,interMediateReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT)

    private val intermediateReceiver = IntermediateReceiver(requestCode)

    fun getText(): String {
        return this.text
    }

    fun setText(text: String) {
        this.text = text
    }

    fun getRemindCalendar(): Calendar{
        return this.remindCalendar
    }

    fun setRemindCalendar(remindCalendar: Calendar) {
        this.remindCalendar = remindCalendar
    }

    fun getRepeatVal(): Int{
        return this.repeatVal
    }

    fun setRepeatVal(repeatVal: Int) {
        this.repeatVal = repeatVal
    }

    private fun setAlarm(remindCalendar: Calendar) {
        this.alarmManager.setExact(AlarmManager.RTC_WAKEUP,remindCalendar.timeInMillis,this.intermediateReceiverPendingIntent)
    }

    fun complete() {
        this.alarmManager.cancel(intermediateReceiverPendingIntent)//cancels the alarm that triggers the repeating alarm
        intermediateReceiver.cancelAlarm()//cancels the repeating alarms
        reminderList.remove(this)

        if (repeatVal != 0) {
            if (repeatVal == 1) {//repeat daily
                remindCalendar.add(Calendar.DAY_OF_YEAR, 1)
                Reminder(this.text,this.remindCalendar,this.repeatVal,this.context)

            } else if (repeatVal == 2) {//repeat weekly
                remindCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                Reminder(this.text,this.remindCalendar,this.repeatVal,this.context)

            } else if (repeatVal == 3) {//repeat monthly
                remindCalendar.add(Calendar.MONTH, 1)
                Reminder(this.text,this.remindCalendar,this.repeatVal,this.context)

            }
        }
    }

    fun cancel() {
        this.alarmManager.cancel(intermediateReceiverPendingIntent)//cancels the alarm that triggers the repeating alarm
        intermediateReceiver.cancelAlarm()//cancels the repeating alarms
        reminderList.remove(this)
    }

}