package com.kiwicorp.dumbdue

import android.os.Bundle
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class SchedulingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scheduling_activity_layout)

        val scheduleDateCalendar: Calendar = Calendar.getInstance() //Calendar with the intended date of notification

        //get display metrics of phone screen
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        //sets popup window dimensions based off of display metrics
        val width: Int = (displayMetrics.widthPixels * 0.95).toInt()
        val height: Int = displayMetrics.heightPixels / 3
        window.setLayout(width,height)

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
        val buttonPreset1: Button = findViewById(R.id.presetButton1); val preset1HourOfDay = 9; val preset1Min = 30
        val buttonPreset2: Button = findViewById(R.id.presetButton2); val preset2HourOfDay = 12; val preset2Min = 0
        val buttonPreset3: Button = findViewById(R.id.presetButton3); val preset3HourOfDay = 18; val preset3Min = 30
        val buttonPreset4: Button = findViewById(R.id.presetButton4); val preset4HourOfDay = 22; val preset4Min = 0

        val dateFormat = SimpleDateFormat("EEE, d MMM, h:mm a")//initializes the date format

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        dateTextView.text = dateFormat.format(scheduleDateCalendar.time) //sets text to be the formatted intended schedule date
        dateTextView.gravity = Gravity.CENTER_VERTICAL //sets text to be in the middle of text view
        dateTextView.height = height / 5

        val taskEditText: EditText = findViewById(R.id.taskEditText)
        taskEditText.height = height / 5


        val timeButtons = listOf(buttonPlus10min,buttonMinus10min,buttonPlus1hr,buttonMinus1hr,buttonPlus3hr,buttonMinus3hr,buttonPlus1day,buttonMinus1day,buttonPreset1,buttonPreset2,buttonPreset3,buttonPreset4) //list of all time buttons
        for (button in timeButtons) {//sets time buttons dimensions
            button.width = width / 4
            button.height = height / 5
        }

        //sets all plus minus button functionality
        buttonPlus10min.setOnClickListener{
            scheduleDateCalendar.set(Calendar.MINUTE, scheduleDateCalendar.get(Calendar.MINUTE) + 10)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonMinus10min.setOnClickListener{
            scheduleDateCalendar.set(Calendar.MINUTE, scheduleDateCalendar.get(Calendar.MINUTE) - 10)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonPlus1hr.setOnClickListener{
            scheduleDateCalendar.set(Calendar.HOUR, scheduleDateCalendar.get(Calendar.HOUR) + 1)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonMinus1hr.setOnClickListener{
            scheduleDateCalendar.set(Calendar.HOUR, scheduleDateCalendar.get(Calendar.HOUR) - 1)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonPlus3hr.setOnClickListener{
            scheduleDateCalendar.set(Calendar.HOUR, scheduleDateCalendar.get(Calendar.HOUR) + 3)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonMinus3hr.setOnClickListener{
            scheduleDateCalendar.set(Calendar.HOUR, scheduleDateCalendar.get(Calendar.HOUR) - 3)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonPlus1day.setOnClickListener{
            scheduleDateCalendar.set(Calendar.DAY_OF_YEAR, scheduleDateCalendar.get(Calendar.DAY_OF_YEAR) + 1)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonMinus1day.setOnClickListener{
            scheduleDateCalendar.set(Calendar.DAY_OF_YEAR, scheduleDateCalendar.get(Calendar.DAY_OF_YEAR) - 1)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        //sets all preset button functionality
        buttonPreset1.setOnClickListener {
            scheduleDateCalendar.set(scheduleDateCalendar.get(Calendar.YEAR),scheduleDateCalendar.get(Calendar.MONTH),scheduleDateCalendar.get(Calendar.DATE),preset1HourOfDay,preset1Min)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonPreset2.setOnClickListener {
            scheduleDateCalendar.set(scheduleDateCalendar.get(Calendar.YEAR),scheduleDateCalendar.get(Calendar.MONTH),scheduleDateCalendar.get(Calendar.DATE),preset2HourOfDay,preset2Min)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonPreset3.setOnClickListener {
            scheduleDateCalendar.set(scheduleDateCalendar.get(Calendar.YEAR),scheduleDateCalendar.get(Calendar.MONTH),scheduleDateCalendar.get(Calendar.DATE),preset3HourOfDay,preset3Min)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
        buttonPreset4.setOnClickListener {
            scheduleDateCalendar.set(scheduleDateCalendar.get(Calendar.YEAR),scheduleDateCalendar.get(Calendar.MONTH),scheduleDateCalendar.get(Calendar.DATE),preset4HourOfDay,preset4Min)
            dateTextView.text = dateFormat.format(scheduleDateCalendar.time)
        }
    }
}