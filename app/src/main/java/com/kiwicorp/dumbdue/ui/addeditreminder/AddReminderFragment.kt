package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentAddReminderBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.AddReminderFragmentDirections.Companion.toChooseAutoSnooze
import com.kiwicorp.dumbdue.ui.addeditreminder.AddReminderFragmentDirections.Companion.toChooseRepeat
import com.kiwicorp.dumbdue.ui.addeditreminder.AddReminderFragmentDirections.Companion.toTimePicker
import com.kiwicorp.dumbdue.util.DialogNavigator
import com.kiwicorp.dumbdue.util.RoundedBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReminderFragment : RoundedBottomSheetDialogFragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_add_reminder

    private lateinit var binding: FragmentAddReminderBinding
    // must pass defaultViewModelProviderFactory https://github.com/google/dagger/issues/1935
    private val viewModel: AddEditReminderViewModel by navGraphViewModels(R.id.nav_graph_add) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_reminder,container,false)
        binding = FragmentAddReminderBinding.bind(root).apply {
            viewmodel = viewModel
            timeSetters.onTimeSetterClickImpl = viewModel
            lifecycleOwner = viewLifecycleOwner
            titleText.requestFocus() // opens keyboard
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
        setupSnackbar()
    }

    private fun setupNavigation() {
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(toChooseRepeat(R.id.nav_graph_add), findNavController())
        })
        viewModel.eventOpenAutoSnoozeMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(toChooseAutoSnooze(R.id.nav_graph_add), findNavController())
        })
        viewModel.eventOpenTimePicker.observe(viewLifecycleOwner, EventObserver {
            navigate(toTimePicker(R.id.nav_graph_add), findNavController())
        })
        viewModel.eventClose.observe(viewLifecycleOwner, EventObserver {
            cancel()
        })
    }

    private fun cancel() {
        findNavController().navigateUp()
    }

    private fun setupSnackbar() {
        viewModel.eventSnackbar.observe(viewLifecycleOwner, EventObserver { snackbar ->
            snackbar.show(binding.coordinatorLayout)
        })
    }

}
