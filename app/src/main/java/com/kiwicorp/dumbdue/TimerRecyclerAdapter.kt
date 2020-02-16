package com.kiwicorp.dumbdue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_timer.view.*

class TimerRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var timers: MutableList<DumbTimer>
    private lateinit var onTimerListener: OnTimerListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TimerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_timer, parent, false), onTimerListener)
    }

    override fun getItemCount(): Int {
        return timers.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TimerViewHolder -> {
                holder.bind(timers[position])
            }
        }
    }

    class TimerViewHolder(itemView: View, onTimerListener: OnTimerListener): RecyclerView.ViewHolder(itemView) , View.OnClickListener  {

        private val timerTitle: TextView = itemView.timerTitle
        private val timerCountdown: TextView = itemView.timerCountdown
        private val timerButton: ImageButton = itemView.timerButton

        init { itemView.setOnClickListener(this) }

        fun bind(timer: DumbTimer) {
            timerTitle.text = timer.getTitle()

            timerButton.setOnClickListener {

            }
        }

        override fun onClick(view: View?) {}
    }
}

interface OnTimerListener {
    fun onTimerClick(position: Int)
}
