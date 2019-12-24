package com.kiwicorp.dumbdue

import android.os.Bundle
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.scheduling_activity_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import android.graphics.Color;

class SchedulingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scheduling_activity_layout)

        val dueDateCalendar: Calendar = Calendar.getInstance() //Calendar with the intended date of notification

        //gets display metrics of phone screen
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
        //Later allow user to change time presets
        val buttonPreset1: Button = findViewById(R.id.presetButton1); val preset1HourOfDay = 9; val preset1Min = 30
        val buttonPreset2: Button = findViewById(R.id.presetButton2); val preset2HourOfDay = 12; val preset2Min = 0
        val buttonPreset3: Button = findViewById(R.id.presetButton3); val preset3HourOfDay = 18; val preset3Min = 30
        val buttonPreset4: Button = findViewById(R.id.presetButton4); val preset4HourOfDay = 22; val preset4Min = 0

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        updateDateTextView(dueDateCalendar) //sets text to be the formatted intended schedule date
        dateTextView.gravity = Gravity.CENTER_VERTICAL //sets text to be in the middle of text view
        dateTextView.height = height / 5

        val taskEditText: EditText = findViewById(R.id.taskEditText)
        taskEditText.height = height / 5


        val timeButtons = listOf(buttonPlus10min,buttonMinus10min,buttonPlus1hr,buttonMinus1hr,buttonPlus3hr,buttonMinus3hr,buttonPlus1day,buttonMinus1day,buttonPreset1,buttonPreset2,buttonPreset3,buttonPreset4) //list of all time buttons
        for (button in timeButtons) {//sets time buttons dimensions
            button.width = width / 4
            button.height = height / 5
        }

        //adds or subtracts intended unit to intended due date
        buttonPlus10min.setOnClickListener{
            dueDateCalendar.set(Calendar.MINUTE, dueDateCalendar.get(Calendar.MINUTE) + 10)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus10min.setOnClickListener{
            dueDateCalendar.set(Calendar.MINUTE, dueDateCalendar.get(Calendar.MINUTE) - 10)
            updateDateTextView(dueDateCalendar)
        }
        buttonPlus1hr.setOnClickListener{
            dueDateCalendar.set(Calendar.HOUR, dueDateCalendar.get(Calendar.HOUR) + 1)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus1hr.setOnClickListener{
            dueDateCalendar.set(Calendar.HOUR, dueDateCalendar.get(Calendar.HOUR) - 1)
            updateDateTextView(dueDateCalendar)
        }
        buttonPlus3hr.setOnClickListener{
            dueDateCalendar.set(Calendar.HOUR, dueDateCalendar.get(Calendar.HOUR) + 3)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus3hr.setOnClickListener{
            dueDateCalendar.set(Calendar.HOUR, dueDateCalendar.get(Calendar.HOUR) - 3)
            updateDateTextView(dueDateCalendar)
        }
        buttonPlus1day.setOnClickListener{
            dueDateCalendar.set(Calendar.DAY_OF_YEAR, dueDateCalendar.get(Calendar.DAY_OF_YEAR) + 1)
            updateDateTextView(dueDateCalendar)
        }
        buttonMinus1day.setOnClickListener{
            dueDateCalendar.set(Calendar.DAY_OF_YEAR, dueDateCalendar.get(Calendar.DAY_OF_YEAR) - 1)
            updateDateTextView(dueDateCalendar)
        }
        //sets all preset button functionality
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
    }

    private fun updateDateTextView(dueDateCalendar: Calendar) {//updates text view
        var fromNowMins: Int = dueDateCalendar.get(Calendar.MINUTE) - Calendar.getInstance().get(Calendar.MINUTE)
        val fromNowHours: Int = dueDateCalendar.get(Calendar.HOUR_OF_DAY) - Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val fromNowDays: Int = dueDateCalendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val fromNowYears: Int = dueDateCalendar.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR)

        fromNowMins += (fromNowHours * 60) + (fromNowDays * 24 * 60) + (fromNowYears * 525600)

        val dateFormat = SimpleDateFormat("EEE, d MMM, h:mm a") //creates a date format

        if (fromNowMins >= 0) { //if time from now is positive, updates text to be in format: "Date in fromNowMins units" and sets grey background color
            dateTextView.text = dateFormat.format(dueDateCalendar.time).plus(" in ").plus(findTimeFromNowString(fromNowMins))
            dateTextView.setBackgroundColor(Color.parseColor("#383838"))
        } else { //if time from now is negative, updates text to be in format: "Date fromNowMins units ago" and sets red background color
            dateTextView.text = dateFormat.format(dueDateCalendar.time).plus(" ").plus(findTimeFromNowString(fromNowMins)).plus(" ago")
            dateTextView.setBackgroundColor(Color.parseColor("#ad0000"))
        }
    }

    private fun findTimeFromNowString(timeInMins: Int): String { //returns a string with absolute value of time from now and its correct unit
        if (timeInMins.absoluteValue == 0) { return "0 Minutes" }//less than 1 minute
        else if (timeInMins.absoluteValue == 1) { return timeInMins.absoluteValue.toString().plus(" Minute") } //equal to 1 minute
        else if (timeInMins.absoluteValue < 60) { return timeInMins.absoluteValue.toString().plus(" Minutes") } //less than 1 hour
        else if ((timeInMins.absoluteValue / 60) == 1) { return (timeInMins.absoluteValue / 60).toString().plus(" Hour") } //equal to 1 hour
        else if ((timeInMins.absoluteValue / 60) < 24 ) { return (timeInMins.absoluteValue / 60).toString().plus(" Hours") } //less than 1 day
        else if ((timeInMins.absoluteValue / 60 / 24) == 1) { return (timeInMins.absoluteValue / 60 / 24).toString().plus(" Day") } //equal to 1 day
        else if ((timeInMins.absoluteValue / 60 / 24) < 7) { return (timeInMins.absoluteValue / 60 / 24).toString().plus(" Days") } //less than 1 week
        else if ((timeInMins.absoluteValue / 60 / 24 / 7) == 1) { return (timeInMins.absoluteValue / 60 / 24 / 7).toString().plus(" Week") } //equal to 1 week
        else if ((timeInMins.absoluteValue / 60 / 24 / 7) < 4) { return (timeInMins.absoluteValue / 60 / 24 / 7).toString().plus(" Weeks") } //less than 1 month
        else if ((timeInMins.absoluteValue / 60 / 24 / 7 / 4) == 1) { return (timeInMins.absoluteValue / 60 / 24 / 7 / 4).toString().plus(" Month") } //equal to 1 month
        else if ((timeInMins.absoluteValue / 60 / 24 / 7 / 4) < 12) { return (timeInMins.absoluteValue / 60 / 24 / 7 / 4).toString().plus(" Months") } //less than one year
        else if ((timeInMins.absoluteValue / 60 / 24 / 7 / 4 / 12) == 1) { return (timeInMins.absoluteValue / 60 / 24 / 7 / 4 / 12).toString().plus(" Year") } //equal to 1 year
        else return (timeInMins.absoluteValue / 60 / 24 / 7 / 4 / 12).toString().plus(" Years")
    }
}