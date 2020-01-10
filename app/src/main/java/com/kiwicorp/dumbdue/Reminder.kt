package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

    @Transient private val alarmManager: AlarmManager
    private val interMediateReceiverIntent: Intent
    private val intermediateReceiverPendingIntent: PendingIntent
    private val intermediateReceiver: IntermediateReceiver

    init {
        this.text = text
        this.remindCalendar = remindCalendar
        this.repeatVal = repeatVal
        this.context = context
        this.requestCode = ++globalRequestCode
        this.alarmManager = this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        this.interMediateReceiverIntent = Intent(this.context,IntermediateReceiver::class.java)
        this.intermediateReceiverPendingIntent = PendingIntent.getBroadcast(this.context,this.requestCode,interMediateReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        this.intermediateReceiver = IntermediateReceiver(requestCode)

        insertInOrder(reminderList, this)
        MainActivity.saveAll(this.context)
        setAlarm(this.remindCalendar)
    }

    fun insertInOrder(reminderList: LinkedList<Reminder>, reminder: Reminder) {
        if (reminderList.size == 0) {
            reminderList.add(reminder)
            return
        }

        val iterator = reminderList.listIterator()
        for (element in iterator) {
            if (element.getRemindCalendar().timeInMillis > reminder.getRemindCalendar().timeInMillis) {
                reminderList.add(iterator.previousIndex(), reminder)
                return
            }
        }

        reminderList.add(reminder)
    }

    fun getText(): String { return this.text }

    fun setText(text: String) { this.text = text }

    fun getRemindCalendar(): Calendar{ return this.remindCalendar }

    fun setRemindCalendar(remindCalendar: Calendar) { this.remindCalendar = remindCalendar }

    fun getRepeatVal(): Int{ return this.repeatVal }

    fun setRepeatVal(repeatVal: Int) { this.repeatVal = repeatVal }

    private fun setAlarm(remindCalendar: Calendar) { alarmManager.setExact(AlarmManager.RTC_WAKEUP,remindCalendar.timeInMillis,this.intermediateReceiverPendingIntent) }

    fun complete() {
        this.alarmManager.cancel(intermediateReceiverPendingIntent)//cancels the alarm that triggers the repeating alarm
        intermediateReceiver.cancelAlarm()//cancels the repeating alarms
        reminderList.remove(this)

        if (repeatVal != 0) {
            if (repeatVal == REPEAT_DAILY) {
                remindCalendar.add(Calendar.DAY_OF_YEAR, 1)
                Reminder(this.text,this.remindCalendar,this.repeatVal,this.context)

            } else if (repeatVal == REPEAT_WEEKLY) {
                remindCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                Reminder(this.text,this.remindCalendar,this.repeatVal,this.context)

            } else if (repeatVal == REPEAT_MONTHLY) {
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