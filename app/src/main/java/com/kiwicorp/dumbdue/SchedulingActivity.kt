package com.kiwicorp.dumbdue

import android.os.Bundle
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.scheduling_activity_layout.*
import java.util.*

class SchedulingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scheduling_activity_layout)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width: Int = (displayMetrics.widthPixels * 0.9).toInt()
        val height: Int = displayMetrics.heightPixels / 3
        window.setLayout(width,height)

        val buttonPlus10min: Button = findViewById(R.id.plus10minbutton)
        val buttonMinus10min: Button = findViewById(R.id.minus10minbutton)
        val buttonPlus1hr: Button = findViewById(R.id.plus1hrbutton)
        val buttonMinus1hr: Button = findViewById(R.id.minus1hrbutton)
        val buttonPlus3hr: Button = findViewById(R.id.plus3hrbutton)
        val buttonMinus3hr: Button = findViewById(R.id.minus3hrbutton)
        val buttonPlus1day: Button = findViewById(R.id.plus1daybutton)
        val buttonMinus1day: Button = findViewById(R.id.minus1daybutton)

        val dateText: TextView = findViewById(R.id.dateTextView)
        dateText.text = Calendar.getInstance().time.toString()

        //sets width and height of all buttons
        val buttons = listOf(buttonPlus10min,buttonMinus10min,buttonPlus1hr,buttonMinus1hr,buttonPlus3hr,buttonMinus3hr,buttonPlus1day,buttonMinus1day)
        for (button in buttons) {
            button.width = width / 4
            button.height = height / 5
        }
    }
}