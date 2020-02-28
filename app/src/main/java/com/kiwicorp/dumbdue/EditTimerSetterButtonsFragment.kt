package com.kiwicorp.dumbdue

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.shawnlin.numberpicker.NumberPicker


class EditTimerSetterButtonsFragment : Fragment() {
    lateinit var onTimeSetterEditedListenerListener: OnTimeSetterEditedListener
    //widgets
    lateinit var doneButton: Button
    lateinit var timePicker: NumberPicker
    lateinit var plusMinusPicker: NumberPicker
    lateinit var unitsPicker: NumberPicker

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTimeSetterEditedListenerListener = context as OnTimeSetterEditedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get key and time setter text from bundle
        val timeSetterText: String = arguments!!.getString("TimeSetterText") as String// contains the text of the button
        val key: String = arguments!!.getString("Key") as String//contains the key corresponding to the button
        //parse time setter text
        var plusMinus: Char = timeSetterText[0] //char that contains + or -
        var (time,notDigits) = timeSetterText.partition { it.isDigit() } //time is a contains the actual number of how much to increment/decrement
        var unit: String = notDigits.substring(2)  //contains the unit of the text

        val view: View = layoutInflater.inflate(R.layout.fragment_preference_edit_time_setters,container,false)

        doneButton = view.findViewById(R.id.done_button)
        //initialize pickers
        val plusMinusList = arrayOf("+","-") //list provided to plus minus picker to display
        plusMinusPicker = view.findViewById(R.id.picker_plus_minus)
        plusMinusPicker.minValue = 1 //corresponds to +
        plusMinusPicker.maxValue = 2 //corresponds to -
        plusMinusPicker.displayedValues = plusMinusList
        plusMinusPicker.value = when (plusMinus) {'+' -> 1 else -> 2 }
        //changes plus minus to correct symbol when picker's value is changed
        plusMinusPicker.setOnValueChangedListener { _, _, newVal ->
            plusMinus = when(newVal) {
                1 -> '+'
                else -> '-'
            }
        }
        timePicker = view.findViewById(R.id.picker_times)
        timePicker.minValue = 1
        updateTimePickerMaxVal(unit)
        timePicker.value = time.toInt()
        //changes time to correct value when picker's value is changed
        timePicker.setOnValueChangedListener { _, _, newVal -> time = newVal.toString() }

        val unitsList = arrayOf("min","hr","day","wk","mo","yr") //list provided to unit picker to display
        unitsPicker = view.findViewById(R.id.picker_units)
        unitsPicker.minValue = 1 //corresponds to min
        unitsPicker.maxValue = 6 //corresponds to yr
        unitsPicker.displayedValues = unitsList
        unitsPicker.value = when (unit) {
            "min" -> 1
            "hr" -> 2
            "day" -> 3
            "wk" -> 4
            "mo" -> 5
            else -> 6
        }
        updateTimePickerMaxVal(unit)
        unitsPicker.setOnValueChangedListener { _, _, newVal ->
            //updates unit to correct value when picker's value is changed
            unit = when(newVal) {
                1 -> "min"
                2 -> "hr"
                3 -> "day"
                4 -> "wk"
                5 -> "mo"
                else -> "yr"
            }
            updateTimePickerMaxVal(unit)
            //updates time picker value in case the max val is changed and the user immediately confirms
            time = timePicker.value.toString()
        }
        doneButton.setOnClickListener {
            //stores the new time setter text
            val newTimeSetter = "$plusMinus$time $unit"
            val sharedPreferences = activity!!.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(key,newTimeSetter)
            editor.apply()
            //updates time setter button in activity
            onTimeSetterEditedListenerListener.onTimeSetterEdited(newTimeSetter,Character.getNumericValue(key.last()))
            //goes back to parent activity
            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }
    //updates the max vale of the time picker based on the unit given
    private fun updateTimePickerMaxVal(unit: String) {
        when(unit) {
            "min" -> timePicker.maxValue = 59
            "hr" -> timePicker.maxValue = 23
            "day" -> timePicker.maxValue = 6
            "wk" -> timePicker.maxValue = 3
            "mo" -> timePicker.maxValue = 11
            "yr" -> timePicker.maxValue = 100
        }
    }
    //interface that allows the button text to be updated in the activity
    interface OnTimeSetterEditedListener {
        fun onTimeSetterEdited(time: String, index: Int)
    }

}
