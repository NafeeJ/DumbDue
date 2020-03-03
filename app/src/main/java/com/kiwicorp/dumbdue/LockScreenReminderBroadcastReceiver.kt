package com.kiwicorp.dumbdue

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LockScreenReminderBroadcastReceiver: BroadcastReceiver() {
    companion object{const val TAG: String = "LockScreenReminderBroadcastReceiver"}

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(TAG,"Broadcast Received")

        if (intent!!.action ==("com.dumbdue.LockScreenReminderBroadcastReceiver")) {
            val lockScreenIntent = Intent(context,LockScreenReminderActivity::class.java)
            lockScreenIntent.putExtra("ReminderDataBundle",intent.getBundleExtra("ReminderDataBundle"))
            lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(lockScreenIntent)
        }

    }

}