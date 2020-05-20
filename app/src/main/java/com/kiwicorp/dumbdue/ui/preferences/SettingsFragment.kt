package com.kiwicorp.dumbdue.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.kiwicorp.dumbdue.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
