package com.kiwicorp.dumbdue.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentSettingsBinding
import com.kiwicorp.dumbdue.ui.settings.SettingsFragmentDirections.Companion.toEditTimeSetters
import com.kiwicorp.dumbdue.util.createMaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.bind(root).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = createMaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.dumbdue_motion_duration_large).toLong()
        }
        enterTransition = createMaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.dumbdue_motion_duration_large).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeBackToolbar.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setupNavigation()
        binding.repeatIntervalUsesDueDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRepeatIntervalUsesReminderDueDate(isChecked)
        }
    }

    private fun setupNavigation() {
        viewModel.eventOpenEditTimeSetButton.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toEditTimeSetters())
        })
        viewModel.eventOpenChooseThemeDialog.observe(viewLifecycleOwner, EventObserver {
            openChooseThemeDialog()
        })
    }

    private fun openChooseThemeDialog() {
        val themesText = arrayOf("Light", "Dark", "System Default")

        val delegate = (requireActivity() as AppCompatActivity).delegate

        val currTheme = when(delegate.localNightMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            else -> 2
        }

        MaterialAlertDialogBuilder(requireContext())
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(themesText, currTheme) { dialog, which ->
                val theme = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                delegate.localNightMode = theme
                viewModel.setTheme(theme)
                dialog.dismiss()
            }
            .show()
    }
    
}
