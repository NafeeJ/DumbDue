package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class IntermediateReceiver : BroadcastReceiver() {
    private lateinit var notificationReceiverIntent: Intent
    private lateinit var notificationPendingIntent : PendingIntent
    private lateinit var alarmManager: AlarmManager
    private var requestCode: Int = 0

    override fun onReceive(context: Context, intent: Intent) {
        requestCode = intent.getIntExtra("requestCode", 0)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        notificationReceiverIntent = Intent(context,NotificationReceiver::class.java) //initializes the intent to start NotificationReceiver activity
        notificationReceiverIntent.putExtra("reminderText",intent.getStringExtra("reminderText"))
        notificationPendingIntent = PendingIntent.getBroadcast(context,requestCode,notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT) //initializes the pending intent to be notification receiver

        setAlarm()
    }

    private fun setAlarm() {
        this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis,60000,notificationPendingIntent) //sets a repeating alarm that repeats every minute
    }

    fun cancelRepeatingAlarms() {
        this.alarmManager.cancel(this.notificationPendingIntent)
    }
}