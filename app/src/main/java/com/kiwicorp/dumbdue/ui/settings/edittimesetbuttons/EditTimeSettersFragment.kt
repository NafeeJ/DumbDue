package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditTimeSettersBinding
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.EditTimeSettersFragmentDirections.Companion.toEditIncrementalTimeSetter
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.EditTimeSettersFragmentDirections.Companion.toEditQuickAccessTimeSetter
import com.kiwicorp.dumbdue.util.DialogNavigator
import com.kiwicorp.dumbdue.util.createMaterialElevationScale
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class EditTimeSettersFragment : DaggerFragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_edit_time_setters

    lateinit var binding: FragmentEditTimeSettersBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EditTimeSettersViewModel by activityViewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_time_setters, container, false)
        binding = FragmentEditTimeSettersBinding.bind(root).apply {
            timeSetters.onTimeSetterClickImpl = viewModel
            viewmodel = viewModel
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = createMaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.dumbdue_motion_duration_large).toLong()
        }
        enterTransition = createMaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.dumbdue_motion_duration_large).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeBackToolbar.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
        viewModel.eventTimeSettersUpdated.observe(viewLifecycleOwner, EventObserver {
            binding.invalidateAll()
        })
    }

    private fun setupNavigation() {
        viewModel.eventEditQuickAccessTimeSetter.observe(viewLifecycleOwner, EventObserver { key ->
            navigate(toEditQuickAccessTimeSetter(key), findNavController())
        })
        viewModel.eventEditIncrementalTimeSetter.observe(viewLifecycleOwner, EventObserver { key ->
            navigate(toEditIncrementalTimeSetter(key), findNavController())
        })
    }
}
