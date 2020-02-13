package com.kiwicorp.dumbdue

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ReminderButtonPreferenceActivity : AppCompatActivity() {
    companion object {
        const val TIME_MUTATOR_1: String = "TimeMutator1"
        const val MINUS_MIN_KEY: String = "minusMin"
        const val PLUS_HR_1_KEY: String = "plusHr1Key"
        const val MINUS_HR__KEY: String = "minusHr2Key"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_preference_reminder_buttons)

        val toolBar: Toolbar = findViewById(R.id.settingsToolBar)
        setSupportActionBar(toolBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonPlusMin1: Button = findViewById(R.id.plus10minbutton)
        val buttonMinusMin2: Button = findViewById(R.id.minus10minbutton)
        val buttonPlusHr1: Button = findViewById(R.id.plus1hrbutton)
        val buttonMinusHr1: Button = findViewById(R.id.minus1hrbutton)
        val buttonPlusHr2: Button = findViewById(R.id.plus3hrbutton)
        val buttonMinusHr2: Button = findViewById(R.id.minus3hrbutton)
        val buttonPlus1day: Button = findViewById(R.id.plus1daybutton)
        val buttonMinus1day: Button = findViewById(R.id.minus1daybutton)

        val quickAccessList = getQuickAccessTimes()
        val quickAccess1 = quickAccessList[0]
        val quickAccess2 = quickAccessList[1]
        val quickAccess3 = quickAccessList[2]
        val quickAccess4 = quickAccessList[3]

        val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)

        val buttonPreset1: Button = findViewById(R.id.presetButton1)
        val preset1HourOfDay = quickAccess1.get(Calendar.HOUR_OF_DAY)
        val preset1Min = quickAccess1.get(Calendar.MINUTE)
        buttonPreset1.text = timeFormatter.format(quickAccess1.time)

        val buttonPreset2: Button = findViewById(R.id.presetButton2)
        val preset2HourOfDay = quickAccess2.get(Calendar.HOUR_OF_DAY)
        val preset2Min = quickAccess2.get(Calendar.MINUTE)
        buttonPreset2.text = timeFormatter.format(quickAccess2.time)

        val buttonPreset3: Button = findViewById(R.id.presetButton3)
        val preset3HourOfDay = quickAccess3.get(Calendar.HOUR_OF_DAY)
        val preset3Min = quickAccess3.get(Calendar.MINUTE)
        buttonPreset3.text = timeFormatter.format(quickAccess3.time)

        val buttonPreset4: Button = findViewById(R.id.presetButton4)
        val preset4HourOfDay = quickAccess4.get(Calendar.HOUR_OF_DAY)
        val preset4Min = quickAccess4.get(Calendar.MINUTE)
        buttonPreset4.text = timeFormatter.format(quickAccess4.time)
    }

    //returns a list of calendars each with the hour and minute of a preset time
    private fun getQuickAccessTimes(): List<Calendar> {
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
        //get quick access times strings from shared preferences
        val sharedPreferences: SharedPreferences = PreferenceManager(applicationContext).sharedPreferences
        val quickAccess1String: String = sharedPreferences.getString("quickAccess1", "8:00") as String
        val quickAccess2String: String = sharedPreferences.getString("quickAccess2", "12:00") as String
        val quickAccess3String: String = sharedPreferences.getString("quickAccess3", "17:00") as String
        val quickAccess4String: String = sharedPreferences.getString("quickAccess4", "22:00") as String

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
}