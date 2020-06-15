package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ChooseCustomRepeatFragment : DaggerDialogFragment() {

    val args: ChooseCustomRepeatFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId)
        val root = layoutInflater.inflate(R.layout.fragment_choose_custom_repeat,container,false)
        return root
    }
}