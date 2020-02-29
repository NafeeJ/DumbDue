package com.kiwicorp.dumbdue

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_schedule_reminder.*
import kotlinx.android.synthetic.main.dialog_choose_repeat.*
import org.w3c.dom.Text
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
            //create and show dialog
            val dialog = BottomSheetDialog(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_choose_repeat,null)
            dialog.setContentView(dialogView)
            dialog.setCanceledOnTouchOutside(true)

            //widgets
            val repeatOffTextView: TextView = dialogView.findViewById(R.id.repeatOffText)
            val repeatDailyTextView: TextView = dialogView.findViewById(R.id.repeatDailyText)
            val repeatWeekdaysTextView: TextView = dialogView.findViewById(R.id.repeatWeekdays)
            val repeatWeeklyTextView: TextView = dialogView.findViewById(R.id.repeatWeekly)
            val repeatMonthlyTextView: TextView = dialogView.findViewById(R.id.repeatMonthly)

            //set text views text
            repeatOffTextView.text = "Repeat Off"
            repeatDailyTextView.text = "Daily ".plus(timeFormatter.format(dueDateCalendar.time))
            repeatWeekdaysTextView.text = "Weekdays ".plus(timeFormatter.format(dueDateCalendar.time))
            repeatWeeklyTextView.text = dayOfWeekFormatter.format(dueDateCalendar.get(Calendar.DAY_OF_WEEK))
                .plus("s ")
                .plus(timeFormatter.format(dueDateCalendar.time))
            repeatMonthlyTextView.text = dueDateCalendar
                .get(Calendar.DAY_OF_MONTH).toString()
                .plus(MainActivity.daySuffixFinder(dueDateCalendar))
                .plus(" each month at ")
                .plus(timeFormatter.format(dueDateCalendar.time))

            repeatOffTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_NONE
                repeatTextView.visibility = View.GONE
                dialog.dismiss()
            }
            repeatDailyTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_DAILY
                repeatTextView.text =  "Daily "
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            repeatWeekdaysTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_WEEKDAYS
                repeatTextView.text = "Weekdays "
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            repeatWeeklyTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_WEEKLY
                repeatTextView.text = dayOfWeekFormatter
                    .format(dueDateCalendar.get(Calendar.DAY_OF_WEEK))
                    .plus("s ")
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            repeatMonthlyTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_MONTHLY
                repeatTextView.text = dueDateCalendar.get(Calendar.DAY_OF_MONTH).toString()
                    .plus(MainActivity.daySuffixFinder(dueDateCalendar))
                    .plus(" each month at ")
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }

            dialog.show()
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