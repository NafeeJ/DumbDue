package com.kiwicorp.dumbdue

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_schedule_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

abstract class ReminderButtonsBaseActivity : AppCompatActivity() {

    protected var dueDateCalendar: Calendar = Calendar.getInstance()
    protected var repeatVal: Int = 0

    protected val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a", Locale.US)
    protected val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
    protected val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.US)

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val quickAccessTimesList = getQuickAccessTimes()
        val quickAccessTime1 = quickAccessTimesList[0]
        val quickAccessTime2 = quickAccessTimesList[1]
        val quickAccessTime3 = quickAccessTimesList[2]
        val quickAccessTime4 = quickAccessTimesList[3]

        //gets display metrics of phone screen
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        //sets popup window dimensions based off of display metrics
        screenWidth = (displayMetrics.widthPixels * 0.95).toInt()
        screenHeight = displayMetrics.heightPixels / 3
        window.setLayout(screenWidth, RelativeLayout.LayoutParams.WRAP_CONTENT)

        val repeatTextView: TextView = findViewById(R.id.repeatTextView)
        repeatTextView.visibility = View.GONE

        //initialize all buttons
        val timeSetterButton1: Button = findViewById(R.id.timeSetterButton1)
        val timeSetterButton2: Button = findViewById(R.id.timeSetterButton2)
        val timeSetterButton3: Button = findViewById(R.id.timeSetterButton3)
        val timeSetterButton4: Button = findViewById(R.id.timeSetterButton4)
        val timeSetterButton5: Button = findViewById(R.id.timeSetterButton5)
        val timeSetterButton6: Button = findViewById(R.id.timeSetterButton6)
        val timeSetterButton7: Button = findViewById(R.id.timeSetterButton7)
        val timeSetterButton8: Button = findViewById(R.id.timeSetterButton8)

        //initialize preset buttons and their intended hour and minutes
        //Later allow user to change time presets
        val quickAccessButton1: Button = findViewById(R.id.quickAccessButton1)
        val quickAccessTime1HourOfDay = quickAccessTime1.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime1Min = quickAccessTime1.get(Calendar.MINUTE)
        quickAccessButton1.text = timeFormatter.format(quickAccessTime1.time)

        val quickAccessButton2: Button = findViewById(R.id.quickAccessButton2)
        val quickAccessTime2HourOfDay = quickAccessTime2.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime2Min = quickAccessTime2.get(Calendar.MINUTE)
        quickAccessButton2.text = timeFormatter.format(quickAccessTime2.time)

        val quickAccessButton3: Button = findViewById(R.id.quickAccessButton3)
        val quickAccessTime3HourOfDay = quickAccessTime3.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime3Min = quickAccessTime3.get(Calendar.MINUTE)
        quickAccessButton3.text = timeFormatter.format(quickAccessTime3.time)

        val quickAccessButton4: Button = findViewById(R.id.quickAccessButton4)
        val quickAccessTime4HourOfDay = quickAccessTime4.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime4Min = quickAccessTime4.get(Calendar.MINUTE)
        quickAccessButton4.text = timeFormatter.format(quickAccessTime4.time)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        updateTextViews() //sets text to be the formatted intended schedule date
        dateTextView.gravity = Gravity.CENTER_VERTICAL //sets text to be in the middle of text view
        dateTextView.height = screenHeight / 5

        //list of time incrementing/decrementing button
        val timeButtons = listOf(
            timeSetterButton1, timeSetterButton2, timeSetterButton3,
            timeSetterButton4, timeSetterButton5, timeSetterButton6,
            timeSetterButton7, timeSetterButton8, quickAccessButton4,
            quickAccessButton2, quickAccessButton3, quickAccessButton4)
        for (button in timeButtons) { //sets time buttons dimensions
            button.width = screenWidth / 4
            button.height = screenHeight / 5
        }

        //adds or subtracts intended unit to intended due date
        timeSetterButton1.setOnClickListener{
            dueDateCalendar.add(Calendar.MINUTE,10)
            updateTextViews()
        }
        timeSetterButton2.setOnClickListener{
            dueDateCalendar.add(Calendar.MINUTE,-10)
            updateTextViews()
        }
        timeSetterButton3.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,1)
            updateTextViews()
        }
        timeSetterButton4.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,-1)
            updateTextViews()
        }
        timeSetterButton5.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,3)
            updateTextViews()
        }
        timeSetterButton6.setOnClickListener{
            dueDateCalendar.add(Calendar.HOUR,-3)
            updateTextViews()
        }
        timeSetterButton7.setOnClickListener{
            dueDateCalendar.add(Calendar.DAY_OF_YEAR,1)
            updateTextViews()
        }
        timeSetterButton8.setOnClickListener{
            dueDateCalendar.add(Calendar.DAY_OF_YEAR,-1)
            updateTextViews()
        }
        //sets all preset button functionality
        quickAccessButton1.setOnClickListener {
            dueDateCalendar.set(
                dueDateCalendar.get(Calendar.YEAR),
                dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DATE),
                quickAccessTime1HourOfDay,
                quickAccessTime1Min)
            updateTextViews()
        }
        quickAccessButton2.setOnClickListener {
            dueDateCalendar.set(
                dueDateCalendar.get(Calendar.YEAR),
                dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DATE),
                quickAccessTime2HourOfDay,
                quickAccessTime2Min)
            updateTextViews()
        }
        quickAccessButton3.setOnClickListener {
            dueDateCalendar.set(dueDateCalendar.get(Calendar.YEAR),
                dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DATE),
                quickAccessTime3HourOfDay,
                quickAccessTime3Min)
            updateTextViews()
        }
        quickAccessButton4.setOnClickListener {
            dueDateCalendar.set(dueDateCalendar.get(Calendar.YEAR),
                dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DATE),
                quickAccessTime4HourOfDay,
                quickAccessTime4Min)
            updateTextViews()
        }
    }

    //returns a list of calendars each with the hour and minute of a preset time
    private fun getQuickAccessTimes(): List<Calendar> {
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
        //get quick access times strings from shared preferences
        val sharedPreferences: SharedPreferences = PreferenceManager(applicationContext).sharedPreferences
        val quickAccess1String: String = sharedPreferences.getString("quickAccess1", "8:00") as String
        val quickAccess2String: String = sharedPreferences.getString("quickAccess2", "12:00") as String
        val quickAccess3String: String = sharedPreferences.getString("quickAccess3", "17:00") as String
        val quickAccess4String: String = sharedPreferences.getString("quickAccess4", "22:00") as String

        val quickAccess1Calendar = Calendar.getInstance()
        val quickAccess2Calendar = Calendar.getInstance()
        val quickAccess3Calendar = Calendar.getInstance()
        val quickAccess4Calendar = Calendar.getInstance()

        quickAccess1Calendar.time = timeFormatter.parse(quickAccess1String) as Date
        quickAccess2Calendar.time = timeFormatter.parse(quickAccess2String) as Date
        quickAccess3Calendar.time = timeFormatter.parse(quickAccess3String) as Date
        quickAccess4Calendar.time = timeFormatter.parse(quickAccess4String) as Date

        return listOf<Calendar>(quickAccess1Calendar,quickAccess2Calendar,quickAccess3Calendar,quickAccess4Calendar)
    }
    protected fun updateTextViews() { //updates text view

        val fromNowMins = MainActivity.findTimeFromNowMins(dueDateCalendar)
        //updates repeatTextView's text
        when(repeatVal) {
            Reminder.REPEAT_DAILY -> repeatTextView.text =
                "Daily ".plus(timeFormatter.format(dueDateCalendar.time))

            Reminder.REPEAT_WEEKLY -> repeatTextView.text = dayOfWeekFormatter
                .format(dueDateCalendar.get(Calendar.DAY_OF_WEEK))
                .plus("s ")
                .plus(timeFormatter.format(dueDateCalendar.time))

            Reminder.REPEAT_MONTHLY -> repeatTextView.text = dayOfWeekFormatter
                .format(dueDateCalendar.get(Calendar.DAY_OF_WEEK))
                .plus("s ")
                .plus(timeFormatter.format(dueDateCalendar.time))
        }
        //if time from now is positive or the same, updates text to be in format:
        // "Date in fromNowMins (units)" and sets grey background color
        if (fromNowMins >= 0) {
            dateTextView.text = dateFormatter.format(dueDateCalendar.time)
                .plus(" in ")
                .plus(findTimeFromNowString(fromNowMins))
            dateTextView.setBackgroundColor(Color.parseColor("#303030"))
            repeatTextView.setBackgroundColor(Color.parseColor("#303030"))
        }
        //if time from now is negative, updates text to be in format:
        //"Date fromNowMins (units) ago" and sets red background color
        else {
            dateTextView.text = dateFormatter.format(dueDateCalendar.time)
                .plus(" ")
                .plus(findTimeFromNowString(fromNowMins))
                .plus(" ago")
            dateTextView.setBackgroundColor(Color.parseColor("#f54242"))
            repeatTextView.setBackgroundColor(Color.parseColor("#f54242"))
        }
    }
    //returns a string with absolute value of time from now and its correct unit
    protected fun findTimeFromNowString(timeInMins: Int): String {
        val absTime = timeInMins.absoluteValue

        return when {
            absTime == 0 -> { "0 Minutes" } //less than 1 minute
            absTime == 1 -> { absTime.toString().plus(" Minute") } //equal to 1 minute
            absTime < 60 -> { absTime.toString().plus(" Minutes") } //less than 1 hour
            absTime / 60 == 1 -> { (absTime / 60).toString().plus(" Hour")}//equal to 1 hour
            absTime / 60 < 24 -> { (absTime / 60).toString().plus(" Hours") } //less than 1 day
            absTime / 60 / 24 == 1 -> { (absTime / 60 / 24).toString().plus(" Day") } //equal to 1 day
            absTime / 60 / 24 < 7 -> { (absTime / 60 / 24).toString().plus(" Days") } //less than 1 week
            absTime / 60 / 24 / 7 == 1 -> { (absTime / 60 / 24 / 7).toString().plus(" Week") } //equal to 1 week
            absTime / 60 / 24 / 7 < 4 -> { (absTime / 60 / 24 / 7).toString().plus(" Weeks") } //less than 1 month
            absTime / 60 / 24 / 7 / 4 == 1 -> { (absTime / 60 / 24 / 7 / 4).toString().plus(" Month") } //equal to 1 month
            absTime / 60 / 24 / 7 / 4 < 12 -> { (absTime / 60 / 24 / 7 / 4).toString().plus(" Months") } //less than one year
            absTime / 60 / 24 / 7 / 4 / 12 == 1 -> { (absTime / 60 / 24 / 7 / 4 / 12).toString().plus(" Year") } //equal to 1 year
            else -> (absTime / 60 / 24 / 7 / 4 / 12).toString().plus(" Years")
        }
    }
}