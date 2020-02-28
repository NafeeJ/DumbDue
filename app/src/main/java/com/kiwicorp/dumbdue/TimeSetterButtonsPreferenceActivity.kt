package com.kiwicorp.dumbdue


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TimeSetterButtonsPreferenceActivity : AppCompatActivity(),
    EditTimerSetterButtonsFragment.OnTimeSetterEditedListener,
    EditQuickAccessTimesFragment.OnQuickAccessTimeEditedListener {

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

        fun loadTimeSetterButtonTexts(context: Context, buttons: List<Button>) {
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

        fun loadQuickAccessButtonTexts(context: Context,buttons: List<Button>) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE)
            buttons[0].text = sharedPreferences.getString(QUICK_ACCESS_1_KEY,"9:30 AM")
            buttons[1].text = sharedPreferences.getString(QUICK_ACCESS_2_KEY,"12:00 PM")
            buttons[2].text = sharedPreferences.getString(QUICK_ACCESS_3_KEY,"6:30 PM")
            buttons[3].text = sharedPreferences.getString(QUICK_ACCESS_4_KEY,"10:00 PM")
        }
    }
    //widgets
    private lateinit var timeSetter1: Button
    private lateinit var timeSetter2: Button
    private lateinit var timeSetter3: Button
    private lateinit var timeSetter4: Button
    private lateinit var timeSetter5: Button
    private lateinit var timeSetter6: Button
    private lateinit var timeSetter7: Button
    private lateinit var timeSetter8: Button
    private lateinit var timeSetterButtons: List<Button>
    private lateinit var quickAccessButton1: Button
    private lateinit var quickAccessButton2: Button
    private lateinit var quickAccessButton3: Button
    private lateinit var quickAccessButton4: Button
    private lateinit var quickAccessButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        setContentView(R.layout.activity_time_setter_button_preference)
        //init time setter buttons
        timeSetter1 = findViewById(R.id.timeSetterButton1)
        timeSetter2 = findViewById(R.id.timeSetterButton2)
        timeSetter3 = findViewById(R.id.timeSetterButton3)
        timeSetter4 = findViewById(R.id.timeSetterButton4)
        timeSetter5 = findViewById(R.id.timeSetterButton5)
        timeSetter6 = findViewById(R.id.timeSetterButton6)
        timeSetter7 = findViewById(R.id.timeSetterButton7)
        timeSetter8 = findViewById(R.id.timeSetterButton8)
        timeSetterButtons = listOf(timeSetter1,timeSetter2,timeSetter3,
            timeSetter4,timeSetter5,timeSetter6,timeSetter7,timeSetter8)
        loadTimeSetterButtonTexts(applicationContext,timeSetterButtons)

        val timeSetterMap:Map<Button, String> = mapOf(timeSetter1 to TIME_SETTER_1_KEY,
            timeSetter2 to TIME_SETTER_2_KEY, timeSetter3 to TIME_SETTER_3_KEY,
            timeSetter4 to TIME_SETTER_4_KEY,timeSetter5 to TIME_SETTER_5_KEY,
            timeSetter6 to TIME_SETTER_6_KEY,timeSetter7 to TIME_SETTER_7_KEY,
            timeSetter8 to TIME_SETTER_8_KEY)
        for (button in timeSetterButtons) {
            button.setOnClickListener {
                startEditButtonFragment(timeSetterMap[button] as String,button.text as String)
            }
        }

        //init quick access buttons
        quickAccessButton1= findViewById(R.id.quickAccessButton1)
        quickAccessButton2= findViewById(R.id.quickAccessButton2)
        quickAccessButton3= findViewById(R.id.quickAccessButton3)
        quickAccessButton4= findViewById(R.id.quickAccessButton4)
        quickAccessButtons = listOf(quickAccessButton1,quickAccessButton2,
            quickAccessButton3,quickAccessButton4)
        loadQuickAccessButtonTexts(applicationContext,quickAccessButtons)

        val quickAccessMap: Map<Button, String> = mapOf(quickAccessButton1 to QUICK_ACCESS_1_KEY,
            quickAccessButton2 to QUICK_ACCESS_2_KEY,quickAccessButton3 to QUICK_ACCESS_3_KEY,
            quickAccessButton4 to QUICK_ACCESS_4_KEY)
        for (button in quickAccessButtons) {
            button.setOnClickListener {
                startEditQuickAccessTimesFragment(quickAccessMap[button] as String,button.text as String)
            }
        }
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

    private fun startEditQuickAccessTimesFragment(key: String, quickAccessTimeText: String) {
        val fragment = EditQuickAccessTimesFragment()
        val bundle = Bundle()
        bundle.putString("QuickAccessTimeText",quickAccessTimeText)
        bundle.putString("Key", key)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onTimeSetterEdited(time: String, index: Int) {
        timeSetterButtons[index - 1].text = time
    }

    override fun onQuickAccessTimeEdited(time: String, index: Int) {
        quickAccessButtons[index - 1].text = time
    }
}
