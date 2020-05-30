package com.kiwicorp.dumbdue.ui.addeditreminder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditReminderBinding
import com.kiwicorp.dumbdue.util.InjectorUtils

class EditReminderFragment : Fragment() {

    lateinit var binding: FragmentEditReminderBinding

    private val args: EditReminderFragmentArgs by navArgs()

    private val viewModel: AddEditReminderViewModel by navGraphViewModels(R.id.nav_graph_edit) {
        InjectorUtils.provideAddEditViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_reminder,container,false)
        binding = FragmentEditReminderBinding.bind(root).apply {
            viewmodel = viewModel
            timeButtons.viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadReminder(args.reminderId)
        setupNavigation()
    }

    override fun onDetach() {
        super.onDetach()
        closeKeyboard()
        viewModel.onUpdateReminder()
    }

    private fun setupNavigation() {
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigateToRepeatMenu()
        })
        viewModel.eventOpenAutoSnoozeMenu.observe(viewLifecycleOwner, EventObserver {
            navigateToAutoSnoozeMenu()
        })
        viewModel.eventClose.observe(viewLifecycleOwner, EventObserver {
            close()

        })
        viewModel.eventCompleteDelete.observe(viewLifecycleOwner, EventObserver {request ->
            close(request, viewModel.reminderId!!)
        })
    }

    private fun navigateToRepeatMenu() {
        val action =
            EditReminderFragmentDirections.actionEditReminderFragmentToChooseRepeatFragment(
                R.id.nav_graph_edit
            )
        findNavController().navigate(action)
    }

    private fun navigateToAutoSnoozeMenu() {
        val action =
            EditReminderFragmentDirections.actionEditReminderFragmentToChooseAutoSnoozeFragment(
                R.id.nav_graph_edit
            )
        findNavController().navigate(action)
    }

    private fun close(request: Int = 0, reminderId: String = "") {
        closeKeyboard()
        val action = EditReminderFragmentDirections.actionGlobalRemindersFragmentDest(request,reminderId)
        findNavController().navigate(action)
    }

    //closes keyboard if the current focus is not the edit text
    private fun closeKeyboard() {
        // Check if no view has focus:
        val view = requireActivity().currentFocus
        view?.let {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
