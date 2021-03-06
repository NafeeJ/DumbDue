package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseAutoSnoozeBinding
import com.kiwicorp.dumbdue.util.RoundedBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseAutoSnoozeFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseAutoSnoozeBinding

    private val args: ChooseAutoSnoozeFragmentArgs by navArgs()

    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId)
        val root = inflater.inflate(R.layout.fragment_choose_auto_snooze, container, false)
        binding = FragmentChooseAutoSnoozeBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.eventAutoSnoozeChosen.observe(viewLifecycleOwner, EventObserver {
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
