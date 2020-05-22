package com.kiwicorp.dumbdue.ui.addreminder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.AddReminderFragmentBinding
import com.kiwicorp.dumbdue.util.InjectorUtils


class AddReminderFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddReminderFragmentBinding

    private val viewModel: AddReminderViewModel by viewModels {
        InjectorUtils.provideAddRemindersViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.add_reminder_fragment,container,false)

        binding.viewModel = viewModel

        binding.timeButtons.timeSetter = viewModel.timeSetter

        binding.lifecycleOwner = viewLifecycleOwner //so the view can observe LiveData updates

        return binding.root
    }

//todo
//    private fun showKeyboard() {
//        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS)
//        binding.titleText.requestFocus()
//    }

}
