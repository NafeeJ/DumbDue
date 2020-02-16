package com.kiwicorp.dumbdue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.aigestudio.wheelpicker.WheelPicker
import com.shawnlin.numberpicker.NumberPicker

class TimeSetterPreferenceDialog : DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = layoutInflater.inflate(R.layout.dialog_preference_time_setters,container,false)

        val wheelPickerPlusMinus: WheelPicker = view.findViewById(R.id.wheel_picker_plus_minus)
        wheelPickerPlusMinus.data = listOf(" +","-")

        val wheelPickerTimeUnit: WheelPicker = view.findViewById(R.id.wheel_picker_time_unit)
        wheelPickerTimeUnit.data = listOf("m","h","w","mo","y")

        val doneButton: Button = view.findViewById(R.id.dialog_button)
        doneButton.setOnClickListener { dialog!!.dismiss() }

        val numberPicker: NumberPicker = view.findViewById(R.id.number_picker)
        numberPicker.maxValue = 60

        return view
    }

}