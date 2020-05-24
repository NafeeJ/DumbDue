package com.kiwicorp.dumbdue.ui.addreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentAddReminderBinding
import com.kiwicorp.dumbdue.ui.AddEditReminderViewModel
import com.kiwicorp.dumbdue.util.InjectorUtils


class AddReminderFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddReminderBinding

    private val viewModel: AddEditReminderViewModel by activityViewModels {
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
    }

    private fun setupNavigation() {
        viewModel.eventOpenRepeatMenu.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                navigateToRepeatMenu()
                viewModel.onOpenRepeatMenuComplete()
            }
        })
    }

    private fun navigateToRepeatMenu() {
        val action = AddReminderFragmentDirections.actionAddReminderFragmentDestToChooseRepeatFragment()
        findNavController().navigate(action)
    }

//todo
//    private fun showKeyboard() {
//        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS)
//        binding.titleText.requestFocus()
//    }

}
