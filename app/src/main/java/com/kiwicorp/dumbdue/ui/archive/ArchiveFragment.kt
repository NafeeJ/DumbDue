package com.kiwicorp.dumbdue.ui.archive

import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentArchiveBinding
import com.kiwicorp.dumbdue.ui.MainActivity
import com.kiwicorp.dumbdue.ui.archive.ArchiveFragmentDirections.Companion.toNavGraphEdit
import com.kiwicorp.dumbdue.ui.reminders.ReminderAdapter
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ArchiveFragment : Fragment() {

    private lateinit var binding: FragmentArchiveBinding

    private val viewModel: ArchiveViewModel by activityViewModels()

    private lateinit var adapter: ArchiveListAdapter
    // for keeping track of whether viewModel.isInSelectionMode has actually changed
    private var isInSelectionMode = false

    private val clearSelectedRemindersCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.clearSelectedReminders()
        }
    }

    private val navigateBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSnackbar()
        requireActivity().onBackPressedDispatcher.apply {
            addCallback(clearSelectedRemindersCallback)
            addCallback(navigateBackCallback)
        }
        viewModel.isInSelectionMode.observe(viewLifecycleOwner, Observer { isInSelectionMode ->
            if (this.isInSelectionMode != isInSelectionMode) { // only execute when isInSelectionMode has actually changed
                this.isInSelectionMode = isInSelectionMode
                
                adapter.notifyDataSetChanged() // so all checkboxes are shown

                val toolbar = binding.toolbar
                
                if (isInSelectionMode && this.isInSelectionMode) {
                    toolbar.setNavigationIcon(R.drawable.ic_cancel)
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.appbar_archive_selection_mode)

                    navigateBackCallback.isEnabled = false
                    clearSelectedRemindersCallback.isEnabled = true
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.appbar_archive)

                    navigateBackCallback.isEnabled = true
                    clearSelectedRemindersCallback.isEnabled = false
                }
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.navigateToEditReminderFragment.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toNavGraphEdit(it))
        })
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId)  {
                R.id.menu_delete_all -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Empty Archive?")
                        .setMessage("${viewModel.reminders.value!!.size} reminders will be destroyed.")
                        .setPositiveButton("Yea") { dialogInterface, _ ->
                            viewModel.deleteArchivedReminders()
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialogInterface, _ ->
                            dialogInterface.cancel()
                        }
                        .show()
                    true
                }
                R.id.menu_delete -> {
                    viewModel.deleteSelectedReminders()
                    true
                }
                R.id.menu_unarchive -> {
                    viewModel.unarchiveSelectedReminders()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { snackbar ->
            with (requireActivity() as MainActivity) {
                snackbar.show(coordinatorLayout)
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = ArchiveListAdapter(viewModel)
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.remindersRecyclerView.adapter = adapter
        setupRecyclerViewSwiping()
        viewModel.checkableReminders.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    private fun setupRecyclerViewSwiping() {
        //create an item touch helper to allow for reminders to be able to be swiped
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            //doesn't allow reminders to be moved
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is ReminderAdapter.HeaderViewHolder) return

                if (direction == ItemTouchHelper.RIGHT) { //if user swipes right, delete reminder
                    viewModel.delete((viewHolder as ArchiveListAdapter.ReminderViewHolder).binding.reminder!!)
                } else if ( direction == ItemTouchHelper.LEFT) {//if user swipes left, unarchive reminder
                    viewModel.unarchive((viewHolder as ArchiveListAdapter.ReminderViewHolder).binding.reminder!!)
                }
            }
            //allows for color and icons in the background when reminders are swiped
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                //do nothing if
                if (viewHolder is ReminderAdapter.HeaderViewHolder) return
                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_white) as Drawable
                val unarchiveIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_unarchive_white) as Drawable
                lateinit var swipeBackground: ColorDrawable

                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {//if user swiped right
                    swipeBackground = ColorDrawable(Color.parseColor("#ff6961"))//sets swipe background to red
                    swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                    //draw background and delete icon
                    swipeBackground.draw(c)
                    //stops icon from being shown outside of the background
                    c.save()
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.draw(c)
                    c.restore()

                } else {//if user swiped left
                    swipeBackground = ColorDrawable(Color.parseColor("#ffaa00"))//sets swipe background to orange
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    unarchiveIcon.setBounds(itemView.right - iconMarginVertical - unarchiveIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    unarchiveIcon.level = 0
                    //draw background and check mark icon
                    swipeBackground.draw(c)
                    //stops icon from being shown outside of the background
                    c.save()
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    unarchiveIcon.draw(c)
                    c.restore()
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.remindersRecyclerView)
    }
}