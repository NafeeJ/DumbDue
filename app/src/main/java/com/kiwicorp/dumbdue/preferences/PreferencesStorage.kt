package com.kiwicorp.dumbdue.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.timesetters.IncrementalTimeSetter
import com.kiwicorp.dumbdue.timesetters.QuickAccessTimeSetter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStorage @Inject constructor(val context: Context) {
    companion object{
        const val PREFS_TIME_SETTER_1 = "prefs_time_setter_1"
        const val PREFS_TIME_SETTER_2 = "prefs_time_setter_2"
        const val PREFS_TIME_SETTER_3 = "prefs_time_setter_3"
        const val PREFS_TIME_SETTER_4 = "prefs_time_setter_4"
        const val PREFS_TIME_SETTER_5 = "prefs_time_setter_5"
        const val PREFS_TIME_SETTER_6 = "prefs_time_setter_6"
        const val PREFS_TIME_SETTER_7 = "prefs_time_setter_7"
        const val PREFS_TIME_SETTER_8 = "prefs_time_setter_8"

        const val PREFS_QUICK_ACCESS_1 = "prefs_quick_access_1"
        const val PREFS_QUICK_ACCESS_2 = "prefs_quick_access_2"
        const val PREFS_QUICK_ACCESS_3 = "prefs_quick_access_3"
        const val PREFS_QUICK_ACCESS_4 = "prefs_quick_access_4"

        //Map containing all the default values of the TimeSetters
        val timeSetterKeyToDefaultValue: Map<String, String> = mapOf(
            PREFS_TIME_SETTER_1 to "+10 min",
            PREFS_TIME_SETTER_2 to "+1 hr",
            PREFS_TIME_SETTER_3 to "+3 hr",
            PREFS_TIME_SETTER_4 to "+1 day",
            PREFS_TIME_SETTER_5 to "-10 min",
            PREFS_TIME_SETTER_6 to "-1 hr",
            PREFS_TIME_SETTER_7 to "-3 hr",
            PREFS_TIME_SETTER_8 to "-1 day",
            PREFS_QUICK_ACCESS_1 to "9:30 AM",
            PREFS_QUICK_ACCESS_2 to "12:00 PM",
            PREFS_QUICK_ACCESS_3 to "6:30 PM",
            PREFS_QUICK_ACCESS_4 to "10:00 PM"
        )
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    private val prefsEditor: SharedPreferences.Editor = prefs.edit()

    fun updateIncrementalTimeSetter(key: String, incrementalTimeSetter: IncrementalTimeSetter) {
        prefsEditor.putString(key, incrementalTimeSetter.toString())
        prefsEditor.apply()
    }

    /**
     * Returns a timer setter from the key
     */
    fun getIncrementalTimeSetter(key: String) = IncrementalTimeSetter(
            prefs.getString(key, timeSetterKeyToDefaultValue[key])!!
        )


    fun updateQuickAccessTimeSetter(key: String, quickAccess: QuickAccessTimeSetter) {
        prefsEditor.putString(key, quickAccess.toString())
        prefsEditor.apply()
    }

    /**
     * Returns a quick access from the key
     */
    fun getQuickAccessTimeSetter(key: String): QuickAccessTimeSetter = QuickAccessTimeSetter(
            prefs.getString(key, timeSetterKeyToDefaultValue[key])!!
        )

    /**
     * Resets all the Time Setters to their default values
     *
     * Only uses the map to simply gain iterative access to the keys
     */
    fun resetTimeSetters() {
        for (keyToDefaultValue in timeSetterKeyToDefaultValue) {
            prefsEditor.remove(keyToDefaultValue.key)
        }
        prefsEditor.apply()
    }

    val defaultAutoSnooze: Long
        get() {
            return prefs.getString(
                context.getString(R.string.default_auto_snooze),
                Reminder.AUTO_SNOOZE_MINUTE.toString()
            )!!.toLong()
        }
}