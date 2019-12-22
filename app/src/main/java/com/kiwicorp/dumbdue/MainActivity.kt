package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        var notificationID = 0 //used to keep notifications unique thus allowing notifications to stack
        var time: Long = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.button)
        val button2: Button = findViewById(R.id.button2)
        val userTime: EditText = findViewById(R.id.userTime)

        val alarmManager: AlarmManager =  getSystemService(Context.ALARM_SERVICE) as AlarmManager //creates AlarmManager object

        val notificationReceiverIntent = Intent(applicationContext,NotificationReceiver::class.java) //initializes the intent to run NotificationReceiver

        val pendingIntent = PendingIntent.getBroadcast(applicationContext,notificationID,notificationReceiverIntent,PendingIntent.FLAG_UPDATE_CURRENT) //initializes the pending intent to be notification receiver

        button.setOnClickListener(object: View.OnClickListener{//set repeating alarm
            override fun onClick(v: View) {

                //time += userTime.text.toString().toLong() //sets calendar time to be minute from time of button press

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 10000,60000,pendingIntent) //sets a repeating alarm that repeats every minute
            }
        })

        button2.setOnClickListener(object: View.OnClickListener{//stops notifications by canceling pending intents
            override fun onClick(v: View) {
                alarmManager.cancel(pendingIntent)
            }
        })
    }
}

