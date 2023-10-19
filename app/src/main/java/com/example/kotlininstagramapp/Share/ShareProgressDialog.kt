package com.example.kotlininstagramapp.Share

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.R

class ShareProgressDialog : DialogFragment() {
    lateinit var tvProgress: TextView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.e("","ShareProgressDialog ÇALIŞTI")
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_share_progress_dialog)
        dialog.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        dialog.setCancelable(false)
        tvProgress = dialog.findViewById(R.id.tv_progress)
        return dialog
    }
}