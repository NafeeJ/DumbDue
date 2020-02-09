package com.kiwicorp.dumbdue

import android.app.Activity
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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity(), ReminderSection.ClickListener {
    private lateinit var deleteIcon: Drawable
    private lateinit var checkIcon: Drawable

    private lateinit var swipeBackground: ColorDrawable

    private val updateRequestCode: Int = 1230498

    companion object {
        //Shared Preferences Keys
        const val prefs: String = "Preferences"
        const val overdueListKey: String = "OverdueListKey"
        const val todayListKey: String = "TodayListKey"
        const val tomorrowListKey: String = "TomorrowListKey"
        const val next7DaysListKey: String = "Next7DaysListKey"
        const val futureListKey: String = "FutureListKey"
        const val globalRequestCodeKey: String = "GlobalRequestCodeKey"

        //custom request codes to handle editing reminders
        const val EDIT_REMINDER_REQUEST: Int = 2
        const val RESULT_DELETE = 6

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

            val sharedPreferences = context.getSharedPreferences(prefs, Context.MODE_PRIVATE)

            val myGson = Gson()
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            //put global request code as a json
            val requestCodeJson: String = myGson.toJson(Reminder.globalRequestCode)
            editor.putString(globalRequestCodeKey, requestCodeJson)
            //get json strings of reminder lists
            val overdueListJson: String = myGson.toJson(Reminder.overdueList)
            val todayListJson: String = myGson.toJson(Reminder.todayList)
            val tomorrowListJson: String = myGson.toJson(Reminder.tomorrowList)
            val next7DaysListJson: String = myGson.toJson(Reminder.next7daysList)
            val futureListJson: String = myGson.toJson(Reminder.futureList)
            //put reminder list strings into shared preferences
            editor.putString(overdueListKey,overdueListJson)
            editor.putString(todayListKey,todayListJson)
            editor.putString(tomorrowListKey,tomorrowListJson)
            editor.putString(next7DaysListKey,next7DaysListJson)
            editor.putString(futureListKey,futureListJson)
            //apply changes
            editor.apply()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        loadAll()

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sectionAdapter
        }
        //makes empty sections invisible
        for (section in ReminderSection.reminderSectionList) {
            if (section.getList().isEmpty()) {
                section.isVisible = false
            }
        }

        //sets a repeating alarm that updates the recycler view at 23:59:59 every night
        val updateIntent = Intent(applicationContext,UpdateReceiver::class.java)
        val updatePendingIntent = PendingIntent.getBroadcast(applicationContext,updateRequestCode,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        globalAlarmManager.setInexactRepeating(AlarmManager.RTC,todayCalendar.timeInMillis,AlarmManager.INTERVAL_DAY,updatePendingIntent)

        //updates recycler view every 5 seconds
        fixedRateTimer("timer",false,0,5000) {
            this@MainActivity.runOnUiThread {
                sectionAdapter.notifyDataSetChanged()
                for (reminder in Reminder.todayList) {
                    if (reminder.getRemindCalendar().timeInMillis < Calendar.getInstance().timeInMillis) {
                        //if reminder becomes overdue, move to overdue section
                        val index = Reminder.todayList.indexOf(reminder)
                        Reminder.todayList.remove(reminder)
                        reminder.deleteReminder()
                        sectionAdapter.notifyItemRemovedFromSection("Today",index)
                        Reminder(reminder.getText(),reminder.getRemindCalendar(),reminder.getRepeatVal(),applicationContext)
                        //remove section if empty
                        val section: ReminderSection = ReminderSection.getReminderSection(reminder)
                        if (section.getList().isEmpty()) {
                            section.isVisible = false
                            sectionAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        scheduleFAB.setOnClickListener {
            startActivity(Intent(applicationContext, ScheduleReminderActivity::class.java))
        }
        //create an item touch helper to allow for reminders to be able to be swiped
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //if user swipes right, delete reminder
                if (direction == ItemTouchHelper.RIGHT) {
                    swipeDeleteItem(viewHolder,findViewById(R.id.activity_main))
                    //if user swipes left, complete reminder
                } else if ( direction == ItemTouchHelper.LEFT) {
                    swipeCompleteItem(viewHolder,findViewById(R.id.activity_main))
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
                isCurrentlyActive: Boolean
            ) {
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

    private fun loadAll() {//load lists from shared preferences
        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val gson = Gson()

        val emptyListJson = gson.toJson(LinkedList<Reminder>())//an empty list in case lists haven't been stored
        //get reminder list json strings
        val overdueListJson = sharedPreferences.getString(overdueListKey, emptyListJson)
        val todayListJson = sharedPreferences.getString(todayListKey, emptyListJson)
        val tomorrowListJson = sharedPreferences.getString(tomorrowListKey, emptyListJson)
        val next7DaysJson = sharedPreferences.getString(next7DaysListKey, emptyListJson)
        val futureListJson = sharedPreferences.getString(futureListKey, emptyListJson)
        //gets type to allow gson to know what type of object it's retrieving
        val reminderListType = object : TypeToken<LinkedList<Reminder>>() {}.type
        //get lists
        val overDueListFromJson = gson.fromJson<LinkedList<Reminder>>(overdueListJson,reminderListType)
        val todayListFromJson = gson.fromJson<LinkedList<Reminder>>(todayListJson,reminderListType)
        val tomorrowListFromJson = gson.fromJson<LinkedList<Reminder>>(tomorrowListJson,reminderListType)
        val next7DaysListFromJson = gson.fromJson<LinkedList<Reminder>>(next7DaysJson,reminderListType)
        val futureListFromJson = gson.fromJson<LinkedList<Reminder>>(futureListJson,reminderListType)

        loadList(overDueListFromJson)
        loadList(todayListFromJson)
        loadList(tomorrowListFromJson)
        loadList(next7DaysListFromJson)
        loadList(futureListFromJson)
        //initializes global request code from shared preferences
        val requestCodeJson = sharedPreferences.getString(globalRequestCodeKey, gson.toJson(0))
        val intType = object : TypeToken<Int>() {}.type
        val indexFromJson = gson.fromJson<Int>(requestCodeJson,intType)
        Reminder.globalRequestCode = indexFromJson
    }
    //creates all reminders in the list
    private fun loadList(list: LinkedList<Reminder>) {
        for (reminder in list) {
            Reminder(
                reminder.getText(),
                reminder.getRemindCalendar(),
                reminder.getRepeatVal(),
                applicationContext
            )
        }
    }
    //starts edit reminder activity when user clicks on a reminder in recycler view
    override fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int) {
        //get reminder that was clicked on
        val section: ReminderSection = sectionAdapter.getSection(sectionTitle) as ReminderSection
        val reminder: Reminder = section.getList()[sectionAdapter.getPositionInSection(itemPosition)]
        //create intent to start edit reminder activity
        val editIntent = Intent(this,EditReminderActivity::class.java)
        //store reminder data to be later retrieved by the edit reminder activity
        editIntent.putExtra("ReminderData", reminder.getReminderData())
        //starts edit reminder activty
        startActivityForResult(editIntent,EDIT_REMINDER_REQUEST)
    }

    //executes based off of result of edit reminder activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check which request we're responding to
        if (requestCode == EDIT_REMINDER_REQUEST) {
            //if result is ok, edit reminder
            if (resultCode == Activity.RESULT_OK) {
                //gets reminder data from edit reminder intent
                val reminderToBeCreatedData: Reminder.ReminderData? = data?.getParcelableExtra("ReminderData")
                //remove reminder and create a new reminder with desired specifications
                if (reminderToBeCreatedData != null) {
                    val section: ReminderSection = sectionAdapter.getSection(reminderToBeCreatedData.sectionTitle) as ReminderSection
                    val reminderList: LinkedList<Reminder> = section.getList()
                    val reminder: Reminder = reminderList[reminderToBeCreatedData.indexInSection]
                    //remove reminder
                    reminderList.remove(reminder)
                    reminder.deleteReminder()
                    //create new reminder
                    Reminder(reminderToBeCreatedData.text,reminderToBeCreatedData.remindCalendar,reminderToBeCreatedData.repeatVal,applicationContext)
                }
            }
            //if result is to delete, delete reminder
            if (resultCode == RESULT_DELETE) {
                val reminderToBeDeletedData: Reminder.ReminderData? = data?.getParcelableExtra("ReminderData")
                //deletes reminder
                if (reminderToBeDeletedData != null) {
                    val indexInSection = reminderToBeDeletedData.indexInSection
                    val section: ReminderSection = sectionAdapter.getSection(reminderToBeDeletedData.sectionTitle) as ReminderSection
                    val list: LinkedList<Reminder> = section.getList()
                    val reminder: Reminder = list[indexInSection]
                    list.remove(reminder)
                    reminder.deleteReminder()
                    sectionAdapter.notifyItemRemovedFromSection(section,indexInSection)
                }
            }
        }
    }
    //function that executes when the user swipes to delete a reminder
    fun swipeDeleteItem(viewHolder: RecyclerView.ViewHolder, view: View) {

        val reminderHolder: ReminderViewHolder = viewHolder as ReminderViewHolder
        val reminderPositionInAdapter: Int = reminderHolder.adapterPosition
        val section: ReminderSection = ReminderSection.getReminderSection(reminderPositionInAdapter)
        val reminderPositionInSection : Int = sectionAdapter.getPositionInSection(reminderPositionInAdapter)
        val reminderList : LinkedList<Reminder> = section.getList()
        val removedReminder: Reminder = reminderList[reminderPositionInSection]

        removedReminder.deleteReminder()
        reminderList.remove(removedReminder)

        if (!reminderList.isEmpty()) {
            sectionAdapter.notifyItemRemovedFromSection(section,reminderPositionInSection)
        }
        //creates a snackbar indicating a reminder has been deleted, and shows the option to undo
        Snackbar.make(view, "Bye-Bye " + removedReminder.getText(), Snackbar.LENGTH_LONG).setAction("Undo") {
            //readd reminder if undo is clicked
            removedReminder.reAddReminder(reminderPositionInAdapter)
        }.show()
    }

    fun swipeCompleteItem(viewHolder: RecyclerView.ViewHolder, view: View) {
        val reminderHolder: ReminderViewHolder = viewHolder as ReminderViewHolder
        val reminderPositionInAdapter: Int = reminderHolder.adapterPosition
        val section: ReminderSection = ReminderSection.getReminderSection(reminderPositionInAdapter)
        val reminderPositionInSection : Int = sectionAdapter.getPositionInSection(reminderPositionInAdapter)
        val reminderList : LinkedList<Reminder> = section.getList()
        val removedReminder: Reminder = reminderList[reminderPositionInSection]

        removedReminder.deleteReminder()
        reminderList.remove(removedReminder)

        if (!reminderList.isEmpty()) {
            sectionAdapter.notifyItemRemovedFromSection(section,reminderPositionInSection)
        }

        //if reminder is repeating, readd item with remind calendar incremented with the correct amount
        when(removedReminder.getRepeatVal()) {
            Reminder.REPEAT_DAILY -> {
                removedReminder.getRemindCalendar().add(Calendar.DAY_OF_YEAR, 1)
                removedReminder.reAddReminder(reminderPositionInAdapter)
            }
            Reminder.REPEAT_WEEKLY -> {
                removedReminder.getRemindCalendar().add(Calendar.WEEK_OF_YEAR, 1)
                removedReminder.reAddReminder(reminderPositionInAdapter)
            }
            Reminder.REPEAT_MONTHLY -> {
                removedReminder.getRemindCalendar().add(Calendar.MONTH, 1)
                removedReminder.reAddReminder(reminderPositionInAdapter)
            }
        }
        //creates snackbar indicating that a reminder has been completed, and shows the option to undo
        Snackbar.make(view,"Completed " + removedReminder.getText() + " :)", Snackbar.LENGTH_LONG).setAction("Undo") {
            //executes if undo is clicked
            val updatedSection : ReminderSection = ReminderSection.getReminderSection(removedReminder)
            val updatedList : LinkedList<Reminder> = updatedSection.getList()
            val updatedCompletedPosition = updatedList.indexOf(removedReminder)
            /* if reminder is repeating, remove item and readd with remind calendar decremented
            the correct amount, else readd reminder normally */
            when(removedReminder.getRepeatVal()) {
                Reminder.REPEAT_DAILY -> {
                    updatedList.removeAt(updatedCompletedPosition)
                    sectionAdapter.notifyItemRemoved(updatedCompletedPosition)

                    removedReminder.getRemindCalendar().add(Calendar.DAY_OF_YEAR, -1)
                    removedReminder.reAddReminder(reminderPositionInAdapter)
                }
                Reminder.REPEAT_WEEKLY -> {
                    updatedList.removeAt(updatedCompletedPosition)
                    sectionAdapter.notifyItemRemoved(updatedCompletedPosition)

                    removedReminder.getRemindCalendar().add(Calendar.WEEK_OF_YEAR, -1)
                    removedReminder.reAddReminder(reminderPositionInAdapter)
                }
                Reminder.REPEAT_MONTHLY -> {
                    updatedList.removeAt(updatedCompletedPosition)
                    sectionAdapter.notifyItemRemoved(updatedCompletedPosition)

                    removedReminder.getRemindCalendar().add(Calendar.MONTH, -1)
                    removedReminder.reAddReminder(reminderPositionInAdapter)
                }
                else -> removedReminder.reAddReminder(reminderPositionInAdapter)
            }
        }.show()
    }

}
