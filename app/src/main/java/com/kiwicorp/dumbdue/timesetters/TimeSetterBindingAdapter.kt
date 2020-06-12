package com.kiwicorp.dumbdue.timesetters

import android.widget.Button
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import com.kiwicorp.dumbdue.preferences.PreferencesStorage

/**
 * Binding adapter to set the text of time setter buttons
 */
@BindingAdapter("key")
fun Button.setTimeSetterText(key: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    text = prefs.getString(key,PreferencesStorage.timeSetterKeyToDefaultValue[key])
}
