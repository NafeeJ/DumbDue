package com.kiwicorp.dumbdue.util

import android.view.View
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * Class that contains methods that modify the given calendar
 * Purpose of this class is to provide consistent onClick functions for the time_buttons.xml layout
 * Every view that includes time_buttons.xml, should have a ViewModel that has an instance of this class
 */
class TimeSetter(private val calendar: MutableLiveData<Calendar>) {
    /**
     * calendar.value must be reassigned in order for onChanged() to be called and thus for the view to update.
     * this is why these functions are written so jankily.
     */
    fun onQuickAccess(view: View) {
        calendar.value = calendar.value!!.apply {
            val text = (view as Button).text as String

            val minute: Int = text.substringAfter(':').substringBefore(' ').toInt()
            var hour: Int = text.substringBefore(':').toInt()
            if (text.takeLast(2) == "PM") {
                if (hour < 12) hour += 12
            } else {
                if (hour == 12) hour = 0
            }
            set(Calendar.HOUR_OF_DAY,hour)
            set(Calendar.MINUTE,minute)
        }
    }

    fun onTimeSetter(view: View) {
        calendar.value = calendar.value!!.apply {
            //update due date calendar
            // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
            val (number,notDigits)= ((view as Button).text as String).partition { it.isDigit() }
            val unit: Int = when (notDigits.substring(2)) {
                "min" -> Calendar.MINUTE
                "hr" -> Calendar.HOUR
                "day" -> Calendar.DAY_OF_YEAR
                "wk" -> Calendar.WEEK_OF_YEAR
                "mo" -> Calendar.MONTH
                else -> Calendar.YEAR
            }
            var incrementNumber: Int = number.toInt()
            if (notDigits[0] == '-') incrementNumber *= -1
            add(unit,incrementNumber)
        }
    }
}