package com.kiwicorp.dumbdue.ui.editduedate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditDueDateBinding
import com.kiwicorp.dumbdue.ui.reminders.RemindersViewModel
import com.kiwicorp.dumbdue.util.DialogNavigator
import com.kiwicorp.dumbdue.util.RoundedBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDueDateFragment : RoundedBottomSheetDialogFragment(), DialogNavigator {
    override val destId: Int = R.id.editDueDateFragment

    private lateinit var binding: FragmentEditDueDateBinding

    private val editDueDateViewModel: EditDueDateViewModel by viewModels()

    private val remindersViewModel: RemindersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditDueDateBinding.inflate(inflater, container, false)
        return binding.root
    }
}