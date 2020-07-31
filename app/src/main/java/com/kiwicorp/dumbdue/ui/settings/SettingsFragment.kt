package com.kiwicorp.dumbdue.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentSettingsBinding
import com.kiwicorp.dumbdue.ui.settings.SettingsFragmentDirections.Companion.toEditTimeSetters
import com.kiwicorp.dumbdue.util.applySystemWindowInsetsPadding
import com.kiwicorp.dumbdue.util.createMaterialElevationScale
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SettingsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

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
    }

    private fun setupNavigation() {
        viewModel.eventOpenEditTimeSetButton.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toEditTimeSetters())
        })
    }
    
}
