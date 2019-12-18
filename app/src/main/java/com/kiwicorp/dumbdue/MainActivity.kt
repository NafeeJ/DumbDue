package com.kiwicorp.dumbdue

import android.app.Notification
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.GregorianCalendar

class MainActivity : AppCompatActivity() {
    private lateinit var notificationManger: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManger = NotificationManagerCompat.from(this)
    }

    fun sendOnChannel1(view: View) {
        val message = "I am the message"
        val title = "I am the title"

        val notification1 : Notification = NotificationCompat.Builder(this, NotificationTest.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentText(message)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManger.notify(1, notification1)


    }

    fun alertNotification(view: View) {
        var alertTime: Long = GregorianCalendar().getTimeInMillis() + 5 * 1000

    }
}

