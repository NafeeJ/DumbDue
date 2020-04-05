package com.kiwicorp.dumbdue

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


open class ScheduleReminderFragment : Fragment(),
    TimeDatePickerDialogFragment.OnDateChangedListener,
    TimeDatePickerSpinnerDialogFragment.OnDateChangedListener {
    lateinit var navController: NavController

    protected lateinit var reminderCalendar: Calendar
    protected var repeatVal: Int = Reminder.REPEAT_NONE
    protected var autoSnoozeVal: Int = Reminder.AUTO_SNOOZE_MINUTE
    protected lateinit var dateTextView: TextView
    protected lateinit var repeatTextView: TextView

    protected val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a", Locale.US)
    protected val dateFormatter2 = SimpleDateFormat("MMM d, h:mm a", Locale.US)
    protected val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
    protected val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.US)
    //widgets
    protected lateinit var titleEditText: EditText
    protected lateinit var cancelButton: ImageButton
    protected lateinit var addButton: ImageButton
    protected lateinit var repeatButton: ImageButton
    protected lateinit var snoozeButton: ImageButton
    protected lateinit var imm: InputMethodManager


    protected var isEditReminderFragment: Boolean = false //boolean used to determine if the keyboard should open when the fragment starts

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_reminder,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        titleEditText = view.findViewById(R.id.titleEditText)
        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (!isEditReminderFragment) {
            //opens keyboard when fragment starts
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS)
            titleEditText.requestFocus()
        }
        cancelButton = view.findViewById(R.id.cancelButton)
        snoozeButton = view.findViewById(R.id.snoozeButton)
        repeatButton = view.findViewById(R.id.repeatButton)
        addButton = view.findViewById(R.id.addButton)
        //set auto snooze based off of default value
        val sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
        val autoSnoozeString: String = sharedPreferences.getString("default_auto_snooze",Reminder.AUTO_SNOOZE_MINUTE.toString()) as String
        autoSnoozeVal = autoSnoozeString.toInt()
        updateSnoozeButtonImage()
        //set click listeners
        cancelButton.setOnClickListener{
            closeKeyboard()
            titleEditText.clearFocus()
            navController.navigate(R.id.action_schedule_reminders_to_main_reminder)
        }
        repeatButton.setOnClickListener {
            closeKeyboard()
            titleEditText.clearFocus()
            showRepeatDialog()
        }
        snoozeButton.setOnClickListener {
            closeKeyboard()
            titleEditText.clearFocus()
            showSnoozeDialog()
        }

        addButton.setOnClickListener {
            closeKeyboard()
            titleEditText.clearFocus()
            Reminder(titleEditText.text.toString(),reminderCalendar,repeatVal,autoSnoozeVal,context!!)
            navController.navigate(R.id.action_schedule_reminders_to_main_reminder)

        }
        dateTextView = view.findViewById(R.id.dateTextView)
        dateTextView.setOnClickListener {
            closeKeyboard()
            val timeDatePickerString = sharedPreferences.getString("time_date_picker","spinner")
            if (timeDatePickerString == "stock") {
                val datePickerDialogFragment: Fragment = TimeDatePickerDialogFragment()
                val args = Bundle()
                args.putLong("timeInMillis",reminderCalendar.timeInMillis)
                datePickerDialogFragment.arguments = args
                childFragmentManager.beginTransaction()
                    .replace(R.id.container,datePickerDialogFragment)
                    .addToBackStack(null)
                    .commit()
            } else if (timeDatePickerString == "spinner") {
                val datePickerSpinnerDialogFragment = TimeDatePickerSpinnerDialogFragment()
                val args = Bundle()
                args.putLong("timeInMillis",reminderCalendar.timeInMillis)
                datePickerSpinnerDialogFragment.arguments = args
                datePickerSpinnerDialogFragment.show(childFragmentManager,null)
            }
        }
        updateSnoozeButtonImage()
        reminderCalendar = Calendar.getInstance()
        reminderCalendar.set(Calendar.SECOND,0)
        reminderCalendar.set(Calendar.MILLISECOND,0)
        //initialize all buttons
        val timeSetterButton1: Button = view.findViewById(R.id.timeSetterButton1)
        val timeSetterButton2: Button = view.findViewById(R.id.timeSetterButton2)
        val timeSetterButton3: Button = view.findViewById(R.id.timeSetterButton3)
        val timeSetterButton4: Button = view.findViewById(R.id.timeSetterButton4)
        val timeSetterButton5: Button = view.findViewById(R.id.timeSetterButton5)
        val timeSetterButton6: Button = view.findViewById(R.id.timeSetterButton6)
        val timeSetterButton7: Button = view.findViewById(R.id.timeSetterButton7)
        val timeSetterButton8: Button = view.findViewById(R.id.timeSetterButton8)
        //initialize preset buttons and their intended hour and minutes
        val quickAccessButton1: Button = view.findViewById(R.id.quickAccessButton1)
        val quickAccessButton2: Button = view.findViewById(R.id.quickAccessButton2)
        val quickAccessButton3: Button = view.findViewById(R.id.quickAccessButton3)
        val quickAccessButton4: Button = view.findViewById(R.id.quickAccessButton4)
        //list of time incrementing/decrementing button
        val timeButtons = listOf(
            timeSetterButton1, timeSetterButton2, timeSetterButton3,
            timeSetterButton4, timeSetterButton5, timeSetterButton6,
            timeSetterButton7, timeSetterButton8, quickAccessButton1,
            quickAccessButton2, quickAccessButton3, quickAccessButton4)

        val timeSetterButtons = timeButtons.subList(0,8)
        val quickAccessButtons = timeButtons.subList(8,12)

        TimeSetterButtonsPreferenceActivity.loadTimeSetterButtonTexts(context!!,timeSetterButtons)
        TimeSetterButtonsPreferenceActivity.loadQuickAccessButtonTexts(context!!,quickAccessButtons)

        repeatTextView = view.findViewById(R.id.repeatTextView)
        repeatTextView.visibility = View.GONE

        updateTextViews() //sets text to be the formatted intended schedule date
        //sets on click listeners for time setters
        for (button in timeSetterButtons) {
            button.setOnClickListener {
                //update due date calendar
                // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
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
                if (notDigits[0] == '-') incrementNumber *= -1
                reminderCalendar.add(unitInt,incrementNumber)

                updateTextViews()
            }
        }
        //sets click listeners for quick access buttons
        for (button in quickAccessButtons) {
            button.setOnClickListener {
                val text: String = button.text as String
                val hour: Int = text.substringBefore(':').toInt()
                val minute: Int = text.substringAfter(':').substringBefore(' ').toInt()
                reminderCalendar.set(Calendar.HOUR,hour)
                reminderCalendar.set(Calendar.MINUTE,minute)

                updateTextViews()
            }
        }
    }
    private fun showRepeatDialog() {
        //create and show dialog
        val dialog = BottomSheetDialog(context!!)
        val dialogView = layoutInflater.inflate(R.layout.dialog_choose_repeat,null)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)

        val repeatOffTextView: TextView = dialogView.findViewById(R.id.repeatOffText)
        val repeatDailyTextView: TextView = dialogView.findViewById(R.id.repeatDailyText)
        val repeatWeekdaysTextView: TextView = dialogView.findViewById(R.id.repeatWeekdays)
        val repeatWeeklyTextView: TextView = dialogView.findViewById(R.id.repeatWeekly)
        val repeatYearlyTextView: TextView = dialogView.findViewById(R.id.repeatYearly)
        val repeatMonthlyTextView: TextView = dialogView.findViewById(R.id.repeatMonthly)
        val repeatCustomTextView: TextView = dialogView.findViewById(R.id.repeatCustom)
        //set text views text
        repeatOffTextView.text = "Repeat Off"
        repeatDailyTextView.text = "Daily ".plus(timeFormatter.format(reminderCalendar.time))
        repeatWeekdaysTextView.text = "Weekdays ".plus(timeFormatter.format(reminderCalendar.time))
        repeatWeeklyTextView.text = dayOfWeekFormatter.format(reminderCalendar.time)
            .plus("s ")
            .plus(timeFormatter.format(reminderCalendar.time))
        repeatMonthlyTextView.text = reminderCalendar
            .get(Calendar.DAY_OF_MONTH).toString()
            .plus(MainFragment.daySuffixFinder(reminderCalendar))
            .plus(" each month at ")
            .plus(timeFormatter.format(reminderCalendar.time))
        repeatYearlyTextView.text = "Every ".plus(dateFormatter2.format(reminderCalendar.time))
        repeatCustomTextView.text = "Custom"
        //set click listeners
        //todo make a loop of this?
        repeatOffTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_NONE
            repeatTextView.visibility = View.GONE
            dialog.dismiss()
        }
        repeatDailyTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_DAILY
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        repeatWeekdaysTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_WEEKDAYS
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        repeatWeeklyTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_WEEKLY
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        repeatMonthlyTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_MONTHLY
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        //todo implement this
        repeatCustomTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_CUSTOM
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
        }
        dialog.show()
    }
    private fun showSnoozeDialog() {
        //create and show dialog
        val dialog = BottomSheetDialog(context!!)
        val dialogView = layoutInflater.inflate(R.layout.dialog_choose_snooze,null)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)

        val noneTextView: TextView = dialogView.findViewById(R.id.noneTextView)
        val everyMinuteTextView: TextView = dialogView.findViewById(R.id.everyMinuteTextView)
        val every5MinutesTextView: TextView = dialogView.findViewById(R.id.every5MinutesTextView)
        val every10MinutesTextView: TextView = dialogView.findViewById(R.id.every10MinutesTextView)
        val every15MinutesTextView: TextView = dialogView.findViewById(R.id.every15MinutesTextView)
        val every30MinutesTextView: TextView = dialogView.findViewById(R.id.every30MinutesTextView)
        val everyHourTextView: TextView = dialogView.findViewById(R.id.everyHourTextView)

        noneTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_NONE; dialog.dismiss(); updateSnoozeButtonImage() }
        everyMinuteTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_MINUTE; dialog.dismiss(); updateSnoozeButtonImage() }
        every5MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_5_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        every10MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_10_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        every15MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_15_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        every30MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_30_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        everyHourTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_HOUR; dialog.dismiss(); updateSnoozeButtonImage() }

        dialog.show()
    }
    //closes keyboard if the current focus is not the edit text
    protected fun closeKeyboard() {
        // Check if no view has focus:
        val view = activity!!.currentFocus
        view?.let { v ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
    protected fun updateSnoozeButtonImage() {
        val image = when(autoSnoozeVal) {
            Reminder.AUTO_SNOOZE_NONE -> R.drawable.white_none_square
            Reminder.AUTO_SNOOZE_MINUTE -> R.drawable.one_white
            Reminder.AUTO_SNOOZE_5_MINUTES -> R.drawable.five_white
            Reminder.AUTO_SNOOZE_10_MINUTES -> R.drawable.ten_white
            Reminder.AUTO_SNOOZE_15_MINUTES -> R.drawable.fifteen_white
            Reminder.AUTO_SNOOZE_30_MINUTES -> R.drawable.thirty_white
            Reminder.AUTO_SNOOZE_HOUR -> R.drawable.one_hour_white
            else -> 0
        }
        snoozeButton.setImageResource(image)
    }
    override fun onDateChanged(timeInMillis: Long) {
        reminderCalendar.timeInMillis = timeInMillis
        updateTextViews()
    }
    protected fun updateTextViews() { //updates text view
        val fromNowMins = MainFragment.findTimeFromNowMins(reminderCalendar)
        val time = reminderCalendar.time
        repeatTextView.text = when(repeatVal) {
            Reminder.REPEAT_DAILY -> "Daily ".plus(timeFormatter.format(time))
            Reminder.REPEAT_WEEKDAYS -> "Weekdays ".plus(timeFormatter.format(time))
            Reminder.REPEAT_WEEKLY -> dayOfWeekFormatter.format(time)
                .plus("s ")
                .plus(timeFormatter.format(time))
            Reminder.REPEAT_MONTHLY -> reminderCalendar.get(Calendar.DAY_OF_MONTH).toString()
                .plus(MainFragment.daySuffixFinder(reminderCalendar))
                .plus(" each month at ")
                .plus(timeFormatter.format(time))
            Reminder.REPEAT_YEARLY -> "Every ".plus(dateFormatter2.format(reminderCalendar.time))
            Reminder.REPEAT_CUSTOM -> "Custom"
            else -> ""
        }
        /**
         * if time from now is negative, updates text to be in format:
         * "Date fromNowMins (units) ago" and sets red background color
         */
        if (fromNowMins >= 0) {
            dateTextView.text = dateFormatter.format(reminderCalendar.time)
                .plus(" in ")
                .plus(findTimeFromNowString(fromNowMins))
            dateTextView.setBackgroundColor(Color.parseColor("#303030"))
            repeatTextView.setBackgroundColor(Color.parseColor("#303030"))
        }
        else {
            dateTextView.text = dateFormatter.format(reminderCalendar.time)
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