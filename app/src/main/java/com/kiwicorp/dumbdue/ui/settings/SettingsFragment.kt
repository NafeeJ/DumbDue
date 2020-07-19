package com.kiwicorp.dumbdue.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.ui.settings.SettingsFragmentDirections.Companion.toEditTimeSetters
import com.kiwicorp.dumbdue.util.applySystemWindowInsetsPadding

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // apply insets here because for some reason applySystemWindowInsetsPadding() won't be
        // called in data binding
        view.applySystemWindowInsetsPadding(
            previousApplyLeft = false,
            previousApplyTop = false,
            previousApplyRight = false,
            previousApplyBottom = false,
            applyLeft = false,
            applyTop = true,
            applyRight = false,
            applyBottom = false
        )
        view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { findNavController().navigateUp() }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) {
            return when (preference.title) {
                getString(R.string.preference_edit_timer_setters) -> {
                    findNavController().navigate(toEditTimeSetters())
                    true
                }
                else -> super.onPreferenceTreeClick(preference)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}
