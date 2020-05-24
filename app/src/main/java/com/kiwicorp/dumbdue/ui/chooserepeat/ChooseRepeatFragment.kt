package com.kiwicorp.dumbdue.ui.chooserepeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseRepeatBinding
import com.kiwicorp.dumbdue.ui.AddEditReminderViewModel
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
        viewModel.eventChooseRepeat.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().popBackStack()
                viewModel.onChooseRepeatComplete()
            }
        })
    }
}