package com.kiwicorp.dumbdue

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.layout_schedule_reminder_activity.*
import java.text.SimpleDateFormat
import java.util.*

class EditReminderActivity : Activity() {
    private var repeatVal: Int = Reminder.REPEAT_NONE

    private val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a") //creates a date format
    private val timeFormatter = SimpleDateFormat("h:mm a")
    private val dayOfWeekFormatter = SimpleDateFormat("EEEE")

    private lateinit var dueDateCalendar: Calendar //Calendar with the intended date of notification

    private val DATE_PICK_REQUEST: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_edit_reminder_activity)

        val reminderData: Reminder.ReminderData = intent.getParcelableExtra("ReminderData") as Reminder.ReminderData

        dueDateCalendar = reminderData.remindCalendar
        repeatVal = reminderData.repeatVal
        val reminderText: String = reminderData.text
        val index: Int = reminderData.index

        //gets display metrics of phone screen
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        //sets popup window dimensions based off of display metrics
        val width: Int = (displayMetrics.widthPixels * 0.95).toInt()
        val height: Int = displayMetrics.heightPixels / 3
        window.setLayout(width,RelativeLayout.LayoutParams.WRAP_CONTENT)

        val repeatTextView: TextView = findViewById(R.id.repeatTextView)
        repeatTextView.visibility = View.GONE

        //initialize all buttons
        val buttonPlus10min: Button = findViewById(R.id.plus10minbutton)
        val buttonMinus10min: Button = findViewById(R.id.minus10minbutton)
        val buttonPlus1hr: Button = findViewById(R.id.plus1hrbutton)
        val buttonMinus1hr: Button = findViewById(R.id.minus1hrbutton)
        val buttonPlus3hr: Button = findViewById(R.id.plus3hrbutton)
        val buttonMinus3hr: Button = findViewById(R.id.minus3hrbutton)
        val buttonPlus1day: Button = findViewById(R.id.plus1daybutton)
        val buttonMinus1day: Button = findViewById(R.id.minus1daybutton)

        //initialize preset buttons and their intended hour and minutes
        //Later allow user to change time presets
        val buttonPreset1: Button = findViewById(R.id.presetButton1); val preset1HourOfDay = 9; val preset1Min = 30
        val buttonPreset2: Button = findViewById(R.id.presetButton2); val preset2HourOfDay = 12; val preset2Min = 0
        val buttonPreset3: Button = findViewById(R.id.presetButton3); val preset3HourOfDay = 18; val preset3Min = 30
        val buttonPreset4: Button = findViewById(R.id.presetButton4); val preset4HourOfDay = 22; val preset4Min = 0

        val addButton: ImageButton = findViewById(R.id.addButton)
        val cancelButton: ImageButton = findViewById(R.id.cancelButton)
        val repeatButton: ImageButton = findViewById(R.id.repeatButton)
        val deleteButton: ImageButton = findViewById(R.id.deleteButton)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        updateDateTextView(dueDateCalendar) //sets text to be the formatted intended schedule date
        dateTextView.gravity = Gravity.CENTER_VERTICAL //sets text to be in the middle of text view
        dateTextView.height = height / 5

        val reminderEditText: EditText = findViewById(R.id.taskEditText)
        reminderEditText.height = height / 5
        reminderEditText.setText(reminderText)


        val timeButtons = listOf(buttonPlus10min,buttonMinus10min,buttonPlus1hr,buttonMinus1hr,buttonPlus3hr,buttonMinus3hr,buttonPlus1day,buttonMinus1day,buttonPreset1,buttonPreset2,buttonPreset3,buttonPreset4) //list of all time buttons
        for (button in timeButtons) { //sets time buttons dimensions
            button.width = width / 4
            button.height = height / 5
        }

        //adds or subtracts intended unit to intended due date
        buttonPlus10min.setOnClickListener{
            dueDateCalendar.add(Calendar.MINUTE,10)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus10min.setOnClickListener{
            dueDateCalendar.add(Calendar.MINUTE,-10)
            updateDateTextView(dueDateCalendar)
        }
        buttonPlus1hr.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,1)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus1hr.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,-1)
            updateDateTextView(dueDateCalendar)
        }
        buttonPlus3hr.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,3)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus3hr.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,-3)
            updateDateTextView(dueDateCalendar)
        }
        buttonPlus1day.setOnClickListener{
            dueDateCalendar.add(Calendar.DAY_OF_YEAR,1)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus1day.setOnClickListener{
            dueDateCalendar.add(Calendar.DAY_OF_YEAR,-1)
            updateDateTextView(dueDateCalendar)
        }
        //preset buttons set calendar to intended time on provided day
        buttonPreset1.setOnClickListener {
            dueDateCalendar.set(dueDateCalendar.get(Calendar.YEAR),dueDateCalendar.get(Calendar.MONTH),dueDateCalendar.get(Calendar.DATE),preset1HourOfDay,preset1Min)
            updateDateTextView(dueDateCalendar)
        }
        buttonPreset2.setOnClickListener {
            dueDateCalendar.set(dueDateCalendar.get(Calendar.YEAR),dueDateCalendar.get(Calendar.MONTH),dueDateCalendar.get(Calendar.DATE),preset2HourOfDay,preset2Min)
            updateDateTextView(dueDateCalendar)
        }
        buttonPreset3.setOnClickListener {
            dueDateCalendar.set(dueDateCalendar.get(Calendar.YEAR),dueDateCalendar.get(Calendar.MONTH),dueDateCalendar.get(Calendar.DATE),preset3HourOfDay,preset3Min)
            updateDateTextView(dueDateCalendar)
        }
        buttonPreset4.setOnClickListener {
            dueDateCalendar.set(dueDateCalendar.get(Calendar.YEAR),dueDateCalendar.get(Calendar.MONTH),dueDateCalendar.get(Calendar.DATE),preset4HourOfDay,preset4Min)
            updateDateTextView(dueDateCalendar)
        }

        cancelButton.setOnClickListener{
            setResult(RESULT_CANCELED)
            finish()
        }
        addButton.setOnClickListener {
            intent.putExtra("ReminderData",Reminder.ReminderData(reminderEditText.text.toString(),dueDateCalendar,repeatVal,index))
            setResult(RESULT_OK,intent)
            finish()
        }
        deleteButton.setOnClickListener {
            intent.putExtra("ReminderData", reminderData)
            setResult(MainActivity.RESULT_DELETE,intent)
            finish()
        }
        repeatButton.setOnClickListener {
            //create and show popup menu
            val popup = PopupMenu(this,findViewById(R.id.repeatButton))
            popup.inflate(R.menu.repeat_popup_menu)
            popup.show()

            //sets item text
            popup.menu.getItem(1).title = "Daily ".plus(timeFormatter.format(dueDateCalendar.time))
            popup.menu.getItem(2).title = dayOfWeekFormatter.format(dueDateCalendar.get(Calendar.DAY_OF_WEEK)).plus("s ").plus(timeFormatter.format(dueDateCalendar.time))
            popup.menu.getItem(3).title = dueDateCalendar.get(Calendar.DAY_OF_MONTH).toString().plus(MainActivity.daySuffixFinder(dueDateCalendar)).plus(" each month at ").plus(timeFormatter.format(dueDateCalendar.time))
            //changes repeatVal based off of which menu item clicked
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_none -> {
                        repeatVal = Reminder.REPEAT_NONE
                        repeatTextView.visibility = View.GONE
                        true
                    }
                    R.id.menu_daily -> {
                        repeatVal = Reminder.REPEAT_DAILY
                        repeatTextView.text =  "Daily ".plus(timeFormatter.format(dueDateCalendar.time))
                        repeatTextView.visibility = View.VISIBLE
                        true
                    }
                    R.id.menu_weekly -> {
                        repeatVal = Reminder.REPEAT_WEEKLY
                        repeatTextView.text = dayOfWeekFormatter.format(dueDateCalendar.get(Calendar.DAY_OF_WEEK)).plus("s ").plus(timeFormatter.format(dueDateCalendar.time))
                        repeatTextView.visibility = View.VISIBLE
                        true
                    }
                    R.id.menu_monthly -> {
                        repeatVal = Reminder.REPEAT_MONTHLY
                        repeatTextView.text = dueDateCalendar.get(Calendar.DAY_OF_MONTH).toString().plus(MainActivity.daySuffixFinder(dueDateCalendar)).plus(" each month at ").plus(timeFormatter.format(dueDateCalendar.time))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {//receive result from date picker
        // Check which request we're responding to
        if (requestCode == DATE_PICK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                dueDateCalendar.timeInMillis = data.getLongExtra("newTimeInMillis",dueDateCalendar.timeInMillis)
                updateDateTextView(dueDateCalendar)
            }
        }
    }
    private fun updateDateTextView(calendar: Calendar) { //updates text view

        val fromNowMins = MainActivity.findTimeFromNowMins(calendar)

        //updates repeatTextView's text
        if (repeatVal == Reminder.REPEAT_DAILY) {
            repeatTextView.text =  "Daily ".plus(timeFormatter.format(calendar.time))
        } else if (repeatVal == Reminder.REPEAT_WEEKLY) {
            repeatTextView.text = dayOfWeekFormatter.format(calendar.get(Calendar.DAY_OF_WEEK)).plus("s ").plus(timeFormatter.format(calendar.time))
        } else if (repeatVal == Reminder.REPEAT_MONTHLY) {
            repeatTextView.text = calendar.get(Calendar.DAY_OF_MONTH).toString().plus(MainActivity.daySuffixFinder(calendar)).plus(" each month at ").plus(timeFormatter.format(calendar.time))
        }

        if (fromNowMins >= 0) { //if time from now is positive or the same, updates text to be in format: "Date in fromNowMins (units)" and sets grey background color
            dateTextView.text = dateFormatter.format(calendar.time).plus(" in ").plus(MainActivity.findTimeFromNowString(fromNowMins))
            dateTextView.setBackgroundColor(Color.parseColor("#303030"))
            repeatTextView.setBackgroundColor(Color.parseColor("#303030"))
        }
        else { //if time from now is negative, updates text to be in format: "Date fromNowMins (units) ago" and sets red background color
            dateTextView.text = dateFormatter.format(calendar.time).plus(" ").plus(MainActivity.findTimeFromNowString(fromNowMins)).plus(" ago")
            dateTextView.setBackgroundColor(Color.parseColor("#f54242"))
            repeatTextView.setBackgroundColor(Color.parseColor("#f54242"))
        }
    }

}