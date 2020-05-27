package com.kiwicorp.dumbdue.ui.addeditreminder.chooserepeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kiwicorp.dumbdue.NavEventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseRepeatBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditReminderViewModel
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditViewModelFactory
import com.kiwicorp.dumbdue.util.InjectorUtils

class ChooseRepeatFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseRepeatBinding

    private val args: ChooseRepeatFragmentArgs by navArgs()
    // Shares ViewModel with Add/Edit ReminderFragment
    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
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

    /**
     * Initializes [viewModel].
     *
     * This fragment doesn't use delegation because [args.graphId] won't be initialized in time to
     * pass to [navGraphViewModels] and doing it this way seemed easier and more readable than
     * creating another overly specific extension function and an [NavArgs] abstract class that has
     * a graphId.
     */
    private fun initViewModel() {
        val backStackEntry = findNavController().getBackStackEntry(args.graphId)

        val viewModelProvider = ViewModelProvider(
            backStackEntry.viewModelStore,
            InjectorUtils.provideAddEditViewModelFactory(requireContext())
        )

        viewModel = viewModelProvider.get(AddEditReminderViewModel::class.java)
    }
}