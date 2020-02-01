package com.kiwicorp.dumbdue

import android.app.Activity
import android.app.AlarmManager
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
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), ReminderSection.ClickListener {
    private lateinit var deleteIcon: Drawable
    private lateinit var checkIcon: Drawable

    private lateinit var swipeBackground: ColorDrawable

    private val EDIT_REMINDER_REQUEST: Int = 2

    companion object {
        //Shared Preferences Keys
        const val prefs: String = "Preferences"
        const val remindersListKey: String = "RemindersListKey"
        const val globalRequestCodeKey: String = "GlobalRequestCodeKey"

        val todayCalendar: Calendar = Calendar.getInstance()//calendar with date at 11:59:59 pm today
        val tomorrowCalendar: Calendar = Calendar.getInstance()//calendar with date at 11:59:59 pm tomorrow
        val next7daysCalendar: Calendar = Calendar.getInstance()//calendar with date at 11:59:59 pm in 7 days

        lateinit var globalAlarmManager: AlarmManager

        val sectionAdapter = ReminderSectionedAdapter()

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
            val listJson: String = myGson.toJson(Reminder.reminderList)
            editor.putString(remindersListKey, listJson)

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

//        loadAll()

        sectionAdapter.addSection("Overdue",ReminderSection("Overdue",Reminder.overdueList,this))
        sectionAdapter.addSection("Today",ReminderSection("Today",Reminder.todayList,this))
        sectionAdapter.addSection("Tomorrow",ReminderSection("Tomorrow",Reminder.tomorrowList,this))
        sectionAdapter.addSection("Next 7 Days",ReminderSection("Next 7 Days",Reminder.next7daysList,this))
        sectionAdapter.addSection("Future",ReminderSection("Future", Reminder.futureList,this))

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sectionAdapter
        }
//        //updates recycler view every 5 seconds
//        fixedRateTimer("timer",false,0,5000) {
//            this@MainActivity.runOnUiThread {
//                reminderRecyclerAdapter.notifyDataSetChanged()
//            }
//        }

        scheduleFAB.setOnClickListener {
            startActivity(Intent(applicationContext, ScheduleReminderActivity::class.java))
        }

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {//if user swipes right, deleteReminder reminder
                    sectionAdapter.swipeDeleteItem(viewHolder,findViewById(R.id.activity_main))
                } else if ( direction == ItemTouchHelper.LEFT) {//if user swipes left, complete reminder
                    sectionAdapter.swipeCompleteItem(viewHolder,findViewById(R.id.activity_main))
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

        val emptyListJson = gson.toJson(LinkedList<Reminder>())//creates an empty list for when the ReminderList has not been initialized yet
        val listJson = sharedPreferences.getString(remindersListKey, emptyListJson)
        val reminderListType = object : TypeToken<LinkedList<Reminder>>() {}.type
        val listFromJson = gson.fromJson<LinkedList<Reminder>>(listJson,reminderListType)
        Reminder.reminderList = listFromJson

        for (reminder in Reminder.reminderList) {
            reminder.setNotifications(applicationContext)
        }

        val requestCodeJson = sharedPreferences.getString(globalRequestCodeKey, gson.toJson(0))
        val intType = object : TypeToken<Int>() {}.type
        val indexFromJson = gson.fromJson<Int>(requestCodeJson,intType)
        Reminder.globalRequestCode = indexFromJson
    }

    override fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int) {
        Toast.makeText(applicationContext,String.format("Clicked on position #%s of Section %s",sectionAdapter.getPositionInSection(itemPosition),sectionTitle),Toast.LENGTH_SHORT).show()
//        val reminder: Reminder = Reminder.reminderList[position]
//        val intent = Intent(this,EditReminderActivity::class.java)
//        intent.putExtra("ReminderData", reminder.getReminderData())
//        startActivityForResult(intent,EDIT_REMINDER_REQUEST)
    }

    //on activity result for edit reminder request activity
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        //check which request we're responding to
//        if (requestCode == EDIT_REMINDER_REQUEST) {
//
//            if (resultCode == Activity.RESULT_OK) {
//                val newData: Reminder.ReminderData? = data?.getParcelableExtra("ReminderData")
//                //remove reminder and create a new reminder with desired specifications
//                if (newData != null) {
//                    val reminder: Reminder = Reminder.reminderList[newData.index]
//                    reminderRecyclerAdapter.removeItem(reminder)
//                    Reminder(newData.text,newData.remindCalendar,newData.repeatVal,applicationContext)
//                }
//            }
//
//            if (resultCode == RESULT_DELETE) {
//                val deleteData: Reminder.ReminderData? = data?.getParcelableExtra("ReminderData")
//                //deletes reminder
//                if (deleteData != null) {
//                    val reminder: Reminder = Reminder.reminderList[deleteData.index]
//                    reminderRecyclerAdapter.deleteItem(reminder,findViewById(R.id.activity_main))
//                }
//            }
//        }
//    }


}
