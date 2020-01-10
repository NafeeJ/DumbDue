package com.kiwicorp.dumbdue


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.recycler_view
import java.util.*
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity() {
    companion object {
        var reminderList: ArrayList<Reminder> = ArrayList()

        //Shared Preferences Keys
        val Prefs: String = "Preferences"
        val Reminders: String = "RemindersKey"

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
    }

    private lateinit var reminderAdapter: ReminderRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Load Shared Preferences into Reminders List
        val sharedPreferences = getSharedPreferences(Prefs, Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String = sharedPreferences.getString(Reminders, "").toString()

        if (json.isNotEmpty()) {
            val reminderListType = object : TypeToken<ArrayList<Reminder>>() {}.type
            val listFromJson = gson.fromJson<ArrayList<Reminder>>(json,reminderListType)
            reminderList = listFromJson
        }
        
        val scheduleFAB: FloatingActionButton = findViewById(R.id.scheduleFAB)

        initRecyclerView()
        addDataSet()

        val saveFAB: FloatingActionButton = findViewById(R.id.floatingActionButton)

        scheduleFAB.setOnClickListener {
            startActivity(Intent(applicationContext, SchedulingActivity::class.java))
        }

        saveFAB.setOnClickListener{
            //Save Reminder List to Shared Preferences
            val mygson = Gson()
            val myjson: String = mygson.toJson(reminderList)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(Reminders, myjson)
            editor.apply()
        }
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            reminderAdapter = ReminderRecyclerAdapter()
            adapter = reminderAdapter
        }
    }

    private fun addDataSet() { reminderAdapter.submitList(reminderList) }
}

