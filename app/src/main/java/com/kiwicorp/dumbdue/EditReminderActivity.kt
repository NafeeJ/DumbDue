package com.kiwicorp.dumbdue

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_schedule_reminder.*
import java.util.*

class EditReminderActivity : ReminderButtonsBaseActivity() {

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
        val sectionTitle: String = reminderData.sectionTitle
        val indexInSection: Int = reminderData.positionInSection

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
                    dueDateCalendar,repeatVal,sectionTitle,indexInSection))
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