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

class EditQuickAccessTimesFragment : Fragment() {
    lateinit var onQuickAccessTimeEditedListener: OnQuickAccessTimeEditedListener
    //widgets
    lateinit var ampmPicker: NumberPicker
    lateinit var minutePicker: NumberPicker
    lateinit var hourPicker: NumberPicker
    lateinit var doneButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onQuickAccessTimeEditedListener = context as OnQuickAccessTimeEditedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val text: String = arguments!!.getString("QuickAccessTimeText") as String
        val key: String = arguments!!.getString("Key") as String

        var ampmString: String = text.substringAfterLast(' ')
        var hourString: String = text.substringBefore(':')
        var minuteString: String = text.substringAfter(':').substringBefore(' ')

        val view = inflater.inflate(R.layout.fragment_preference_quick_access_times,container,false)

        val ampmList: Array<String> = arrayOf("AM","PM")
        ampmPicker = view.findViewById(R.id.picker_am_pm)
        ampmPicker.value = when(ampmString) {
            "AM" -> 1
            else -> 2
        }
        ampmPicker.displayedValues = ampmList
        ampmPicker.setOnValueChangedListener { _, _, newVal ->
            ampmString = when (newVal) {
                1 -> "AM"
                else -> "PM"
            }
        }

        val minuteList: Array<String> = Array(60) { "$it" }
        for (i in 0..9) { minuteList[i] = "0".plus(i) }
        minutePicker = view.findViewById(R.id.picker_minutes)
        minutePicker.displayedValues = minuteList
        minutePicker.value = minuteString.toInt()
        minutePicker.setOnValueChangedListener { _, _, newVal ->
            minuteString = newVal.toString()
        }

        val hourList: Array<String> = Array(12) { "$it" }
        for (i in 1..12) {
            if (i < 10)
                hourList[i - 1] = "0".plus(i)
            else
                hourList[i - 1] = i.toString()
        }
        hourPicker = view.findViewById(R.id.picker_hours)
        hourPicker.displayedValues = hourList
        hourPicker.value = hourString.toInt()
        hourPicker.setOnValueChangedListener { _, _, newVal ->
            hourString = newVal.toString()
        }

        doneButton = view.findViewById(R.id.done_button)
        doneButton.setOnClickListener {
            //store new quick access times fragment
            val newPresetTime = "$hourString:$minuteString $ampmString"
            val sharedPreferences = activity!!.getSharedPreferences("Preferences",Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(key,newPresetTime)
            editor.apply()

            onQuickAccessTimeEditedListener.onQuickAccessTimeEdited(newPresetTime,Character.getNumericValue(key.last()))

            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }

    //interface that allows the button text to be updated in the activity
    interface OnQuickAccessTimeEditedListener {
        fun onQuickAccessTimeEdited(time: String,index: Int)
    }

}
