package com.kiwicorp.dumbdue

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_schedule_reminder.*
import java.util.*

class EditReminderActivity : AbstractReminderButtonsActivity() {

    private val DATE_PICK_REQUEST: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_edit_reminder)
        super.onCreate(savedInstanceState)

        val reminderData: Reminder.ReminderData =
            intent.getParcelableExtra("ReminderData") as Reminder.ReminderData

        dueDateCalendar = reminderData.remindCalendar
        updateTextViews()
        repeatVal = reminderData.repeatVal
        val reminderText: String = reminderData.text

        val addButton: ImageButton = findViewById(R.id.addButton)
        val cancelButton: ImageButton = findViewById(R.id.cancelButton)
        val repeatButton: ImageButton = findViewById(R.id.repeatButton)
        val deleteButton: ImageButton = findViewById(R.id.deleteButton)

        val reminderEditText: EditText = findViewById(R.id.titleEditText)
        reminderEditText.height = screenHeight / 5
        reminderEditText.setText(reminderText)

        cancelButton.setOnClickListener{ setResult(RESULT_CANCELED); finish() }
        addButton.setOnClickListener {
            //stores reminder data in intent to later be retrieved in main activity to apply edits
            intent.putExtra("ReminderData",
                Reminder.ReminderData(reminderEditText.text.toString(),
                    dueDateCalendar,repeatVal,reminderData.requestCode,reminderData.sectionTitle,reminderData.positionInSection
                ))
            //set result
            setResult(RESULT_OK,intent)
            finish()
        }
        deleteButton.setOnClickListener {
            //stores reminder data intent to later be retrieved in main activity's on result
            intent.putExtra("ReminderData", reminderData)
            //set result
            setResult(MainActivity.RESULT_DELETE,intent)
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
            repeatWeeklyTextView.text = dayOfWeekFormatter.format(dueDateCalendar.time)
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
                    .format(dueDateCalendar.time)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//receive result from date picker
        super.onActivityResult(requestCode,resultCode,data)
        // Check which request we're responding to
        if (requestCode == DATE_PICK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                dueDateCalendar.timeInMillis = data.getLongExtra("newTimeInMillis",dueDateCalendar.timeInMillis)
                updateTextViews()
            }
        }
    }
}