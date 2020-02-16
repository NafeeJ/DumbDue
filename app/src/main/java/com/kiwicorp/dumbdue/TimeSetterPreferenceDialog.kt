package com.kiwicorp.dumbdue

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.DialogFragment

class TimeSetterPreferenceDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setPositiveButton("Done", { _, _ -> dismiss() })
        builder.setView(R.layout.layout_dialog_time_setter_button)
        return builder.create()
    }
}