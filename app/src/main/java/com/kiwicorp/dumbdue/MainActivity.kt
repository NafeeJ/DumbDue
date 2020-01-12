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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.recycler_view
import java.util.*
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    private lateinit var deleteIcon: Drawable
    private lateinit var checkIcon: Drawable

    private lateinit var swipeBackground: ColorDrawable

    companion object {
        //Shared Preferences Keys
        val prefs: String = "Preferences"
        val remindersListKey: String = "RemindersListKey"
        val globalRequestCodeKey: String = "GlobalRequestCodeKey"

        val reminderAdapter = ReminderRecyclerAdapter()

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

        fun findTimeFromNowString(timeInMins: Int): String { //returns a string with absolute value of time from now and its correct unit
            val absTime = timeInMins.absoluteValue

            if (absTime == 0) { return "0 Minutes" } //less than 1 minute
            else if (absTime == 1) { return absTime.toString().plus(" Minute") } //equal to 1 minute
            else if (absTime < 60) { return absTime.toString().plus(" Minutes") } //less than 1 hour
            else if ((absTime / 60) == 1) { return (absTime / 60).toString().plus(" Hour") } //equal to 1 hour
            else if ((absTime / 60) < 24 ) { return (absTime / 60).toString().plus(" Hours") } //less than 1 day
            else if ((absTime / 60 / 24) == 1) { return (absTime / 60 / 24).toString().plus(" Day") } //equal to 1 day
            else if ((absTime / 60 / 24) < 7) { return (absTime / 60 / 24).toString().plus(" Days") } //less than 1 week
            else if ((absTime / 60 / 24 / 7) == 1) { return (absTime / 60 / 24 / 7).toString().plus(" Week") } //equal to 1 week
            else if ((absTime / 60 / 24 / 7) < 4) { return (absTime / 60 / 24 / 7).toString().plus(" Weeks") } //less than 1 month
            else if ((absTime / 60 / 24 / 7 / 4) == 1) { return (absTime / 60 / 24 / 7 / 4).toString().plus(" Month") } //equal to 1 month
            else if ((absTime / 60 / 24 / 7 / 4) < 12) { return (absTime / 60 / 24 / 7 / 4).toString().plus(" Months") } //less than one year
            else if ((absTime / 60 / 24 / 7 / 4 / 12) == 1) { return (absTime / 60 / 24 / 7 / 4 / 12).toString().plus(" Year") } //equal to 1 year
            else return (absTime / 60 / 24 / 7 / 4 / 12).toString().plus(" Years")
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
        fun saveAll(context: Context) {

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
        
        val scheduleFAB: FloatingActionButton = findViewById(R.id.scheduleFAB)
        val reminderFAB: FloatingActionButton = findViewById(R.id.reminderFAB)

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete_white) as Drawable
        checkIcon = ContextCompat.getDrawable(this,R.drawable.check_white) as Drawable

        loadAll()
        initRecyclerView()
        addDataSet()

        scheduleFAB.setOnClickListener {
            startActivity(Intent(applicationContext, SchedulingActivity::class.java))
        }

        reminderFAB.setOnClickListener{
            val requestCode = Reminder.globalRequestCode++
            val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val interMediateReceiverIntent = Intent(applicationContext,IntermediateReceiver::class.java)
            val intermediateReceiverPendingIntent = PendingIntent.getBroadcast(applicationContext,requestCode,interMediateReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
            val intermediateReceiver = IntermediateReceiver(requestCode)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,Calendar.getInstance().timeInMillis + 10000,intermediateReceiverPendingIntent)
        }

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {//if user swipes right, deleteReminder reminder
                    reminderAdapter.deleteItem(viewHolder,findViewById(R.id.activity_main))
                } else if ( direction == ItemTouchHelper.LEFT) {//if user swipes left, complete reminder
                    reminderAdapter.completeItem(viewHolder,findViewById(R.id.activity_main))
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

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = reminderAdapter
        }
    }

    private fun addDataSet() { reminderAdapter.submitList(Reminder.reminderList) }

    private fun loadAll() {//load list from shared preferences
        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val gson = Gson()

        val emptyListJson = gson.toJson(LinkedList<Reminder>())//creates an empty list for when the ReminderList has not been initialized yet
        val listJson = sharedPreferences.getString(remindersListKey, emptyListJson)
        val reminderListType = object : TypeToken<LinkedList<Reminder>>() {}.type
        val listFromJson = gson.fromJson<LinkedList<Reminder>>(listJson,reminderListType)
        Reminder.reminderList = listFromJson

        val requestCodeJson = sharedPreferences.getString(globalRequestCodeKey, gson.toJson(0))
        val intType = object : TypeToken<Int>() {}.type
        val indexFromJson = gson.fromJson<Int>(requestCodeJson,intType)
        Reminder.globalRequestCode = indexFromJson
    }
}
