package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditTimeSetButtonsBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class EditTimeSetButtonsFragment : DaggerFragment() {

    lateinit var binding: FragmentEditTimeSetButtonsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EditTimeSetButtonsViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_time_set_buttons, container, false)
        binding = FragmentEditTimeSetButtonsBinding.bind(root).apply {
            timeButtons.onTimeSetButtonsClickImpl = viewModel
            timeButtons.preferencesStorage = viewModel.preferencesStorage
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
        viewModel.eventReset.observe(viewLifecycleOwner, EventObserver {
            //tells view to redraw
            binding.invalidateAll()
        })
    }

    private fun setupNavigation() {
        viewModel.eventEditQuickAccessTimeSetter.observe(viewLifecycleOwner, EventObserver { key ->
            navigateToEditQuickAccessTimerSetterFragment(key)
        })
        viewModel.eventEditIncrementalTimeSetter.observe(viewLifecycleOwner, EventObserver { key ->
            navigateToEditIncrementalTimeSetterFragment(key)
        })
    }

    private fun navigateToEditQuickAccessTimerSetterFragment(key: String) {
        val action = EditTimeSetButtonsFragmentDirections.actionEditTimeSetButtonsFragmentToEditQuickAccessTimeSetterFragment(key)
        findNavController().navigate(action)
    }

    private fun navigateToEditIncrementalTimeSetterFragment(key: String) {
        val action = EditTimeSetButtonsFragmentDirections.actionEditTimeSetButtonsFragmentToEditIncrementalTimeSetter(key)
        findNavController().navigate(action)
    }
}
