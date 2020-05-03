package com.kiwicorp.dumbdue

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_schedule_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

abstract class AbstractReminderButtonsActivity : AppCompatActivity() {

    protected var dueDateCalendar: Calendar = Calendar.getInstance()
    protected var repeatVal: Int = 0

    protected val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a", Locale.US)
    protected val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
    protected val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.US)

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val quickAccessButton1: Button = findViewById(R.id.quickAccessButton1)
        val quickAccessButton2: Button = findViewById(R.id.quickAccessButton2)
        val quickAccessButton3: Button = findViewById(R.id.quickAccessButton3)
        val quickAccessButton4: Button = findViewById(R.id.quickAccessButton4)

        //list of time incrementing/decrementing button
        val timeButtons = listOf(
            timeSetterButton1, timeSetterButton2, timeSetterButton3,
            timeSetterButton4, timeSetterButton5, timeSetterButton6,
            timeSetterButton7, timeSetterButton8, quickAccessButton1,
            quickAccessButton2, quickAccessButton3, quickAccessButton4)

        val timeSetterButtons = timeButtons.subList(0,8)
        val quickAccessButtons = timeButtons.subList(8,12)

        TimeSetterButtonsPreferenceActivity.loadTimeSetterButtonTexts(applicationContext,timeSetterButtons)
        TimeSetterButtonsPreferenceActivity.loadQuickAccessButtonTexts(applicationContext,quickAccessButtons)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        updateTextViews() //sets text to be the formatted intended schedule date
        dateTextView.gravity = Gravity.CENTER_VERTICAL //sets text to be in the middle of text view
        dateTextView.height = screenHeight / 5

        for (button in timeButtons) { //sets time buttons dimensions
            button.width = screenWidth / 4
            button.height = screenHeight / 5
        }
        //sets on click listeners for time setters
        for (button in timeSetterButtons) {
            button.setOnClickListener {
                //update due date calendar
                /* number is the actual number of how much to increment/decrement,
                notDigits contains "+ unit" */
                val (number,notDigits)= button.text.partition { it.isDigit() }
                val unit: String = notDigits.substring(2)  //contains the unit of the text
                val unitInt: Int = when (unit) {
                    "min" -> Calendar.MINUTE
                    "hr" -> Calendar.HOUR
                    "day" -> Calendar.DAY_OF_YEAR
                    "wk" -> Calendar.WEEK_OF_YEAR
                    "mo" -> Calendar.MONTH
                    else -> Calendar.YEAR
                }
                var incrementNumber: Int = number.toString().toInt()
                if (notDigits[0] == '-') {
                    incrementNumber *= -1
                }
                dueDateCalendar.add(unitInt,incrementNumber)

                updateTextViews()
            }
        }
        //sets click listeners for quick access buttons
        for (button in quickAccessButtons) {
            button.setOnClickListener {
                val text: String = button.text as String
                val hour: Int = text.substringBefore(':').toInt()
                val minute: Int = text.substringAfter(':').substringBefore(' ').toInt()
                dueDateCalendar.set(Calendar.HOUR,hour)
                dueDateCalendar.set(Calendar.MINUTE,minute)

                updateTextViews()
            }
        }
    }

    protected fun updateTextViews() { //updates text view
        val fromNowMins = MainFragment.findTimeFromNowMins(dueDateCalendar)
        val time = dueDateCalendar.time

        repeatTextView.text = when(repeatVal) {
            OldReminder.REPEAT_DAILY -> "Daily ".plus(timeFormatter.format(time))
            OldReminder.REPEAT_WEEKDAYS -> "Weekdays ".plus(timeFormatter.format(time))
            OldReminder.REPEAT_WEEKLY -> dayOfWeekFormatter.format(time)
                .plus("s ")
                .plus(timeFormatter.format(time))
            OldReminder.REPEAT_MONTHLY -> dayOfWeekFormatter.format(time)
                .plus("s ")
                .plus(timeFormatter.format(time))
            else -> ""
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
    private fun findTimeFromNowString(timeInMins: Int): String {
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