package com.kiwicorp.dumbdue.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kiwicorp.dumbdue.R

class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) {
            val action = when (preference.title) {
                getString(R.string.preference_edit_timer_setters) -> {
                    SettingsFragmentDirections.actionSettingsFragmentDestToEditTimeSetButtonsFragment()
                }
                else -> return super.onPreferenceTreeClick(preference)
            }
            findNavController().navigate(action)
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }
}
