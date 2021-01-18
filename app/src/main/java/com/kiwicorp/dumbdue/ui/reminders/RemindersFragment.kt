package com.kiwicorp.dumbdue.ui.reminders

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentRemindersBinding
import com.kiwicorp.dumbdue.ui.MainActivity
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toNavGraphAdd
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toNavGraphEdit
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toSettings
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toArchiveFragment
import com.kiwicorp.dumbdue.util.DialogNavigator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.concurrent.fixedRateTimer

@AndroidEntryPoint
class RemindersFragment : Fragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_reminders

    private lateinit var binding: FragmentRemindersBinding

    private val viewModel: RemindersViewModel by activityViewModels()

    private lateinit var listAdapter: ReminderAdapter

    private var refreshTimer: Timer? = null

    // for keeping track of whether viewModel.isInSelectionMode has actually changed
    private var isInSelectionMode = false

    private val clearSelectedRemindersCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.clearSelectedReminders()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_reminders,container,false)
        binding = FragmentRemindersBinding.bind(root).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupBottomAppbar()
        setupFAB()
        setupSelectableReminders()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
        setupSearchView()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        setupRefreshTimer()
    }

    override fun onPause() {
        super.onPause()
        // Cancels RefreshTimer because, otherwise, the timer will still be running when the user
        // navigates to from RemindersFragment to SettingsFragment and then back to RemindersFragment
        // using the NavigationDrawer. This will cause the app to crash because a new instance of
        // RemindersFragment will be attached the activity while the old one will be detached with
        // the timer still running and thus calling requireActivity() will crash the app.
        cancelRefreshTimer()
    }

    private fun setupRecyclerView() {
        listAdapter = ReminderAdapter(viewModel)
        listAdapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.remindersRecyclerView.adapter = listAdapter

        viewModel.checkableReminders.observe(viewLifecycleOwner) {
            listAdapter.addHeadersAndSubmitList(it)
        }

        setupRecyclerViewSwiping()
    }

    private fun setupBottomAppbar() {
        (requireActivity() as MainActivity).bottomAppBar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when(it.itemId) {
                R.id.menu_settings -> {
                    navigate(toSettings(), findNavController())
                    true
                }
                R.id.menu_archive -> {
                    navigate(toArchiveFragment(), findNavController())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFAB() {
        (requireActivity() as MainActivity).fab.setOnClickListener {
            viewModel.addReminder()
        }
    }

    private fun setupNavigation() {
        viewModel.eventAddReminder.observe(viewLifecycleOwner, EventObserver {
            navigate(toNavGraphAdd(), findNavController())
        })
        viewModel.eventEditReminder.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toNavGraphEdit(it))
        })
    }

    private fun setupSearchView() {
        with(binding.searchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.onSearchQueryChanged(newText)
                    return true
                }
            })
        }
    }

    private fun setupSelectableReminders() {
        requireActivity().onBackPressedDispatcher.apply {
            addCallback(clearSelectedRemindersCallback)
        }

        binding.toolbar.setNavigationOnClickListener { viewModel.clearSelectedReminders() }

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_archive -> {
                    viewModel.archiveSelectedRemindersAndShowSnackbar()
                    true
                }
                R.id.menu_complete -> {
                    viewModel.completeSelectedRemindersAndShowSnackBar()
                    true
                }
                else -> false
            }
        }

        viewModel.isInSelectionMode.observe(viewLifecycleOwner) { isInSelectionMode ->
            if (this.isInSelectionMode != isInSelectionMode) { // only execute when isInSelectionMode has actually changed
                this.isInSelectionMode = isInSelectionMode

                listAdapter.notifyDataSetChanged() // so all checkboxes are shown

                val toolbar = binding.toolbar

                if (isInSelectionMode && this.isInSelectionMode) { // is in selection mode
                    binding.materialCardView.visibility = View.GONE
                    toolbar.setNavigationIcon(R.drawable.ic_cancel)
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.appbar_reminders_selection_mode)

                    clearSelectedRemindersCallback.isEnabled = true
                } else {
                    toolbar.navigationIcon = null
                    toolbar.menu.clear()
                    binding.materialCardView.visibility  = View.VISIBLE

                    clearSelectedRemindersCallback.isEnabled = false
                }
            }

        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { snackbar ->
            with (requireActivity() as MainActivity) {
                snackbar.show(coordinatorLayout, fab)
            }
        })
    }

    private fun setupRecyclerViewSwiping() {
        //create an item touch helper to allow for reminders to be able to be swiped
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            //doesn't allow reminders to be moved
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is ReminderAdapter.HeaderViewHolder) return

                if (direction == ItemTouchHelper.RIGHT) { //if user swipes right, archive reminder
                    viewModel.archiveAndShowSnackbar((viewHolder as ReminderAdapter.ReminderViewHolder).binding.reminder!!)
                } else if ( direction == ItemTouchHelper.LEFT) {//if user swipes left, complete reminder
                    viewModel.completeAndShowSnackbar((viewHolder as ReminderAdapter.ReminderViewHolder).binding.reminder!!)
                }
            }
            //allows for color and icons in the background when reminders are swiped
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                //do nothing if
                if (viewHolder is ReminderAdapter.HeaderViewHolder) return
                val archiveIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_archive_white) as Drawable
                val checkIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_check_white) as Drawable
                lateinit var swipeBackground: ColorDrawable

                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - archiveIcon.intrinsicHeight) / 2

                if (dX > 0) {//if user swiped right
                    swipeBackground = ColorDrawable(Color.parseColor("#ffaa00"))//sets swipe background to orange
                    swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    archiveIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + archiveIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                    //draw background and delete icon
                    swipeBackground.draw(c)
                    //stops icon from being shown outside of the background
                    c.save()
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    archiveIcon.draw(c)
                    c.restore()

                } else {//if user swiped left
                    swipeBackground = ColorDrawable(Color.parseColor("#77dd77"))//sets swipe background to green
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    checkIcon.setBounds(itemView.right - iconMarginVertical - checkIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    checkIcon.level = 0
                    //draw background and check mark icon
                    swipeBackground.draw(c)
                    //stops icon from being shown outside of the background
                    c.save()
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    checkIcon.draw(c)
                    c.restore()
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.remindersRecyclerView)
    }

    /**
     * Sets up a timer that updates the recycler view every minute on the minute
     */
    private fun setupRefreshTimer() {
        val thisMinuteCalendar = Calendar.getInstance().apply {
            set(Calendar.MILLISECOND, 0)
            set(Calendar.SECOND, 0)
        }
        if (refreshTimer == null) {
            refreshTimer = fixedRateTimer("RefreshTimer", false, thisMinuteCalendar.time, 60000) {
                requireActivity().runOnUiThread {
                    Timber.d("Recycler View Updated id: ${this.hashCode()}")
                    listAdapter.addHeadersAndSubmitList(viewModel.checkableReminders.value)
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    /**
     * Cancels timer
     */
    private fun cancelRefreshTimer() {
        refreshTimer?.cancel()
        refreshTimer = null
    }

}
