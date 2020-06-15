package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseRepeatBinding
import com.kiwicorp.dumbdue.util.daggerext.DaggerBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import javax.inject.Inject

class ChooseRepeatFragment : DaggerBottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseRepeatBinding

    private val args: ChooseRepeatFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Shares ViewModel with Add/Edit ReminderFragment
    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId) { viewModelFactory }
        val root = inflater.inflate(R.layout.fragment_choose_repeat,container,false)
        binding = FragmentChooseRepeatBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.eventChooseRepeat.observe(viewLifecycleOwner, EventObserver {
            close()
        })
        viewModel.eventOpenCustomRepeatMenu.observe(viewLifecycleOwner, EventObserver {
            navigateToCustomRepeatMenu()
        })
    }

    /**
     * Closes this Dialog
     */
    private fun close() {
        findNavController().popBackStack()
    }

    private fun navigateToCustomRepeatMenu() {
        val action = ChooseRepeatFragmentDirections.actionChooseRepeatFragmentDestToCustomRepeatFragment(args.graphId)
        findNavController().navigate(action)
    }
}