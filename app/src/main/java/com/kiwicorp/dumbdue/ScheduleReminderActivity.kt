package com.kiwicorp.dumbdue

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_schedule_reminder.*
import java.util.*

class ScheduleReminderActivity : AbstractReminderButtonsActivity() {

    private val DATE_PICK_REQUEST: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_schedule_reminder)
        super.onCreate(savedInstanceState)

        val titleEditText: EditText = findViewById(R.id.titleEditText)
        titleEditText.height = screenHeight / 5
        titleEditText.requestFocus() //opens keyboard when window opens

        cancelButton.setOnClickListener{ finish() }
        addButton.setOnClickListener {
            Reminder(titleEditText.text.toString(),dueDateCalendar,repeatVal,applicationContext)
            finish()
        }
        repeatButton.setOnClickListener {
            //create and show popup menu
            val popup = PopupMenu(this,findViewById(R.id.repeatButton))
            popup.inflate(R.menu.repeat_popup_menu)
            popup.show()

            //sets pop up menu's item
            popup.menu.getItem(1).title = "Daily "
                .plus(timeFormatter.format(dueDateCalendar.time))

            popup.menu.getItem(2).title = dayOfWeekFormatter
                .format(dueDateCalendar.get(Calendar.DAY_OF_WEEK))
                .plus("s ")
                .plus(timeFormatter.format(dueDateCalendar.time))

            popup.menu.getItem(3).title = dueDateCalendar
                .get(Calendar.DAY_OF_MONTH).toString()
                .plus(MainActivity.daySuffixFinder(dueDateCalendar))
                .plus(" each month at ")
                .plus(timeFormatter.format(dueDateCalendar.time))

            //change reminder's repeat val based off of which menu item clicked
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_none -> {
                        repeatVal = Reminder.REPEAT_NONE
                        repeatTextView.visibility = View.GONE
                        true
                    }
                    R.id.menu_daily -> {
                        repeatVal = Reminder.REPEAT_DAILY
                        repeatTextView.text =  "Daily "
                            .plus(timeFormatter.format(dueDateCalendar.time))
                        repeatTextView.visibility = View.VISIBLE
                        true
                    }
                    R.id.menu_weekly -> {
                        repeatVal = Reminder.REPEAT_WEEKLY
                        repeatTextView.text = dayOfWeekFormatter
                            .format(dueDateCalendar.get(Calendar.DAY_OF_WEEK))
                            .plus("s ")
                            .plus(timeFormatter.format(dueDateCalendar.time))
                        repeatTextView.visibility = View.VISIBLE
                        true
                    }
                    R.id.menu_monthly -> {
                        repeatVal = Reminder.REPEAT_MONTHLY
                        repeatTextView.text = dueDateCalendar.get(Calendar.DAY_OF_MONTH).toString()
                            .plus(MainActivity.daySuffixFinder(dueDateCalendar))
                            .plus(" each month at ")
                            .plus(timeFormatter.format(dueDateCalendar.time))
                        repeatTextView.visibility = View.VISIBLE
                        true
                    }
                    else -> false
                }
            }
        }

        dateTextView.setOnClickListener {
            val datePickerIntent = Intent(applicationContext, DatePickerActivity::class.java)
            datePickerIntent.putExtra("timeInMillis", dueDateCalendar.timeInMillis)
            startActivityForResult(datePickerIntent, DATE_PICK_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == DATE_PICK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                dueDateCalendar.timeInMillis =
                    data.getLongExtra("newTimeInMillis", dueDateCalendar.timeInMillis)
                updateTextViews()
            }
        }
    }
}