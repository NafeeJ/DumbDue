package com.kiwicorp.dumbdue

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class LockScreenReminderActivity: AbstractReminderButtonsActivity() {
    companion object { const val TAG: String = "LockScreenReminderActivity" }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"Lock Screen Activity Started")
        //todo allow for delaying reminders to actually work
        val reminderDataBundle = intent.getBundleExtra("ReminderDataBundle")
        val reminderData: Reminder.ReminderData = reminderDataBundle.getParcelable("ReminderData") as Reminder.ReminderData

        setContentView(R.layout.activity_lockscreen_reminder)
        super.onCreate(savedInstanceState)

        repeatVal = reminderData.repeatVal
        val reminderText: String = reminderData.text

        val reminderEditText: EditText = findViewById(R.id.titleEditText)
        reminderEditText.height = screenHeight / 5
        reminderEditText.setText(reminderText)

        val markDoneButton: Button = findViewById(R.id.markDoneButton)
        val plus1HrButton: Button = findViewById(R.id.plus1HrButton)
        val plus3HrButton: Button = findViewById(R.id.plus3HrButton)
        val plus1DayButton: Button = findViewById(R.id.plus1DayButton)

        markDoneButton.setOnClickListener {
//            reminder.deleteReminder()
            finish()
        }
        plus1HrButton.setOnClickListener {
//            dueDateCalendar.add(Calendar.HOUR,1)
//            reminder.deleteReminder()
//            Reminder(reminderText,dueDateCalendar,repeatVal,applicationContext)
            finish()
        }
        plus3HrButton.setOnClickListener {
//            dueDateCalendar.add(Calendar.HOUR,3)
//            reminder.deleteReminder()
//            Reminder(reminderText,dueDateCalendar,repeatVal,applicationContext)
            finish()
        }
        plus1DayButton.setOnClickListener {
//            dueDateCalendar.add(Calendar.DAY_OF_YEAR,1)
//            reminder.deleteReminder()
//            Reminder(reminderText,dueDateCalendar,repeatVal,applicationContext)
            finish()
        }
    }
}