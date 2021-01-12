package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditReminderBinding
import com.kiwicorp.dumbdue.ui.*
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toChooseAutoSnooze
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toChooseRepeat
import com.kiwicorp.dumbdue.ui.addeditreminder.EditReminderFragmentDirections.Companion.toTimePicker
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest.Companion.REQUEST_ARCHIVE
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest.Companion.REQUEST_COMPLETE
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest.Companion.REQUEST_DELETE
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest.Companion.REQUEST_UNARCHIVE
import com.kiwicorp.dumbdue.util.DialogNavigator
import com.kiwicorp.dumbdue.util.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReminderFragment : Fragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_edit_reminder

    lateinit var binding: FragmentEditReminderBinding

    private val args: EditReminderFragmentArgs by navArgs()
    //must past default defaultViewModelProviderFactory https://github.com/google/dagger/issues/1935
    private val viewModel: AddEditReminderViewModel by navGraphViewModels(R.id.nav_graph_edit) { defaultViewModelProviderFactory }

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
        binding.toolbar.setNavigationOnClickListener { viewModel.updateReminderAndClose() }
        setupBottomAppBar()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.updateReminderAndClose()
        }
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
        viewModel.eventRequest.observe(viewLifecycleOwner, EventObserver { reminderRequest ->
            close(reminderRequest)
        })
    }

    private fun close(reminderRequest: ReminderRequest? = null) {
        closeKeyboard()

        reminderRequest?.let {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("request", it)
        }

        findNavController().navigateUp()
    }

    private fun setupBottomAppBar() {
        with((requireActivity() as MainActivity).bottomAppBar) {
            viewModel.isArchived.observe(viewLifecycleOwner, Observer { isArchived ->
                if (isArchived) {
                    replaceMenu(R.menu.appbar_edit_reminder_archived)
                } else {
                    replaceMenu(R.menu.appbar_edit_reminder_unarchived)
                }
            })

            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_complete -> {
                        viewModel.requestAction(REQUEST_COMPLETE)
                        true
                    }
                    R.id.menu_archive -> {
                        viewModel.requestAction(REQUEST_ARCHIVE)
                        true
                    }
                    R.id.menu_unarchive -> {
                        viewModel.requestAction(REQUEST_UNARCHIVE)
                        true
                    }
                    R.id.menu_delete -> {
                        viewModel.requestAction(REQUEST_DELETE)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupSnackbar() {
        viewModel.eventSnackbar.observe(viewLifecycleOwner, EventObserver { snackbar ->
            with (requireActivity() as MainActivity) {
                snackbar.show(coordinatorLayout, bottomAppBar)
            }
        })
    }
}
