package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.MainActivity
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentAddReminderBinding
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections
import com.kiwicorp.dumbdue.util.RoundedDaggerBottomSheetDialogFragment
import javax.inject.Inject


class AddReminderFragment : RoundedDaggerBottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddReminderBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: AddEditReminderViewModel by navGraphViewModels(R.id.nav_graph_add) { viewModelFactory }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).bottomAppBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_settings) {
                val action = RemindersFragmentDirections.actionRemindersFragmentDestToSettingsFragmentDest()
                findNavController().navigate(action)
            }
            true
        }
    }

    private fun setupNavigation() {
        //todo do the rest of this
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(AddReminderFragmentDirections.actionAddReminderFragmentToChooseRepeatFragment(R.id.nav_graph_add))
        })
        viewModel.eventOpenAutoSnoozeMenu.observe(viewLifecycleOwner, EventObserver {
            navigateToAutoSnoozeMenu()
        })
        viewModel.eventOpenTimePicker.observe(viewLifecycleOwner, EventObserver {
            navigateToTimePicker()
        })
        viewModel.eventClose.observe(viewLifecycleOwner, EventObserver {
            cancel()
        })
        viewModel.eventOpenChooseCustomRepeat.observe(viewLifecycleOwner, EventObserver {
            navigateToChooseCustomRepeat()
        })
    }

    private fun navigate(direction: NavDirections) {
        if (findNavController().currentDestination?.id == R.id.add_reminder_fragment_dest) {
            findNavController().navigate(direction)
        }
    }

    private fun navigateToAutoSnoozeMenu() {
        val action =
            AddReminderFragmentDirections.actionAddReminderFragmentToChooseAutoSnoozeFragment(
                R.id.nav_graph_add
            )
        findNavController().navigate(action)
    }

    private fun navigateToTimePicker() {
        val action = AddReminderFragmentDirections.actionAddReminderFragmentToTimePickerFragment(R.id.nav_graph_add)
        findNavController().navigate(action)
    }

    private fun navigateToChooseCustomRepeat() {
        val action = AddReminderFragmentDirections.actionAddReminderFragmentDestToCustomRepeatFragment(R.id.nav_graph_add)
        findNavController().navigate(action)
    }

    private fun cancel() {
        findNavController().popBackStack()
    }

    private fun setupSnackbar() {
        viewModel.eventSnackbar.observe(viewLifecycleOwner, EventObserver { snackbar ->
            snackbar.show(binding.coordinatorLayout)
        })
    }

}
