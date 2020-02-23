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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTimeSetterEditedListenerListener = context as OnTimeSetterEditedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val timeSetterText: String = arguments!!.getString("TimeSetterText") as String
        val key: String = arguments!!.getString("Key") as String
        //parse string
        var plusMinus: Char = timeSetterText[0]
        var (time,notDigits) = timeSetterText.partition { it.isDigit() }
        var unit = notDigits.substring(2)

        val view: View = layoutInflater.inflate(R.layout.fragment_preference_edit_time_setters,container,false)

        val doneButton: Button = view.findViewById(R.id.dialog_button)

        val plusMinusList = arrayOf("+","-")
        val pickerPlusMinus: NumberPicker = view.findViewById(R.id.picker_plus_minus)
        pickerPlusMinus.minValue = 1
        pickerPlusMinus.maxValue = 2
        pickerPlusMinus.displayedValues = plusMinusList
        pickerPlusMinus.value = when (plusMinus) {'+' -> 1 else -> 2 }
        pickerPlusMinus.setOnValueChangedListener { _, _, newVal ->
            plusMinus = when(newVal) {
                1 -> '+'
                else -> '-'
            }
        }
        val timePicker: NumberPicker = view.findViewById(R.id.picker_times)
        timePicker.minValue = 1
        updateTimePickerMaxVal(unit,timePicker)
        timePicker.value = time.toInt()
        timePicker.setOnValueChangedListener { _, _, newVal -> time = newVal.toString() }

        val unitsList = arrayOf("min","hr","day","wk","mo","yr")
        val pickerUnits: NumberPicker = view.findViewById(R.id.picker_units)
        pickerUnits.minValue = 1
        pickerUnits.maxValue = 6
        pickerUnits.displayedValues = unitsList
        pickerUnits.value = when (unit) {
            "min" -> 1
            "hr" -> 2
            "day" -> 3
            "wk" -> 4
            "mo" -> 5
            else -> 6
        }
        updateTimePickerMaxVal(unit,timePicker)
        pickerUnits.setOnValueChangedListener { _, _, newVal ->
            time = timePicker.value.toString()
            unit = when(newVal) {
                1 -> "min"
                2 -> "hr"
                3 -> "day"
                4 -> "wk"
                5 -> "mo"
                else -> "yr"
            }
            updateTimePickerMaxVal(unit,timePicker)
        }
        doneButton.setOnClickListener {
            val returnString= "$plusMinus$time $unit"
            val sharedPreferences  = activity!!.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(key,returnString)
            editor.apply()

            onTimeSetterEditedListenerListener.onTimeSetterEdited(returnString,Character.getNumericValue(key.last()))

            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun updateTimePickerMaxVal(unit: String,timePicker: NumberPicker) {
        when(unit) {
            "min" -> timePicker.maxValue = 59
            "hr" -> timePicker.maxValue = 23
            "day" -> timePicker.maxValue = 6
            "wk" -> timePicker.maxValue = 51
            "mo" -> timePicker.maxValue = 11
            "yr" -> timePicker.maxValue = 100
        }
    }

    interface OnTimeSetterEditedListener {
        fun onTimeSetterEdited(time: String, timeSetterIndex: Int)
    }

}
