package com.kiwicorp.dumbdue.ui.addeditreminder.addreminder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.NavEventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentAddReminderBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditReminderViewModel
import com.kiwicorp.dumbdue.ui.addeditreminder.chooserepeat.ChooseRepeatFragment
import com.kiwicorp.dumbdue.util.InjectorUtils


class AddReminderFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddReminderBinding

    private val viewModel: AddEditReminderViewModel by navGraphViewModels(R.id.nav_graph_add) {
        InjectorUtils.provideAddEditViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_reminder,container,false)
        binding = FragmentAddReminderBinding.bind(root).apply {
            viewmodel = viewModel
            timeButtons.viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
        setupSnackbar()
    }

    private fun setupNavigation() {
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, NavEventObserver {
            navigateToRepeatMenu()
        })
        viewModel.eventOpenAutoSnoozeMenu.observe(viewLifecycleOwner, NavEventObserver {
            navigateToAutoSnoozeMenu()
        })
        viewModel.eventCancel.observe(viewLifecycleOwner, NavEventObserver {
            cancel()
        })
    }

    private fun navigateToRepeatMenu() {
        val action = AddReminderFragmentDirections.actionAddReminderFragmentToChooseRepeatFragment(R.id.nav_graph_add)
        findNavController().navigate(action)
    }

    private fun navigateToAutoSnoozeMenu() {
        val action = AddReminderFragmentDirections.actionAddReminderFragmentToChooseAutoSnoozeFragment(R.id.nav_graph_add)
        findNavController().navigate(action)
    }

    private fun cancel() {
        findNavController().popBackStack()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("AddReminderFragment","Fragment Detached")
    }

    private fun setupSnackbar() {
        viewModel.snackbarText.observe(viewLifecycleOwner, Observer { text ->
            Snackbar.make(binding.coordinatorLayout,text,Snackbar.LENGTH_SHORT).show()
        })
    }

}
