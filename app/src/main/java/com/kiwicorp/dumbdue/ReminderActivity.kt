package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_reminder.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ReminderActivity : AppCompatActivity(),
    ReminderSection.ClickListener,
    EditReminderFragment.OnReminderEditListener {


    private lateinit var deleteIcon: Drawable
    private lateinit var checkIcon: Drawable

    private lateinit var swipeBackground: ColorDrawable

    private val updateRequestCode: Int = 1230498

    companion object {
        private const val TAG = "ReminderActivity"

        val todayCalendar: Calendar = Calendar.getInstance()//calendar with date at 23:59:59 today
        val tomorrowCalendar: Calendar = Calendar.getInstance()//calendar with date at 23:59:59 tomorrow
        val next7daysCalendar: Calendar = Calendar.getInstance()//calendar with date at 23:59:59 in 7 days

        lateinit var globalAlarmManager: AlarmManager

        val sectionAdapter = SectionedRecyclerViewAdapter()

        var notificationID = 0 //used to keep notifications unique thus allowing notifications to stack

        //returns the correct suffix of number of the day based off the date of the calendar
        fun daySuffixFinder(calendar: Calendar): String {
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
            return when {
                dayOfMonth.rem(10) == 1 && dayOfMonth != 11 -> "st"
                dayOfMonth.rem(10) == 2 && dayOfMonth != 12 -> "nd"
                dayOfMonth.rem(10) == 3 && dayOfMonth != 13 -> "rd"
                else -> "th"
            }

        }
        //returns the time in minutes from now of the date of the calendar
        fun findTimeFromNowMins(calendar: Calendar): Int {
            //Get time difference of each time unit with fromNowMins as variable to use as the standard
            var fromNowMins: Int = calendar.get(Calendar.MINUTE) - Calendar.getInstance().get(Calendar.MINUTE)
            val fromNowHours: Int = calendar.get(Calendar.HOUR_OF_DAY) - Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val fromNowDays: Int = calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val fromNowYears: Int = calendar.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR)

            fromNowMins += (fromNowHours * 60) + (fromNowDays * 24 * 60) + (fromNowYears * 525600) //Add the other time unit differences, in minutes, to fromNowMins
            return fromNowMins
        }
        //saves all the reminder lists into shared preferences
        fun saveAll(context: Context) {

            val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

            val myGson = Gson()
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            //put global request code as a json
            val requestCodeJson: String = myGson.toJson(Reminder.globalRequestCode)
            editor.putString("GlobalRequestCode", requestCodeJson)
            val reminderList = LinkedList<Reminder>()
            for (list in Reminder.reminderListArray) {
                for (reminder in list) {
                    reminderList.add(reminder)
                }
            }
            //get json strings of reminder lists
            val reminderListJson = myGson.toJson(reminderList)
            //put reminder list strings into shared preferences
            editor.putString("ReminderList",reminderListJson)
            //apply changes
            editor.apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        //set today calendar to correct date and time
        todayCalendar.set(Calendar.MILLISECOND, 59)
        todayCalendar.set(Calendar.SECOND,59)
        todayCalendar.set(Calendar.MINUTE, 59)
        todayCalendar.set(Calendar.HOUR_OF_DAY,23)
        //set tomorrow calender to correct date and time
        tomorrowCalendar.set(Calendar.MILLISECOND, 59)
        tomorrowCalendar.set(Calendar.SECOND,59)
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY,23)
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR,1)
        //set next 7 days calendar to correct date and time
        next7daysCalendar.set(Calendar.MILLISECOND, 59)
        next7daysCalendar.set(Calendar.SECOND,59)
        next7daysCalendar.set(Calendar.MINUTE, 59)
        next7daysCalendar.set(Calendar.HOUR_OF_DAY,23)
        next7daysCalendar.add(Calendar.WEEK_OF_YEAR,1)

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        globalAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val scheduleFAB: FloatingActionButton = findViewById(R.id.scheduleFAB)

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete_white) as Drawable
        checkIcon = ContextCompat.getDrawable(this,R.drawable.check_white) as Drawable
        //create and add reminder sections to the reminder section list
        ReminderSection.reminderSectionList.add(ReminderSection("Overdue",Reminder.overdueList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Today",Reminder.todayList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Tomorrow",Reminder.tomorrowList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Next 7 Days",Reminder.next7daysList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Future", Reminder.futureList,this))
        //add reminder sections to section adapter
        sectionAdapter.addSection("Overdue",ReminderSection.reminderSectionList[0])
        sectionAdapter.addSection("Today",ReminderSection.reminderSectionList[1])
        sectionAdapter.addSection("Tomorrow",ReminderSection.reminderSectionList[2])
        sectionAdapter.addSection("Next 7 Days",ReminderSection.reminderSectionList[3])
        sectionAdapter.addSection("Future",ReminderSection.reminderSectionList[4])

        if (!isLoaded()) {
            val sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val gson = Gson()

            val emptyListJson = gson.toJson(LinkedList<Reminder>())//an empty list in case lists haven't been stored

            val reminderListJson = sharedPreferences.getString("ReminderList",emptyListJson)
            //gets type to allow gson to know what type of object it's retrieving
            val reminderListType = object : TypeToken<LinkedList<Reminder>>() {}.type
            //get list
            val reminderListFromJson = gson.fromJson<LinkedList<Reminder>>(reminderListJson,reminderListType)
            //load reminders
            for (reminder in reminderListFromJson) {
                reminder.loadReminder(applicationContext)
            }
            //initializes global request code from shared preferences
            val requestCodeJson = sharedPreferences.getString("GlobalRequestCode", gson.toJson(0))
            val intType = object : TypeToken<Int>() {}.type
            val indexFromJson = gson.fromJson<Int>(requestCodeJson,intType)
            Reminder.globalRequestCode = indexFromJson
        }

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ReminderActivity)
            adapter = sectionAdapter
        }
        //makes empty sections invisible
        for (section in ReminderSection.reminderSectionList) {
            if (section.getList().isEmpty()) {
                section.isVisible = false
                sectionAdapter.notifyDataSetChanged()
            }
        }
        //todo test if this shit actually works
        //sets a repeating alarm that updates the recycler view at 23:59:59 every night
        val updateIntent = Intent(applicationContext,UpdateReceiver::class.java)
        val updatePendingIntent = PendingIntent.getBroadcast(applicationContext,updateRequestCode,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        globalAlarmManager.setInexactRepeating(AlarmManager.RTC,todayCalendar.timeInMillis,AlarmManager.INTERVAL_DAY,updatePendingIntent)
        val calendar = Calendar.getInstance()

        //updates recycler view every 5 seconds
        fixedRateTimer("timer",false,0,5000) {
            this@ReminderActivity.runOnUiThread {
                sectionAdapter.notifyDataSetChanged()
                for (reminder in Reminder.todayList) {
                    Log.d(TAG,"Looking through today list")
                    //if reminder becomes overdue, move to overdue section
                    if (reminder.remindCalendar.timeInMillis < System.currentTimeMillis()) {
                        Log.d(TAG,"Found an overdue Reminder")
                        Reminder.todayList.remove(reminder)
                        Log.d(TAG,"Removing the Reminder")
                        if (Reminder.todayList.isEmpty()) {
                            Log.d(TAG,"Removing the Section")
                            val todaySection = ReminderSection.reminderSectionList[1]
                            todaySection.isVisible = false
                            sectionAdapter.notifyDataSetChanged()
                            Log.d(TAG,"Section has been removed")
                        }
                        reminder.list = Reminder.overdueList
                        reminder.insertInOrder(reminder.list,reminder)
                        reminder.section = ReminderSection.getReminderSection(reminder)
                    }
                }
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                scheduleFAB.show()
            } else {
                scheduleFAB.hide()
            }
        }

        scheduleFAB.setOnClickListener {
            val fragment = ScheduleReminderFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,fragment)
                .addToBackStack(null)
                .commit()
        }

        //create an item touch helper to allow for reminders to be able to be swiped
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is HeaderViewHolder) return
                //if user swipes right, delete reminder
                if (direction == ItemTouchHelper.RIGHT) {
                    swipeDeleteItem(viewHolder)
                    //if user swipes left, complete reminder
                } else if ( direction == ItemTouchHelper.LEFT) {
                    swipeCompleteItem(viewHolder)
                }
            }
            //allows for color and icons in the background when reminders are swiped
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean) {

                if (viewHolder is HeaderViewHolder) return
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
                    swipeBackground = ColorDrawable(Color.parseColor("#77dd77"))//sets swipe background to green
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    checkIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
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
        itemTouchHelper.attachToRecyclerView(recycler_view)
    }
    //creates menu on action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu,menu)
        return true
    }
    //starts activity depending on which option was selected
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            true
        }
        R.id.action_timer -> {
            startActivity(Intent(applicationContext, TimerActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    //starts edit reminder fragment when user clicks on a reminder in recycler view
    override fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int) {
        //get reminder that was clicked on
        val section: ReminderSection = sectionAdapter.getSection(sectionTitle) as ReminderSection
        val reminder: Reminder = section.getList()[sectionAdapter.getPositionInSection(itemPosition)]

        val editReminderFragment = EditReminderFragment()
        val args = Bundle()
        args.putParcelable("ReminderData",reminder.getReminderData())
        editReminderFragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,editReminderFragment)
            .addToBackStack(null)
            .commit()
    }

    //function that executes when the user swipes to delete a reminder
    fun swipeDeleteItem(viewHolder: RecyclerView.ViewHolder) {

        val reminderHolder: ReminderViewHolder = viewHolder as ReminderViewHolder
        val positionInAdapter: Int = reminderHolder.adapterPosition
        val section: ReminderSection = ReminderSection.getReminderSection(positionInAdapter)
        val positionInSection: Int = sectionAdapter.getPositionInSection(positionInAdapter)
        val reminderList: LinkedList<Reminder> = section.getList()
        val removedReminder: Reminder = reminderList[positionInSection]

        removedReminder.deleteReminder()
        //creates a snackbar indicating a reminder has been deleted, and shows the option to undo
        Snackbar.make(findViewById(R.id.main_coordinator_layout), "Bye-Bye " + removedReminder.text, Snackbar.LENGTH_LONG).setAction("Undo") {
            //readd reminder if undo is clicked
            removedReminder.reAddReminder()
        }.show()
    }

    fun swipeCompleteItem(viewHolder: RecyclerView.ViewHolder) {
        val reminderPositionInAdapter: Int = viewHolder.adapterPosition
        val section: ReminderSection = ReminderSection.getReminderSection(viewHolder.adapterPosition)
        val reminderPositionInSection : Int = sectionAdapter.getPositionInSection(reminderPositionInAdapter)
        val reminderList : LinkedList<Reminder> = section.getList()
        val removedReminder: Reminder = reminderList[reminderPositionInSection]
        val previousTime = removedReminder.remindCalendar.time

        removedReminder.deleteReminder()
        //if reminder is repeating, readd item with remind calendar incremented with the correct amount
        if (removedReminder.repeatVal != Reminder.REPEAT_NONE) {
            val calendar = removedReminder.remindCalendar
            //todo implement custom repeat
            when(removedReminder.repeatVal) {
                Reminder.REPEAT_DAILY -> { calendar.add(Calendar.DAY_OF_YEAR, 1) }
                Reminder.REPEAT_WEEKDAYS -> {
                    when (calendar.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.FRIDAY -> calendar.add(Calendar.DAY_OF_YEAR, 3)
                        Calendar.SATURDAY -> calendar.add(Calendar.DAY_OF_YEAR, 2)
                        else -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                Reminder.REPEAT_WEEKLY -> { calendar.add(Calendar.WEEK_OF_YEAR, 1) }
                Reminder.REPEAT_MONTHLY -> { calendar.add(Calendar.MONTH, 1) }
                Reminder.REPEAT_YEARLY -> { calendar.add(Calendar.YEAR,1) }
            }
            removedReminder.reAddReminder()
        }
        //creates snackbar indicating that a reminder has been completed, and shows the option to undo
        Snackbar.make(findViewById(R.id.main_coordinator_layout),"Completed " + removedReminder.text + " :)", Snackbar.LENGTH_LONG).setAction("Undo") {
            if (removedReminder.repeatVal != Reminder.REPEAT_NONE) {
                val updatedSection : ReminderSection = ReminderSection.getReminderSection(removedReminder)
                val updatedList : LinkedList<Reminder> = updatedSection.getList()
                val updatedCompletedPosition = updatedList.indexOf(removedReminder)
                /* if reminder is repeating, remove item and readd with remind calendar decremented
                the correct amount, else readd reminder normally */
                updatedList.removeAt(updatedCompletedPosition)
                sectionAdapter.notifyItemRemoved(updatedCompletedPosition)
                if (updatedSection.getList().isEmpty()) {
                    updatedSection.isVisible = false
                    sectionAdapter.notifyDataSetChanged()
                }
                if (removedReminder.repeatVal != Reminder.REPEAT_NONE) {
                    removedReminder.remindCalendar.time = previousTime
                }
            }
            removedReminder.reAddReminder()
        }.show()
    }
    //if reminders have already been loaded return true
    private fun isLoaded(): Boolean {
        for(section in ReminderSection.reminderSectionList) {
            if (!section.getList().isEmpty()) {
                return true
            }
        }
        return false
    }

    override fun onReminderEdited(
        newText: String,
        newRemindCalendar: Calendar,
        newRepeatVal: Int,
        newAutoSnoozeVal: Int,
        oldSectionTitle: String,
        oldPositionInSection: Int
    ) {
        val oldSection: ReminderSection = sectionAdapter.getSection(oldSectionTitle) as ReminderSection
        val oldReminder = oldSection.getList()[oldPositionInSection]
        //remove reminder
        oldReminder.deleteReminder()
        //create new reminder with new specifications
        Reminder(newText,newRemindCalendar,newRepeatVal,newAutoSnoozeVal,applicationContext)
    }
}
