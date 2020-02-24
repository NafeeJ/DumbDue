package com.kiwicorp.dumbdue


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_time_setter_button_preference.*
import java.text.SimpleDateFormat
import java.util.*

class TimeSetterButtonsPreferenceActivity : AppCompatActivity(), EditTimerSetterButtonsFragment.OnTimeSetterEditedListener {
    companion object {
        const val TIME_SETTER_1_KEY: String = "TimeSetter1"
        const val TIME_SETTER_2_KEY: String = "TimeSetter2"
        const val TIME_SETTER_3_KEY: String = "TimeSetter3"
        const val TIME_SETTER_4_KEY: String = "TimeSetter4"
        const val TIME_SETTER_5_KEY: String = "TimeSetter5"
        const val TIME_SETTER_6_KEY: String = "TimeSetter6"
        const val TIME_SETTER_7_KEY: String = "TimeSetter7"
        const val TIME_SETTER_8_KEY: String = "TimeSetter8"

        const val QUICK_ACCESS_1_KEY: String = "QuickAccess1"
        const val QUICK_ACCESS_2_KEY: String = "QuickAccess2"
        const val QUICK_ACCESS_3_KEY: String = "QuickAccess3"
        const val QUICK_ACCESS_4_KEY: String = "QuickAccess4"

        fun loadTimeSetterButtons(context: Context, buttons: List<Button>) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE)
            buttons[0].text = sharedPreferences.getString(TIME_SETTER_1_KEY,"+10 min")
            buttons[1].text = sharedPreferences.getString(TIME_SETTER_2_KEY,"+1 hr")
            buttons[2].text = sharedPreferences.getString(TIME_SETTER_3_KEY,"+3 hr")
            buttons[3].text = sharedPreferences.getString(TIME_SETTER_4_KEY,"+1 day")
            buttons[4].text = sharedPreferences.getString(TIME_SETTER_5_KEY,"-10 min")
            buttons[5].text = sharedPreferences.getString(TIME_SETTER_6_KEY,"-1 hr")
            buttons[6].text = sharedPreferences.getString(TIME_SETTER_7_KEY,"-3 hr")
            buttons[7].text = sharedPreferences.getString(TIME_SETTER_8_KEY,"+1 day")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        setContentView(R.layout.activity_time_setter_button_preference)

        val timeSetter1: Button = findViewById(R.id.timeSetterButton1)
        val timeSetter2: Button = findViewById(R.id.timeSetterButton2)
        val timeSetter3: Button = findViewById(R.id.timeSetterButton3)
        val timeSetter4: Button = findViewById(R.id.timeSetterButton4)
        val timeSetter5: Button = findViewById(R.id.timeSetterButton5)
        val timeSetter6: Button = findViewById(R.id.timeSetterButton6)
        val timeSetter7: Button = findViewById(R.id.timeSetterButton7)
        val timeSetter8: Button = findViewById(R.id.timeSetterButton8)

        loadTimeSetterButtons(applicationContext,listOf(timeSetter1,timeSetter2,timeSetter3,timeSetter4,timeSetter5,timeSetter6,timeSetter7,timeSetter8))

        timeSetter1.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_1_KEY,timeSetter1.text as String)
        }
        timeSetter2.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_2_KEY,timeSetter2.text as String)
        }
        timeSetter3.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_3_KEY,timeSetter3.text as String)
        }
        timeSetter4.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_4_KEY,timeSetter4.text as String)
        }
        timeSetter5.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_5_KEY,timeSetter5.text as String)
        }
        timeSetter6.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_6_KEY,timeSetter6.text as String)
        }
        timeSetter7.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_7_KEY,timeSetter7.text as String)
        }
        timeSetter8.setOnClickListener {
            startEditButtonFragment(TIME_SETTER_8_KEY,timeSetter8.text as String)
        }

        val quickAccessTimesList = getQuickAccessTimes()
        val quickAccessTime1 = quickAccessTimesList[0]
        val quickAccessTime2 = quickAccessTimesList[1]
        val quickAccessTime3 = quickAccessTimesList[2]
        val quickAccessTime4 = quickAccessTimesList[3]

        val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)

        val quickAccessButton1: Button = findViewById(R.id.quickAccessButton1)
        val quickAccessTime1HourOfDay = quickAccessTime1.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime1Min = quickAccessTime1.get(Calendar.MINUTE)
        quickAccessButton1.text = timeFormatter.format(quickAccessTime1.time)

        val quickAccessButton2: Button = findViewById(R.id.quickAccessButton2)
        val quickAccessTime2HourOfDay = quickAccessTime1.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime2Min = quickAccessTime2.get(Calendar.MINUTE)
        quickAccessButton2.text = timeFormatter.format(quickAccessTime2.time)

        val quickAccessButton3: Button = findViewById(R.id.quickAccessButton3)
        val quickAccessTime3HourOfDay = quickAccessTime1.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime3Min = quickAccessTime3.get(Calendar.MINUTE)
        quickAccessButton3.text = timeFormatter.format(quickAccessTime3.time)

        val quickAccessButton4: Button = findViewById(R.id.quickAccessButton4)
        val quickAccessTime4HourOfDay = quickAccessTime1.get(Calendar.HOUR_OF_DAY)
        val quickAccessTime4Min = quickAccessTime4.get(Calendar.MINUTE)
        quickAccessButton4.text = timeFormatter.format(quickAccessTime4.time)
    }

    //returns a list of calendars each with the hour and minute of a preset time
    private fun getQuickAccessTimes(): List<Calendar> {
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
        //get quick access times strings from shared preferences
        val sharedPreferences: SharedPreferences = PreferenceManager(applicationContext).sharedPreferences
        val quickAccess1String: String = sharedPreferences.getString(QUICK_ACCESS_1_KEY, "8:00") as String
        val quickAccess2String: String = sharedPreferences.getString(QUICK_ACCESS_2_KEY, "12:00") as String
        val quickAccess3String: String = sharedPreferences.getString(QUICK_ACCESS_3_KEY, "17:00") as String
        val quickAccess4String: String = sharedPreferences.getString(QUICK_ACCESS_4_KEY, "22:00") as String

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

    private fun startEditButtonFragment(key: String, timeSetterText: String) {
        val fragment = EditTimerSetterButtonsFragment()
        val bundle = Bundle()
        bundle.putString("TimeSetterText",timeSetterText)
        bundle.putString("Key",key)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onTimeSetterEdited(time: String, timeSetterIndex: Int) {
        when (timeSetterIndex) {
            1 -> timeSetterButton1.text = time
            2 -> timeSetterButton2.text = time
            3 -> timeSetterButton3.text = time
            4 -> timeSetterButton4.text = time
            5 -> timeSetterButton5.text = time
            6 -> timeSetterButton6.text = time
            7 -> timeSetterButton7.text = time
            else -> timeSetterButton8.text = time
        }
    }
}
