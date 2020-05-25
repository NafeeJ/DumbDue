package com.kiwicorp.dumbdue.ui.addeditreminder.chooserepeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiwicorp.dumbdue.NavEventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseRepeatBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditReminderViewModel
import com.kiwicorp.dumbdue.util.InjectorUtils

class ChooseRepeatFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseRepeatBinding
    // Shares ViewModel with Add/Edit ReminderFragment to get access to calendar and repeatVal
    private val viewModel: AddEditReminderViewModel by activityViewModels {
        InjectorUtils.provideAddEditViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_repeat,container,false)
        binding = FragmentChooseRepeatBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.eventChooseRepeat.observe(viewLifecycleOwner, NavEventObserver {
            close()
        })
    }

    /**
     * Closes this Dialog
     */
    private fun close() {
        findNavController().popBackStack()
    }
}