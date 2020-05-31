package com.kiwicorp.dumbdue.ui.addeditreminder

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentAddReminderBinding
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
            titleText.requestFocus() //open keyboard
        }
        return root
    }

    /**
     * Expands the BottomSheetDialog so the entire dialog is shown when the keyboard is first opened
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = dialog.findViewById(R.id.design_bottom_sheet)!!
            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
        setupSnackbar()
    }

    private fun setupNavigation() {
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigateToRepeatMenu()
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
    }

    private fun navigateToRepeatMenu() {
        val action =
            AddReminderFragmentDirections.actionAddReminderFragmentToChooseRepeatFragment(
                R.id.nav_graph_add
            )
        findNavController().navigate(action)
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

    private fun cancel() {
        findNavController().popBackStack()
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { snackbarData ->
            val snackbar = Snackbar.make(binding.coordinatorLayout,snackbarData.text,snackbarData.duration)
            if (snackbarData.action != null) {
                snackbar.setAction(snackbarData.actionText,snackbarData.action)
            }
            snackbar.show()
        })
    }

}
