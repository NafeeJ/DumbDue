package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class IntermediateReceiver(requestCode: Int) : BroadcastReceiver() {
    private lateinit var notificationReceiverIntent: Intent
    private lateinit var pendingIntent : PendingIntent
    private lateinit var alarmManager: AlarmManager
    private var requestCode: Int = 0

    init {
        this.requestCode = requestCode
    }

    override fun onReceive(context: Context, intent: Intent) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationReceiverIntent = Intent(context,NotificationReceiver::class.java) //initializes the intent to start NotificationReceiver activity
        pendingIntent = PendingIntent.getBroadcast(context,this.requestCode,notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT) //initializes the pending intent to be notification receiver
        setAlarm()
    }

    private fun setAlarm() {
        this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis,60000,pendingIntent) //sets a repeating alarm that repeats every minute
    }

    fun cancelAlarm() {
        this.alarmManager.cancel(this.pendingIntent)
    }
}