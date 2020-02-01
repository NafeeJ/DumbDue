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

    private val EDIT_REMINDER_REQUEST: Int = 2

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

        val todayCalendar: Calendar = Calendar.getInstance()//calendar with date at 11:59:59 pm today
        val tomorrowCalendar: Calendar = Calendar.getInstance()//calendar with date at 11:59:59 pm tomorrow
        val next7daysCalendar: Calendar = Calendar.getInstance()//calendar with date at 11:59:59 pm in 7 days

        lateinit var globalAlarmManager: AlarmManager

        val sectionAdapter = SectionedRecyclerViewAdapter()

        const val RESULT_DELETE = 6//custom result code for delete

        var notificationID = 0 //used to keep notifications unique thus allowing notifications to stack

        fun daySuffixFinder(calendar: Calendar): String {
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
            if (dayOfMonth.rem(10) == 1 && dayOfMonth != 11) {
                return "st"
            } else if (dayOfMonth.rem(10) == 2 && dayOfMonth != 12) {
                return "nd"
            } else if (dayOfMonth.rem(10) == 3 && dayOfMonth != 13) {
                return "rd"
            } else {
                return "th"
            }
        }
        fun findTimeFromNowMins(calendar: Calendar): Int {
            //Get time difference of each time unit with fromNowMins as variable to use as the standard
            var fromNowMins: Int = calendar.get(Calendar.MINUTE) - Calendar.getInstance().get(Calendar.MINUTE)
            val fromNowHours: Int = calendar.get(Calendar.HOUR_OF_DAY) - Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val fromNowDays: Int = calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val fromNowYears: Int = calendar.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR)

            fromNowMins += (fromNowHours * 60) + (fromNowDays * 24 * 60) + (fromNowYears * 525600) //Add the other time unit differences, in minutes, to fromNowMins
            return fromNowMins
        }
        fun saveAll(context: Context) {//saves the list and global request code

            val sharedPreferences = context.getSharedPreferences(prefs, Context.MODE_PRIVATE)

            val myGson = Gson()
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            //put global request code as a json
            val requestCodeJson: String = myGson.toJson(Reminder.globalRequestCode)
            editor.putString(globalRequestCodeKey, requestCodeJson)
            //puts reminder list as a json
            val overdueListJson: String = myGson.toJson(Reminder.overdueList)
            val todayListJson: String = myGson.toJson(Reminder.todayList)
            val tomorrowListJson: String = myGson.toJson(Reminder.tomorrowList)
            val next7DaysListJson: String = myGson.toJson(Reminder.next7daysList)
            val futureListJson: String = myGson.toJson(Reminder.futureList)

            editor.putString(overdueListKey,overdueListJson)
            editor.putString(todayListKey,todayListJson)
            editor.putString(tomorrowListKey,tomorrowListJson)
            editor.putString(next7DaysListKey,next7DaysListJson)
            editor.putString(futureListKey,futureListJson)

            editor.apply()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todayCalendar.set(Calendar.MILLISECOND, 59)
        todayCalendar.set(Calendar.SECOND,59)
        todayCalendar.set(Calendar.MINUTE, 59)
        todayCalendar.set(Calendar.HOUR_OF_DAY,23)

        tomorrowCalendar.set(Calendar.MILLISECOND, 59)
        tomorrowCalendar.set(Calendar.SECOND,59)
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY,23)
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR,1)

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

        ReminderSection.reminderSectionList.add(ReminderSection("Overdue",Reminder.overdueList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Today",Reminder.todayList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Tomorrow",Reminder.tomorrowList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Next 7 Days",Reminder.next7daysList,this))
        ReminderSection.reminderSectionList.add(ReminderSection("Future", Reminder.futureList,this))

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

        for (section in ReminderSection.reminderSectionList) {
            if (section.getList().isEmpty()) {
                section.isVisible = false;
            }
        }

        //updates the recycler view every night at 11:59:59 AM
        val updateIntent = Intent(applicationContext,UpdateReceiver::class.java)
        val updatePendingIntent = PendingIntent.getBroadcast(applicationContext,updateRequestCode,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        globalAlarmManager.setInexactRepeating(AlarmManager.RTC,todayCalendar.timeInMillis,AlarmManager.INTERVAL_DAY,updatePendingIntent)

        //updates recycler view every 5 seconds
        fixedRateTimer("timer",false,0,5000) {
            this@MainActivity.runOnUiThread {
                sectionAdapter.notifyDataSetChanged()
                for (reminder in Reminder.todayList) {
                    if (reminder.getRemindCalendar().timeInMillis < Calendar.getInstance().timeInMillis) {
                        val index = Reminder.todayList.indexOf(reminder)
                        Reminder.todayList.remove(reminder)
                        reminder.deleteReminder()
                        sectionAdapter.notifyItemRemovedFromSection("Today",index)
                        Reminder(reminder.getText(),reminder.getRemindCalendar(),reminder.getRepeatVal(),applicationContext)
                    }
                }
            }
        }

        scheduleFAB.setOnClickListener {
            startActivity(Intent(applicationContext, ScheduleReminderActivity::class.java))
        }

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {//if user swipes right, deleteReminder reminder
                    swipeDeleteItem(viewHolder,findViewById(R.id.activity_main))
                } else if ( direction == ItemTouchHelper.LEFT) {//if user swipes left, complete reminder
                    swipeCompleteItem(viewHolder,findViewById(R.id.activity_main))
                }

            }

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

                    swipeBackground.draw(c)
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

                    swipeBackground.draw(c)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            true
        }
//        R.id.action_timer -> {
//            startActivity(Intent(applicationContext, TimerActivity::class.java))
//            true
//        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun loadAll() {//load list from shared preferences
        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val gson = Gson()

        val emptyListJson = gson.toJson(LinkedList<Reminder>())//an empty list in case lists haven't been stored

        val overdueListJson = sharedPreferences.getString(overdueListKey, emptyListJson)
        val todayListJson = sharedPreferences.getString(todayListKey, emptyListJson)
        val tomorrowListJson = sharedPreferences.getString(tomorrowListKey, emptyListJson)
        val next7DaysJson = sharedPreferences.getString(next7DaysListKey, emptyListJson)
        val futureListJson = sharedPreferences.getString(futureListKey, emptyListJson)

        val reminderListType = object : TypeToken<LinkedList<Reminder>>() {}.type

        val overDueListFromJson = gson.fromJson<LinkedList<Reminder>>(overdueListJson,reminderListType)
        val todayListFromJson = gson.fromJson<LinkedList<Reminder>>(todayListJson,reminderListType)
        val tomorrowListFromJson = gson.fromJson<LinkedList<Reminder>>(tomorrowListJson,reminderListType)
        val next7DaysListFromJson = gson.fromJson<LinkedList<Reminder>>(next7DaysJson,reminderListType)
        val futureListFromJson = gson.fromJson<LinkedList<Reminder>>(futureListJson,reminderListType)

        loadLists(overDueListFromJson)
        loadLists(todayListFromJson)
        loadLists(tomorrowListFromJson)
        loadLists(next7DaysListFromJson)
        loadLists(futureListFromJson)

        val requestCodeJson = sharedPreferences.getString(globalRequestCodeKey, gson.toJson(0))
        val intType = object : TypeToken<Int>() {}.type
        val indexFromJson = gson.fromJson<Int>(requestCodeJson,intType)
        Reminder.globalRequestCode = indexFromJson
    }

    private fun loadLists(list: LinkedList<Reminder>) {
        for (reminder in list) {
            Reminder(
                reminder.getText(),
                reminder.getRemindCalendar(),
                reminder.getRepeatVal(),
                applicationContext
            )
        }
    }
    override fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int) {
        val section: ReminderSection = sectionAdapter.getSection(sectionTitle) as ReminderSection
        val reminder: Reminder = section.getList()[sectionAdapter.getPositionInSection(itemPosition)]
        val intent = Intent(this,EditReminderActivity::class.java)
        intent.putExtra("ReminderData", reminder.getReminderData())
        startActivityForResult(intent,EDIT_REMINDER_REQUEST)
    }

    //on activity result for edit reminder request activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check which request we're responding to
        if (requestCode == EDIT_REMINDER_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                val reminderToBeCreatedData: Reminder.ReminderData? = data?.getParcelableExtra("ReminderData")
                //remove reminder and create a new reminder with desired specifications
                if (reminderToBeCreatedData != null) {
                    val section: ReminderSection = sectionAdapter.getSection(reminderToBeCreatedData.sectionTitle) as ReminderSection
                    val reminderList: LinkedList<Reminder> = section.getList()
                    val reminder: Reminder = reminderList[reminderToBeCreatedData.indexInSection]
                    reminderList.remove(reminder)
                    reminder.deleteReminder()
                    Reminder(reminderToBeCreatedData.text,reminderToBeCreatedData.remindCalendar,reminderToBeCreatedData.repeatVal,applicationContext)
                }
            }

            if (resultCode == RESULT_DELETE) {
                val reminderToBeDeletedData: Reminder.ReminderData? = data?.getParcelableExtra("ReminderData")
                //deletes reminder
                if (reminderToBeDeletedData != null) {
                    val indexInSection = reminderToBeDeletedData.indexInSection
                    val section: ReminderSection = sectionAdapter.getSection(reminderToBeDeletedData.sectionTitle) as ReminderSection
                    val reminderList: LinkedList<Reminder> = section.getList()
                    val reminder: Reminder = reminderList[indexInSection]
                    reminderList.remove(reminder)
                    reminder.deleteReminder()
                    sectionAdapter.notifyItemRemovedFromSection(section,indexInSection)
                }
            }
        }
    }

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

        Snackbar.make(view, "Bye-Bye " + removedReminder.getText(), Snackbar.LENGTH_LONG).setAction("Undo") {
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

        Snackbar.make(view,"Completed " + removedReminder.getText() + " :)", Snackbar.LENGTH_LONG).setAction("Undo") {
            val updatedSection : ReminderSection = ReminderSection.getReminderSection(removedReminder)
            val updatedList : LinkedList<Reminder> = updatedSection.getList()
            val updatedCompletedPosition = updatedList.indexOf(removedReminder)

            //if reminder is repeating, remove item and readd with remind calendar decremented with the correct amount
            //else readd reminder normally
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
