package com.kiwicorp.dumbdue.ui.addeditreminder.chooseautosnooze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiwicorp.dumbdue.NavEventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseAutoSnoozeBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditReminderViewModel
import com.kiwicorp.dumbdue.util.InjectorUtils

class ChooseAutoSnoozeFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseAutoSnoozeBinding

    private val viewModel: AddEditReminderViewModel by activityViewModels {
        InjectorUtils.provideAddEditViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_auto_snooze, container, false)
        binding = FragmentChooseAutoSnoozeBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.eventChooseAutoSnooze.observe(viewLifecycleOwner, NavEventObserver {
            close()
        })
    }

    /**
     * Closes this dialog
     */
    private fun close() {
        findNavController().popBackStack()
    }

}
