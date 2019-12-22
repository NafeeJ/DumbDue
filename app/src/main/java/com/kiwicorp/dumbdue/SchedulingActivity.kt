package com.kiwicorp.dumbdue

import android.os.Bundle
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.scheduling_activity_layout.*
import java.util.*

class SchedulingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scheduling_activity_layout)

        val dateText: TextView = findViewById(R.id.dateTextView)
        dateText.text = Calendar.getInstance().time.toString()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width: Int = (displayMetrics.widthPixels * 0.9).toInt()
        val height: Int = displayMetrics.heightPixels / 3

        window.setLayout(width,height)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER

        window.attributes = params
    }
}