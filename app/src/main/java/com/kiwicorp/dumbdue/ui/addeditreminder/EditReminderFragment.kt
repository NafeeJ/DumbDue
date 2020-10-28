package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditReminderBinding
import com.kiwicorp.dumbdue.ui.MainActivity
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toChooseAutoSnooze
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toChooseRepeat
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toReminders
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toTimePicker
import com.kiwicorp.dumbdue.util.DialogNavigator
import com.kiwicorp.dumbdue.util.closeKeyboard
import com.kiwicorp.dumbdue.util.createMaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReminderFragment : Fragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_edit_reminder

    lateinit var binding: FragmentEditReminderBinding

    private val args: EditReminderFragmentArgs by navArgs()
    //must past default defaultViewModelProviderFactory https://github.com/google/dagger/issues/1935
    private val viewModel: AddEditReminderViewModel by navGraphViewModels(R.id.nav_graph_edit) { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = createMaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.dumbdue_motion_duration_large).toLong()
        }
        enterTransition = createMaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.dumbdue_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_reminder,container,false)
        binding = FragmentEditReminderBinding.bind(root).apply {
            viewmodel = viewModel
            timeSetters.onTimeSetterClickImpl = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.updateReminder()
        }
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        (requireActivity() as MainActivity).bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_complete -> {
                    viewModel.completeReminder()
                    true
                }
                R.id.menu_delete -> {
                    viewModel.deleteReminder()
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadReminder(args.reminderId)
        setupNavigation()
        setupSnackbar()
    }

    override fun onDetach() {
        super.onDetach()
        closeKeyboard()
    }

    private fun setupNavigation() {
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(toChooseRepeat(R.id.nav_graph_edit), findNavController())
        })
        viewModel.eventOpenAutoSnoozeMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(toChooseAutoSnooze(R.id.nav_graph_edit),findNavController())
        })
        viewModel.eventOpenTimePicker.observe(viewLifecycleOwner, EventObserver {
            navigate(toTimePicker(R.id.nav_graph_edit),findNavController())
        })
        viewModel.eventClose.observe(viewLifecycleOwner, EventObserver {
            close()
        })
        viewModel.eventCompleteDelete.observe(viewLifecycleOwner, EventObserver {request ->
            close(request, viewModel.reminderId!!)
        })
    }

    private fun close(request: Int = 0, reminderId: String = "") {
        closeKeyboard()
        navigate(toReminders(request,reminderId), findNavController())
    }



    private fun setupSnackbar() {
        viewModel.eventSnackbar.observe(viewLifecycleOwner, EventObserver { snackbar ->
            with (requireActivity() as MainActivity) {
                snackbar.show(coordinatorLayout, bottomAppBar)
            }
        })
    }
}
