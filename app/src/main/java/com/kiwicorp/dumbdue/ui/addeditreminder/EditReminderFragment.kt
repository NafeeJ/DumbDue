package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.kiwicorp.dumbdue.ui.archive.ArchiveViewModel
import com.kiwicorp.dumbdue.ui.reminders.RemindersViewModel
import com.kiwicorp.dumbdue.util.DialogNavigator
import com.kiwicorp.dumbdue.util.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReminderFragment : Fragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_edit_reminder

    lateinit var binding: FragmentEditReminderBinding

    private val args: EditReminderFragmentArgs by navArgs()

    //must past default defaultViewModelProviderFactory https://github.com/google/dagger/issues/1935
    private val addEditReminderViewModel: AddEditReminderViewModel by navGraphViewModels(
        R.id.nav_graph_edit) { defaultViewModelProviderFactory }

    // share remindersViewModel for showing snackbars when completing and archiving
    private val remindersViewModel: RemindersViewModel by activityViewModels()

    // share archiveViewModel for showing snackbars when unarchiving and deleting reminders
    private val archiveViewModel: ArchiveViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_reminder,container,false)
        binding = FragmentEditReminderBinding.bind(root).apply {
            viewmodel = addEditReminderViewModel
            timeSetters.onTimeSetterClickImpl = addEditReminderViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { addEditReminderViewModel.updateReminderAndClose() }
        setupBottomAppBar()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {
            addEditReminderViewModel.updateReminderAndClose()
        }
        addEditReminderViewModel.loadReminder(args.reminderId)
        setupNavigation()
        setupSnackbar()
    }

    override fun onDetach() {
        super.onDetach()
        closeKeyboard()
    }

    private fun setupNavigation() {
        addEditReminderViewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(toChooseRepeat(R.id.nav_graph_edit), findNavController())
        })
        addEditReminderViewModel.eventOpenAutoSnoozeMenu.observe(viewLifecycleOwner, EventObserver {
            navigate(toChooseAutoSnooze(R.id.nav_graph_edit),findNavController())
        })
        addEditReminderViewModel.eventOpenTimePicker.observe(viewLifecycleOwner, EventObserver {
            navigate(toTimePicker(R.id.nav_graph_edit),findNavController())
        })
        addEditReminderViewModel.eventClose.observe(viewLifecycleOwner, EventObserver {
            closeKeyboard()
            findNavController().navigateUp()
        })
    }

    private fun setupBottomAppBar() {
        with((requireActivity() as MainActivity).bottomAppBar) {
            addEditReminderViewModel.isArchived.observe(viewLifecycleOwner, Observer { isArchived ->
                if (isArchived) {
                    replaceMenu(R.menu.appbar_edit_reminder_archived)
                } else {
                    replaceMenu(R.menu.appbar_edit_reminder_unarchived)
                }
            })

            setOnMenuItemClickListener {
                val reminderId = addEditReminderViewModel.reminderId!!

                addEditReminderViewModel.close()

                when(it.itemId) {
                    R.id.menu_complete -> remindersViewModel.completeAndShowSnackbar(reminderId)
                    R.id.menu_archive -> remindersViewModel.archiveAndShowSnackbar(reminderId)
                    R.id.menu_unarchive -> archiveViewModel.unarchive(reminderId)
                    R.id.menu_delete -> archiveViewModel.delete(reminderId)
                }

                true
            }
        }
    }

    private fun setupSnackbar() {
        addEditReminderViewModel.eventSnackbar.observe(viewLifecycleOwner, EventObserver { snackbar ->
            with (requireActivity() as MainActivity) {
                snackbar.show(coordinatorLayout, bottomAppBar)
            }
        })
    }
}
