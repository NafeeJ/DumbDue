package com.kiwicorp.dumbdue

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shawnlin.numberpicker.NumberPicker


class EditTimerSetterButtonsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = layoutInflater.inflate(R.layout.fragment_preference_edit_time_setters,container,false)

        val plusMinus = arrayOf("+","-")
        val pickerPlusMinus: NumberPicker = view.findViewById(R.id.picker_plus_minus)
        pickerPlusMinus.minValue = 1
        pickerPlusMinus.maxValue = 2
        pickerPlusMinus.displayedValues = plusMinus

        val units = arrayOf("min","hr","day","wk","mo","yr")
        val pickerUnits: NumberPicker = view.findViewById(R.id.picker_units)
        pickerUnits.minValue = 1
        pickerUnits.maxValue = 6
        pickerUnits.displayedValues = units

        val timePicker: NumberPicker = view.findViewById(R.id.picker_times)
        timePicker.minValue = 1
        timePicker.maxValue = 60

        return view
    }

}
